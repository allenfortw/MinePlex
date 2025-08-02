package mineplex.core.antistack;

import mineplex.core.MiniPlugin;

import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiStack extends MiniPlugin
{
	public AntiStack(JavaPlugin plugin)
	{
		super("AntiStack", plugin);
	}

	@EventHandler (priority=EventPriority.HIGHEST)
	public void ItemSpawn(ItemSpawnEvent event)
	{
		if (event.isCancelled())
			return;

		Item item = event.getEntity();
		
		//ItemName()
		if (item.getLocation().getY() < -10)
			return;

		//Get Name
		String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();

		//Append UID
		name += ":" + item.getUniqueId();

		//Set Name
		((CraftItemStack)item.getItemStack()).getHandle().c(name);
	}

	@EventHandler (priority=EventPriority.HIGHEST)
	public void PlayerPickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;

		Item item = event.getItem();

		//Get Name
		String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();

		//Remove UID
		if (name.contains(":"))
		    name = name.substring(0, name.indexOf(":" + item.getUniqueId()));

		//Set Name
		((CraftItemStack)item.getItemStack()).getHandle().c(name);
	}
	
	@EventHandler
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		Item item = event.getItem();

		//Get Name
		String name = ((CraftItemStack)item.getItemStack()).getHandle().getName();

		//Remove UID
		if (name.contains(":"))
		    name = name.substring(0, name.indexOf(":" + item.getUniqueId()));

		//Set Name
		((CraftItemStack)item.getItemStack()).getHandle().c(name);
	}
}
