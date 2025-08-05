package mineplex.core.projectile;

import java.util.HashSet;
import java.util.WeakHashMap;

import mineplex.core.MiniPlugin;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ProjectileManager extends MiniPlugin
{
	private WeakHashMap<Entity, ProjectileUser> _thrown = new WeakHashMap<Entity, ProjectileUser>();
	
	public ProjectileManager(JavaPlugin plugin) 
	{
		super("Throw", plugin);
	}
	
	public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, 
			long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, double hitboxMult)
	{
		_thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
				expireTime, hitPlayer, hitBlock, idle, false,
				null, 1f, 1f, null, 0, null, hitboxMult)); 
	}
	
	public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, 
			long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, boolean pickup, double hitboxMult)
	{
		_thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
				expireTime, hitPlayer, hitBlock, idle, pickup, 
				null, 1f, 1f, null, 0, null, hitboxMult)); 
	}
	
	public void AddThrow(Entity thrown, LivingEntity thrower, IThrown callback, 
			long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle,
			Sound sound, float soundVolume, float soundPitch, Effect effect, int effectData, UpdateType effectRate , double hitboxMult)
	{
		_thrown.put(thrown, new ProjectileUser(this, thrown, thrower, callback, 
				expireTime, hitPlayer, hitBlock, idle, false,
				sound, soundVolume, soundPitch, effect, effectData, effectRate, hitboxMult)); 
	}
		
	@EventHandler
	public void Update(UpdateEvent event)
	{
		//Collisions
		if (event.getType() == UpdateType.TICK)
		{
			HashSet<Entity> remove = new HashSet<Entity>();
			
			for (Entity cur : _thrown.keySet())
				if (_thrown.get(cur).Collision())
					remove.add(cur);
				else if (cur.isDead() || !cur.isValid())
					remove.add(cur);
			
			for (Entity cur : remove)
				_thrown.remove(cur);
		}
		
		//Effects
		for (ProjectileUser cur : _thrown.values())
			cur.Effect(event);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;

		if (_thrown.containsKey(event.getItem()))
			if (!_thrown.get(event.getItem()).CanPickup(event.getPlayer()))
				event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_thrown.containsKey(event.getItem()))
			event.setCancelled(true);
	}
}
