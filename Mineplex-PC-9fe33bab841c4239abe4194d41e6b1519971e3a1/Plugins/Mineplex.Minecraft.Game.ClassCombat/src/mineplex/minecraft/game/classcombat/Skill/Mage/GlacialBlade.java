package mineplex.minecraft.game.classcombat.Skill.Mage;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class GlacialBlade extends SkillActive implements IThrown
{
	public GlacialBlade(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Swinging your sword releases a",
				"shard of ice, dealing 5 damage",
				"to anything it hits.",
				"",
				"Will not work if enemies are close."
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		for (Player cur : UtilPlayer.getNearby(player.getLocation(), 4))
			if (cur.equals(player))
				continue;
			else if (Factory.Relation().CanHurt(cur, player))
				return false;

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(370));
		UtilAction.velocity(item, player.getLocation().getDirection(), 1.2, false, 0, 0.1, 10, false);
		Factory.Projectile().AddThrow(item, player, this, -1, true, true, true, 1d);

		//Effect
		item.getWorld().playSound(item.getLocation(), Sound.ORB_PICKUP, 1f, 2f);
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{		
		//Effect
		data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);

		//Remove
		data.GetThrown().remove();

		if (target == null)
			return;

		//Damage
		Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
				DamageCause.CUSTOM, 5, true, true, false,
				UtilEnt.getName(data.GetThrower()), GetName());
	}

	@Override
	public void Idle(ProjectileUser data) 
	{
		//Effect
		data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);

		//Remove
		data.GetThrown().remove();
	}

	@Override
	public void Expire(ProjectileUser data) 
	{
		//Effect
		data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 20);

		//Remove
		data.GetThrown().remove();
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
