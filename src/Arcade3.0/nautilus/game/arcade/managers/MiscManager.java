package nautilus.game.arcade.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;

public class MiscManager implements Listener
{
	private List<String> _dontGiveClockList = new ArrayList<String>();
	private ArcadeManager Manager;

	public MiscManager(ArcadeManager manager)
	{
		Manager = manager;
		
		Manager.getPluginManager().registerEvents(this, Manager.getPlugin());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractActive(PlayerInteractEvent event)
	{
		event.setCancelled(false);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void InteractClickCancel(PlayerInteractEvent event)
	{
		if (Manager.GetGame() == null)
			return;

		//BoneMeal
		if (!Manager.GetGame().WorldBoneMeal &&
			event.getAction() == Action.RIGHT_CLICK_BLOCK &&
			event.getPlayer().getItemInHand().getType() == Material.INK_SACK && 
			event.getPlayer().getItemInHand().getData().getData() == (byte)15)
		{
			event.setCancelled(true);
		}
		else if (Manager.GetGame().GetState() != GameState.Live)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void addClockPrevent(InventoryOpenEvent event)
	{
		if (event.getPlayer() instanceof Player)
		{
			_dontGiveClockList.add(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void removeClockPrevent(InventoryCloseEvent event)
	{
		if (event.getPlayer() instanceof Player)
		{
			_dontGiveClockList.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void HubClockUpdate(UpdateEvent event)
	{
		if (!Manager.IsHotbarHubClock())
			return;
		
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (Manager.GetGame() == null)
			return;
				
		for (Player player : UtilServer.getPlayers())
		{
			if (Manager.GetGame().IsAlive(player))
				continue;
			
			if (_dontGiveClockList.contains(player.getName()))
				continue;
				
			Manager.HubClock(player);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void HubClockInteract(PlayerInteractEvent event)
	{
		if (!Manager.IsHotbarHubClock())
			return;
		
		if (event.getAction() == Action.PHYSICAL)
			return;
		
		Player player = event.getPlayer();

		if (player.getItemInHand() == null)
			return;

		if (player.getItemInHand().getType() != Material.WATCH)
			return;

		// Prevent players from hub warping off clock in Master Builders
		if (!player.getItemInHand().getItemMeta().getDisplayName().contains("Hub"))
			return;

		if (!Recharge.Instance.usable(event.getPlayer(), "Return to Hub"))
			return;
			
	}
	
	@EventHandler
	public void HubCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().toLowerCase().startsWith("/lobby") || event.getMessage().toLowerCase().startsWith("/hub") || event.getMessage().toLowerCase().startsWith("/leave"))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void disableAchievementGUI(GameStateChangeEvent event)
	{
	}
}
