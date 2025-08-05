package nautilus.game.arcade.managers;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilServer;
import mineplex.core.pet.PetManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;

public class GameLootManager implements Listener
{
	private ArcadeManager Manager; 
	
	private HashSet<Player> _players = new HashSet<Player>();
	
	public GameLootManager(ArcadeManager manager, PetManager petManager)
	{
		Manager = manager;

		Manager.getPluginManager().registerEvents(this, Manager.getPlugin());
	}
	
	@EventHandler
	public void registerPlayers(GameStateChangeEvent event)
	{
		if (!Manager.IsRewardItems())
			return;

		if (event.GetState() != GameState.Live)
			return;
		
		_players.clear();
		
		int requirement = (int)((double)event.GetGame().Manager.GetPlayerFull() * 0.5d);
		
		event.GetGame().CanGiveLoot = (double)event.GetGame().GetPlayers(true).size() >= requirement;
		
		if (!event.GetGame().CanGiveLoot)
		{
			event.GetGame().Announce(C.Bold + "Game Loot Disabled. Requires " + requirement + " Players.", event.GetGame().PlaySoundGameStart);
			return;
		}
			
		for (Player player : event.GetGame().GetPlayers(true))
			_players.add(player);
		
		System.currentTimeMillis();
	}
	
	@EventHandler
	public void unregisterPlayer(PlayerQuitEvent event)
	{
		_players.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void giveLoot(final GameStateChangeEvent event)
	{
		if (!Manager.IsRewardItems())
			return;

		if (event.GetState() != GameState.Dead)
			return;

		UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(Manager.getPlugin(), new Runnable()
		{
			public void run()
			{
				_players.clear();
			}
		}, 240);
		//Delay after Achievements
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event)
	{
		if (!event.getPlayer().isOp())
			return;
		
		//TODO Remove
		if (event.getMessage().startsWith("/lootdebug"))
		{
			event.getPlayer().sendMessage(C.cGreen + C.Bold + "Loot Debug...");
			event.setCancelled(true);
		}
	}
}
