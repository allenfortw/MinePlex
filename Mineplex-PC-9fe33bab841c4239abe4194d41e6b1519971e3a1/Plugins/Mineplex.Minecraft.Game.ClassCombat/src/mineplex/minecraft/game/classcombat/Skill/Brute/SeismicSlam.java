package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;

public class SeismicSlam extends SkillActive
{
	private HashMap<LivingEntity, Long> _live = new HashMap<LivingEntity, Long>();
	private HashMap<LivingEntity, Double> _height = new HashMap<LivingEntity, Double>();

	public SeismicSlam(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy, int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType,
				cost, levels,
				energy, energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);

		SetDesc(new String[] 
				{
				"Jump up and slam back into the ground.",
				"Players within 6 Blocks take up to",
				"6 damage and are thrown into the air.",
				"",
				"You receive Slow 2 for 6 seconds."
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		Vector vec = player.getLocation().getDirection();
		if (vec.getY() < 0)
			vec.setY(vec.getY() * -1);
		
		UtilAction.velocity(player, vec, 0.6, false, 0, 0.6, 0.6, true);

		//Record
		_live.put(player, System.currentTimeMillis());
		_height.put(player, player.getLocation().getY());

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
	}

	@EventHandler
	public void Slam(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player player : GetUsers())
		{
			if (!UtilEnt.isGrounded(player))
				continue;

			if (!_live.containsKey(player))
				continue;

			int level = GetLevel(player);
			if (level == 0)		continue;

			if (!UtilTime.elapsed(_live.get(player), 1000))  
				continue;

			_live.remove(player);

			//Bonus
			double mult = 1;
			if (_height.containsKey(player))
			{
				mult += (_height.remove(player) - player.getLocation().getY()) / 20;
				mult = Math.min(mult, 2);
				UtilPlayer.message(player, F.main(GetClassType().name(), 
							GetName() + ": " + F.elem(((int)((mult) * 100)) + "% Effectiveness")));
			}
			
			
			//Action
			int damage = 6;
			double range = (6) * mult;
			HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(player.getLocation(), range);
			for (LivingEntity cur : targets.keySet())
			{
				if (cur.equals(player))
					continue;

				if (!UtilEnt.isGrounded(player))
					continue;

				//Damage Event
				Factory.Damage().NewDamageEvent(cur, player, null, 
						DamageCause.CUSTOM, damage * targets.get(cur) + 0.5, false, true, false,
						player.getName(), GetName());	

				//Velocity
				UtilAction.velocity(cur, 
						UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector()), 
						1.8 * targets.get(cur) * mult, true, 0, 0.4 + 1.0 * targets.get(cur) * mult, 1.6 * mult, true);
				
				//Condition
				Factory.Condition().Factory().Falling(GetName(), cur, player, 10, false, true);

				//Inform
				if (cur instanceof Player)
					UtilPlayer.message((Player)cur, F.main(GetClassType().name(), F.name(player.getName()) +" hit you with " + F.skill(GetName(level)) + "."));	
			}
			
			//Slow
			Factory.Condition().Factory().Slow(GetName(), player, player, 6, 1, false, true, false, true);

			//Effect
			player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOOD, 2f, 0.2f);
			for (Block cur : UtilBlock.getInRadius(player.getLocation(), 4d).keySet())
				if (UtilBlock.airFoliage(cur.getRelative(BlockFace.UP)) && !UtilBlock.airFoliage(cur))
					cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, cur.getTypeId());
			
			//Event
			UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(player, GetName(), ClassType.Brute, targets.keySet()));
		}	
	}

	@Override
	public void Reset(Player player) 
	{
		_live.remove(player);
		_height.remove(player);
	}
}
