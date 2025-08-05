package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Longshot extends Skill
{
	private HashSet<Player> _zoomed = new HashSet<Player>();
	private HashMap<Entity, Location> _arrows = new HashMap<Entity, Location>();

	public Longshot(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Arrows do an additional",
				"1 damage per 3 Blocks travelled,",
				"however, their base damage is",
				"reduced by 3.",
				"",
				"Maximum of 20 additional damage."
				});
	}

	@EventHandler
	public void ShootBow(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;
		
		int level = GetLevel((Player)event.getEntity());
		if (level == 0)		return;
		
		//Save
		_arrows.put(event.getProjectile(), event.getProjectile().getLocation());
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

		if (!_arrows.containsKey(projectile))
			return;
		
		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)		return;
		
		Location loc = _arrows.remove(projectile);
		double length = UtilMath.offset(loc, projectile.getLocation());
		
		//Damage
		double damage = Math.min(20, (length / 3) - 3);
	
		event.AddMod(damager.getName(), GetName(), damage, damage > 0);
	}
	
	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		HashSet<Entity> remove = new HashSet<Entity>();

		for (Entity cur : _arrows.keySet())
			if (cur.isDead() || !cur.isValid())
				remove.add(cur);

		for (Entity cur : remove)
		{
			_arrows.remove(cur);
		}		
	}
	
	//@EventHandler
	public void ToggleZoom(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		
		if (GetLevel(player) == 0)
			return;

		if (UtilGear.isWeapon(player.getItemInHand()))
			return;

		event.setCancelled(true);
		
		//Disable
		if (_zoomed.contains(player))
		{
			_zoomed.remove(player);
			UtilPlayer.message(player, F.main(GetClassType().name(), "Hawks Eye: " + F.oo("Disabled", false)));
			Factory.Condition().EndCondition(player, ConditionType.SLOW, GetName());
		}
		//Enable
		else
		{
			_zoomed.add(event.getPlayer());
			UtilPlayer.message(event.getPlayer(), F.main(GetClassType().name(), "Hawks Eye: " + F.oo("Enabled", true)));
		}
	}
	
	//@EventHandler
	public void Zoom(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return; 

		Iterator<Player> zoomIterator = _zoomed.iterator();
		
		while (zoomIterator.hasNext())
		{
			Player cur = zoomIterator.next();

			if (!cur.isOnline())
			{
				zoomIterator.remove();
				continue;
			}
			
			if (GetLevel(cur) > 0)
				Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9, 1 + (GetLevel(cur)), false, true, false, true);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_zoomed.remove(player);
	}
}
