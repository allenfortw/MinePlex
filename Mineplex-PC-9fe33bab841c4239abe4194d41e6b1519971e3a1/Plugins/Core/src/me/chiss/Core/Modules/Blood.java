package me.chiss.Core.Modules;

import java.util.HashMap;
import java.util.HashSet;

import mineplex.core.MiniPlugin;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Blood extends MiniPlugin
{
	private HashMap<Item, Integer> _blood = new HashMap<Item, Integer>();

	public Blood(JavaPlugin plugin) 
	{
		super("Blood", plugin);
	}

	@EventHandler
	public void Death(PlayerDeathEvent event)
	{
		Effects(event.getEntity().getEyeLocation(), 10, 0.5, Sound.HURT, 1f, 1f, Material.INK_SACK, (byte)1, true);
	}
	
	public void Effects(Location loc, int particles, double velMult, Sound sound,
			float soundVol, float soundPitch, Material type, byte data, boolean bloodStep)
	{
		Effects(loc, particles, velMult, sound, soundVol, soundPitch, type, data, 10, bloodStep);
	}
	
	public void Effects(Location loc, int particles, double velMult, Sound sound,
			float soundVol, float soundPitch, Material type, byte data, int ticks, boolean bloodStep)
	{
		for (int i = 0 ; i < particles ; i++)
		{
			Item item = loc.getWorld().dropItem(loc, 
					ItemStackFactory.Instance.CreateStack(type, data));
			
			item.setVelocity(new Vector((Math.random() - 0.5)*velMult,Math.random()*velMult,(Math.random() - 0.5)*velMult));

			_blood.put(item, ticks);
		}

		if (bloodStep)
			loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 55);
		
		loc.getWorld().playSound(loc, sound, soundVol, soundPitch);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		HashSet<Item> expire = new HashSet<Item>();

		for (Item cur : _blood.keySet())
			if (cur.getTicksLived() > _blood.get(cur) || !cur.isValid())
				expire.add(cur);

		for (Item cur : expire)
		{
			cur.remove();
			_blood.remove(cur);
		}
	}

	@EventHandler
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_blood.containsKey(event.getItem()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_blood.containsKey(event.getItem()))
			event.setCancelled(true);
	}
}
