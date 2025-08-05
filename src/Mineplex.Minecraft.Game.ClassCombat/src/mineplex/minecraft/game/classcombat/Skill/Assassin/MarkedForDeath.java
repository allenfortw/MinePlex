package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class MarkedForDeath extends SkillActive
{
	private HashSet<Entity> _arrows = new HashSet<Entity>();
	private HashSet<Player> _poison = new HashSet<Player>();

	public MarkedForDeath(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy,	int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType, 
				cost, levels,
				energy,	energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);

		SetDesc(new String[] 
				{
				"Your next arrow will mark players,",
				"giving them Vulnerability 2 for up",
				"to 10 seconds. Duration is lower for",
				"uncharged arrows."				
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		_poison.add(player);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));

		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.BREATH, 2.5f, 2.0f);
	}

	@EventHandler
	public void ShootBow(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getProjectile() instanceof Arrow))
			return;

		Player player = (Player)event.getEntity();

		if (!_poison.remove(player))
			return;

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(GetLevel(player))) + "."));

		_arrows.add(event.getProjectile());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.PROJECTILE)
			return;

		Projectile projectile = event.GetProjectile();
		if (projectile == null)	return;

		//Not Pin Down Arrow
		if (!_arrows.contains((Entity)projectile)) 
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		//Level
		int level = GetLevel(damager);
		if (level == 0)			return;

		//Effect
		damagee.getWorld().playSound(damagee.getLocation(), Sound.BREATH, 2.5f, 2.0f);

		//Inform
		UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(),	F.name(damager.getName()) +" hit you with " + F.skill(GetName(level)) + "."));
		UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(UtilEnt.getName(damagee)) +" with " + F.skill(GetName(level)) + "."));
		
		double duration = 10;
		if (projectile.getVelocity().length() < 2.5d)
		{
			duration = 4 + (6 *(projectile.getVelocity().length() / 2.5d));
		}
		
		//Vuln
		Factory.Condition().Factory().Vulnerable(GetName(), damagee, damager, duration, 1, true, true, true);
		
		//Damage
		event.AddMod(damager.getName(), GetName(), 0, true);
	}

	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		HashSet<Entity> remove = new HashSet<Entity>();

		for (Entity cur : _arrows)
			if (cur.isDead())
				remove.add(cur);

		for (Entity cur : remove)
			_arrows.remove(cur);
	}

	@Override
	public void Reset(Player player) 
	{
		_poison.remove(player);
	}
}
