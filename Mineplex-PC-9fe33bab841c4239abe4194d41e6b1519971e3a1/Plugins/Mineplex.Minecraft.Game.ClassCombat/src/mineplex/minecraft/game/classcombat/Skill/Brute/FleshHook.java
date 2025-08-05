package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;

public class FleshHook extends SkillActive implements IThrown
{
	private WeakHashMap<Player, Integer> _charge = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap<Player, Long>();

	public FleshHook(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Hold Block to charge Flesh Hook.",
				"Release Block to release it.",
				"",
				"If Flesh Hook hits a player, it",
				"deals up to 12 damage, and rips them",
				"towards you with high velocity.",
				});
	}
	
	@Override
	public String GetEnergyString()
	{
		return "Energy: 20 + (5 per 20% Strength)";
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
		//Start Charge 
		_charge.put(player, 0);
		_chargeLast.put(player, System.currentTimeMillis());
	}

	@EventHandler
	public void ChargeRelease(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			//Not Charging
			if (!_charge.containsKey(cur))
				continue;

			//Level
			int level = GetLevel(cur);
			if (level == 0)			return;

			//Add Charge
			if (cur.isBlocking())
			{
				//Max Charge
				if (_charge.get(cur) >= 4)
					continue;

				//Charge Interval
				if (!UtilTime.elapsed(_chargeLast.get(cur), 400))
					continue;
				
				//Energy
				if (!Factory.Energy().Use(cur, GetName(), 5, true, false))
					continue;

				//Increase Charge
				_charge.put(cur, _charge.get(cur) + 1);
				_chargeLast.put(cur, System.currentTimeMillis());

				//Inform
				UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem("+" + (_charge.get(cur) * 25) + "% Strength")));

				//Effect
				for (int i=_charge.get(cur) ; i>0 ; i--)
					cur.playEffect(cur.getLocation(), Effect.CLICK2, 0);
			}

			//Release Charge
			else
			{
				double base = 0.8;

				//Action
				Item item = cur.getWorld().dropItem(cur.getEyeLocation().add(cur.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(131));
				UtilAction.velocity(item, cur.getLocation().getDirection(), 
						base + (_charge.remove(cur) * (0.25*base)), false, 0, 0.2, 20, false);
				 
				Factory.Projectile().AddThrow(item, cur, this, -1, true, true, true, 
						Sound.FIRE_IGNITE, 1.4f, 0.8f, null, 0, UpdateType.TICK, 1.5d);

				//Inform
				UtilPlayer.message(cur, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));

				//Effect
				item.getWorld().playSound(item.getLocation(), Sound.IRONGOLEM_THROW, 2f, 0.8f);
				
			}	
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_charge.remove(player);
		_chargeLast.remove(player);
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{
		//Remove
		double velocity = data.GetThrown().getVelocity().length();
		data.GetThrown().remove();

		if (!(data.GetThrower() instanceof Player))
			return;

		Player player = (Player)data.GetThrower();

		//Level
		int level = GetLevel(player);
		if (level == 0)				return;

		if (target == null)
			return;

		//Pull
		UtilAction.velocity(target, 
				UtilAlg.getTrajectory(target.getLocation(), player.getLocation()), 
				2, false, 0, 0.8, 1.5, true);
		
		//Condition
		Factory.Condition().Factory().Falling(GetName(), target, player, 10, false, true);

		//Damage Event
		Factory.Damage().NewDamageEvent(target, player, null, 
				DamageCause.CUSTOM, velocity * 8, false, true, false,
				player.getName(), GetName());
		

		//Event
		if (target != null)
			UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(player, GetName(), ClassType.Brute, target));
		
		//Inform
		UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " pulled you with " + F.skill(GetName(level)) + "."));
	}

	@Override
	public void Idle(ProjectileUser data) 
	{
		//Remove
		data.GetThrown().remove();
	}

	@Override
	public void Expire(ProjectileUser data) 
	{
		//Remove
		data.GetThrown().remove();
	}
}
