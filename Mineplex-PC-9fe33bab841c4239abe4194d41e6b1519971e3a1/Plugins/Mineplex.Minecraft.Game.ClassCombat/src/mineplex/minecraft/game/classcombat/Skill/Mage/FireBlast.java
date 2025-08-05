package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class FireBlast extends SkillActive
{
	public FireBlast(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Launch an explosive fireball;",
				"Explosion gives large knockback",
				"and ignites enemies for 8 seconds.",
				"",
				"Effects scale down with distance",
				"from explosion."
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
		LargeFireball ball = player.launchProjectile(LargeFireball.class);
		ball.setShooter(player);
		ball.setIsIncendiary(false);		
		ball.setYield(0);
		ball.setBounce(false);
		ball.teleport(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)));
		ball.setVelocity(new Vector(0,0,0));

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));

		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 0.8f);
	}

	@EventHandler
	public void Collide(ProjectileHitEvent event)
	{
		Projectile proj = event.getEntity();

		if (!(proj instanceof LargeFireball))
			return;

		if (proj.getShooter() == null)
			return;

		if (!(proj.getShooter() instanceof Player))
			return;

		Player player = (Player)proj.getShooter();

		//Level
		int level = GetLevel(player);
		if (level == 0)				return;

		//Velocity Players
		HashMap<Player,Double> hitMap = UtilPlayer.getInRadius(proj.getLocation(), 8);
		for (Player cur : hitMap.keySet())
		{	
			double range = hitMap.get(cur);

			//Damage Event
			Factory.Condition().Factory().Ignite(GetName(), cur, player, 2 + 6 * range, false, false);

			//Velocity
			UtilAction.velocity(cur, UtilAlg.getTrajectory(proj.getLocation().add(0, -0.5, 0), cur.getEyeLocation()), 
					0.5 + 1.5 * range, false, 0, 0.2 + 0.4 * range, 1.2, true);
		}
		
		//Fire
		for (int i=0 ; i<60 ; i++)
		{
			Item fire = player.getWorld().dropItem(proj.getLocation().add(0, 0.5, 0), ItemStackFactory.Instance.CreateStack(Material.FIRE));
			fire.setVelocity(new Vector((Math.random() - 0.5)/2,Math.random()/2+0.5,(Math.random() - 0.5)/2));
			Factory.Fire().Add(fire, player, 8, 2, 3, 0, GetName());
		}
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
