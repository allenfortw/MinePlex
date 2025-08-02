package me.chiss.Core.Modules;

import java.util.WeakHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Module.AModule;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;

public class Observer extends AModule 
{
	private WeakHashMap<Player, Location> _obs = new WeakHashMap<Player, Location>();

	public Observer(JavaPlugin plugin) 
	{
		super("Observer", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{
		for (Player player : _obs.keySet())
			remove(player, false);
	}

	@Override
	public void config() 
	{

	}
	
	@Override
	public void commands() 
	{
		AddCommand("o");
		AddCommand("obs");
		AddCommand("observer");
		AddCommand("z");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.MODERATOR, true))
			return;

		if (isObserver(caller, false))			remove(caller, true);
		else									add(caller, true);
	}
	
	@EventHandler
	public void handleJoin(PlayerQuitEvent event)
	{
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		remove(event.getPlayer(), false);
	}

	public void add(Player player, boolean inform)
	{
		_obs.put(player, player.getLocation());
		player.setGameMode(GameMode.CREATIVE);
		UtilInv.Clear(player);
		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.WEB, 1));

		if (inform)
			UtilPlayer.message(player, F.main(_moduleName, "You entered Observer Mode."));
	}

	public boolean remove(Player player, boolean inform)
	{
		Location loc = _obs.remove(player);
		if (loc != null && inform)
		{
			UtilPlayer.message(player, F.main(_moduleName, "You left Observer Mode."));
			player.setGameMode(GameMode.SURVIVAL);
			UtilInv.Clear(player);
			Teleport().TP(player, loc);
			return true;
		}

		return false;
	}


	public boolean isObserver(Player player, boolean adminReq)
	{
		if (adminReq)
		{
			if (_obs.containsKey(player) && Clients().Get(player).Rank().Has(Rank.ADMIN, false))
				return true;
		}
		else if (_obs.containsKey(player))
			return true;

		return false;
	}

	//Invisible
	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player player : _obs.keySet())
			Condition().Factory().Cloak("Observer", player, player, 1.9, false, true);
	}

	//Inventory Open
	@EventHandler
	public void handleInventoryOpen(InventoryOpenEvent event)
	{
		//Not Obs
		if (!_obs.containsKey((Entity)event.getPlayer()))
			return;

		//Non-Admin > DISALLOW ALL
		if (!Clients().Get(event.getPlayer().getName()).Rank().Has(Rank.ADMIN, false))
		{

			UtilPlayer.message(UtilPlayer.searchExact(event.getPlayer().getName()), F.main(_moduleName, "You cannot open Inventory."));
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void handleInventoryClick(InventoryClickEvent event)
	{
		//Not Obs
		if (!_obs.containsKey((Entity)event.getWhoClicked()))
			return;

		UtilPlayer.message(UtilPlayer.searchExact(event.getWhoClicked().getName()), F.main(_moduleName, "You cannot interact with Inventory."));
		event.getWhoClicked().closeInventory();
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void handlePickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_obs.containsKey((Entity)event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void handleDrop(PlayerDropItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_obs.containsKey((Entity)event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void handleBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_obs.containsKey((Entity)event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void handleBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_obs.containsKey((Entity)event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void handleDamage(CustomDamageEvent event)
	{
		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;
		
		if (_obs.containsKey((Entity)damager))
			event.SetCancelled("Observer Mode");
	}
}
