package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class LightningOrb extends SkillActive implements IThrown
{
	public LightningOrb(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Launch a lightning orb.",
				"",
				"Detonates on direct hit, or after",
				"4 seconds. On detonation, enemies",
				"within 6 range are struck by lightning,",
				"receiving Shock, Slow and Vulnerability",
				"for 2 seconds",
				"",
				"Effectiveness scales down with range."
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
		Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(57));
		item.setVelocity(player.getLocation().getDirection());
		Factory.Projectile().AddThrow(item, player, this, System.currentTimeMillis() + 4000, true, false, false, 
				Sound.FIZZ, 0.6f, 1.6f, null, 0, UpdateType.FASTEST, 1d);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));

		//Effect 
		item.getWorld().playSound(item.getLocation(), Sound.SILVERFISH_HIT, 2f, 1f);
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{
		Strike(target, data);
	}

	@Override
	public void Idle(ProjectileUser data) 
	{
		Strike(null, data);
	}

	@Override
	public void Expire(ProjectileUser data) 
	{
		Strike(null, data);
	}

	public void Strike(LivingEntity target, ProjectileUser data)
	{
		//Remove
		data.GetThrown().remove();

		//Thrower
		if (!(data.GetThrower() instanceof Player))
			return;

		Player player = (Player)data.GetThrower();

		//Level
		int level = GetLevel(player);
		if (level == 0)				return;

		//Others
		HashMap<Player, Double> hit = UtilPlayer.getInRadius(data.GetThrown().getLocation(), 6);
		for (Player cur : hit.keySet())
		{	
			//Lightning Condition 
			Factory.Condition().Factory().Lightning(GetName(), cur, player, 2, 1, false, true);
		}

		for (Player cur : hit.keySet())
		{
			if (!UtilAlg.HasSight(data.GetThrown().getLocation(), cur))
				continue;

			//Inform
			UtilPlayer.message(cur, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));

			//Lightning
			cur.getWorld().strikeLightning(cur.getLocation());
		}

		//Apply Aftereffects
		for (Player cur : hit.keySet())
		{	
			//Condition
			Factory.Condition().Factory().Shock(GetName(), cur, player, 2, false, true);
			Factory.Condition().Factory().Slow(GetName(), cur, player, 2, 0, false, true, true, true);
			Factory.Condition().Factory().Vulnerable(GetName(), cur, data.GetThrower(), 2, 0, false, true, true);
		}
	}

	@EventHandler
	public void CancelFire(BlockIgniteEvent event)
	{
		if (event.getCause() == IgniteCause.LIGHTNING)
			event.setCancelled(true);
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
