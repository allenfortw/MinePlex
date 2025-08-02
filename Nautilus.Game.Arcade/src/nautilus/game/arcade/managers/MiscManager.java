package nautilus.game.arcade.managers;

import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MiscManager implements Listener
{
	ArcadeManager Manager;

	public MiscManager(ArcadeManager manager)
	{
		Manager = manager;
		
		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractActive(PlayerInteractEvent event)
	{
		event.setCancelled(false);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void InteractClickCancel(PlayerInteractEvent event)
	{
		if (Manager.GetGame() == null)
			return;

		Player player = event.getPlayer();

		if (!Manager.GetGame().IsAlive(player))
		{
			event.setCancelled(true);
		}
		else if (event.getPlayer().getItemInHand().getType() == Material.INK_SACK && event.getPlayer().getItemInHand().getData().getData() == (byte)15)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void InventoryClickCancel(InventoryClickEvent event)
	{
		if (Manager.GetGame() == null)
			return;

		Player player = UtilPlayer.searchExact(event.getWhoClicked().getName());

		if (Manager.GetGame().IsLive() && !Manager.GetGame().IsAlive(player))
		{
			event.setCancelled(true);
			player.closeInventory();
		}
	}
	
	@EventHandler
	public void EnsureHubClock(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;
		
		if (Manager.GetGame() != null && Manager.GetGame().GetType() == GameType.UHC)
			return;
		
		for (Player player : UtilServer.getPlayers())
		{
			if (Manager.GetGame() == null || !Manager.GetGame().IsAlive(player))
			{
				if (!player.getInventory().contains(Material.WATCH))
				{
					Manager.HubClock(player);
				}
			}
		}
	}
}
