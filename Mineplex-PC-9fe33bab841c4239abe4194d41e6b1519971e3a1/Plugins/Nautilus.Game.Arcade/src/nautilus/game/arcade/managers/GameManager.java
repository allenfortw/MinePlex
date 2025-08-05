package nautilus.game.arcade.managers;

import java.util.Iterator;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.RestartServerEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GameManager implements Listener
{
	ArcadeManager Manager;

	public GameManager(ArcadeManager manager)
	{
		Manager = manager;

		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}

	@EventHandler
	public void StateUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		if (game.GetState() == GameState.Loading)
		{
			if (UtilTime.elapsed(game.GetStateTime(), 30000))
			{
				System.out.println("Game Load Expired.");
				game.SetState(GameState.Dead);
			}
		}
		else if (game.GetState() == GameState.Recruit)
		{
			//Stop Countdown!
			if (game.GetCountdown() != -1 && 
					UtilServer.getPlayers().length < Manager.GetPlayerMin() && 
					!game.GetCountdownForce())
			{
				game.SetCountdown(-1);
				Manager.GetLobby().DisplayWaiting();
			}

			if (game.GetCountdown() != -1)
				StateCountdown(game, -1, false);

			else if (game.AutoStart)
			{
				if (UtilServer.getPlayers().length >= Manager.GetPlayerFull()) 
					StateCountdown(game, 20, false);

				else if (UtilServer.getPlayers().length >= Manager.GetPlayerMin())
					StateCountdown(game, 60, false);	
			}
		}
		else if (game.GetState() == GameState.Prepare)
		{
			if (UtilTime.elapsed(game.GetStateTime(), 9000))
			{
				for (Player player : UtilServer.getPlayers())
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);

				if (game.GetPlayers(true).size() < 2)
				{
					game.Announce(C.cWhite + C.Bold + game.GetName() + " ended, not enough players!");
					game.SetState(GameState.Dead);
				}
				else
				{
					game.SetState(GameState.Live);
				}
			}
			else
			{
				for (Player player : UtilServer.getPlayers())
					player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
			}
		}
		else if (game.GetState() == GameState.Live)
		{
			if (game.GetType() == GameType.Bridge)
			{
				if (UtilTime.elapsed(game.GetStateTime(), 2400000))
				{
					game.SetState(GameState.End);
				}
			}
			else if (game.GetType() != GameType.UHC)
			{
				if (UtilTime.elapsed(game.GetStateTime(), 1200000))
				{
					game.SetState(GameState.End);
				}
			}	
		}
		else if (game.GetState() == GameState.End)
		{
			if (UtilTime.elapsed(game.GetStateTime(), 10000))
			{
				game.SetState(GameState.Dead);
			}
		}
	}

	public void StateCountdown(Game game, int timer, boolean force)
	{
		//Always give time to pick team.
		if (!game.GetCountdownForce() && !force && !UtilTime.elapsed(game.GetStateTime(), 15000))
		{
			return;
		}

		if (force)
			game.SetCountdownForce(true);

		//Team Preference
		TeamPreferenceJoin(game);

		//Team Swap
		TeamPreferenceSwap(game);

		//Team Default
		TeamDefaultJoin(game);

		//Team Inform STILL Queued
		if (game.GetCountdown() == -1)
		{
			game.InformQueuePositions();
			//game.AnnounceGame();
		}
		
		//Initialise Countdown
		if (force)
			game.SetCountdownForce(true);

		//Start  Timer
		if (game.GetCountdown() == -1)
			game.SetCountdown(timer + 1);

		//Decrease Timer
		if (game.GetCountdown() > timer + 1 && timer != -1)
			game.SetCountdown(timer + 1);

		//Countdown--
		if (game.GetCountdown() > 0)
			game.SetCountdown(game.GetCountdown() - 1);

		//Inform Countdown
		if (game.GetCountdown() > 0)		
		{
			Manager.GetLobby().WriteGameLine("starting in " + game.GetCountdown() + "...", 3, 159, (byte)13);
		}
		else					
		{
			Manager.GetLobby().WriteGameLine("game in progress", 3, 159, (byte)13);
		}

		if (game.GetCountdown() > 0 && game.GetCountdown() <= 10)
			for (Player player : UtilServer.getPlayers())
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);

		//Countdown Ended
		if (game.GetCountdown() == 0)
			game.SetState(GameState.Prepare);
	}
	
	@EventHandler
	public void restartServerCheck(RestartServerEvent event)
	{
		if (Manager.GetGame() != null && Manager.GetGame().GetState() != GameState.Recruit)
			event.setCancelled(true);
	}

	@EventHandler
	public void KitRegister(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Live)
			return;

		event.GetGame().RegisterKits();
	}

	@EventHandler
	public void KitDeregister(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Dead)
			return;

		event.GetGame().DeregisterKits();

		for (Player player : UtilServer.getPlayers())
		{
			Manager.Clear(player);
			Manager.GetCondition().EndCondition(player, ConditionType.CLOAK, "Spectator");
			player.teleport(Manager.GetLobby().GetSpawn());	
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)	//BEFORE PARSE DATA
	public void TeamGeneration(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Recruit)
			return;

		Game game = event.GetGame();

		for (String team : game.WorldData.SpawnLocs.keySet())
		{
			ChatColor color;

			if (team.equalsIgnoreCase("RED"))			color = ChatColor.RED;
			else if (team.equalsIgnoreCase("YELLOW"))	color = ChatColor.YELLOW;
			else if (team.equalsIgnoreCase("GREEN"))	color = ChatColor.GREEN;
			else if (team.equalsIgnoreCase("BLUE"))		color = ChatColor.AQUA;
			else
			{
				color = ChatColor.DARK_GREEN;

				if (game.GetTeamList().size() == 0) 	if (game.WorldData.SpawnLocs.size() > 1)		color = ChatColor.RED;
				if (game.GetTeamList().size() == 1) 	color = ChatColor.YELLOW;
				if (game.GetTeamList().size() == 2) 	color = ChatColor.GREEN;
				if (game.GetTeamList().size() == 3) 	color = ChatColor.AQUA;
				if (game.GetTeamList().size() == 4) 	color = ChatColor.GOLD;
				if (game.GetTeamList().size() == 5) 	color = ChatColor.DARK_BLUE;
				if (game.GetTeamList().size() == 6) 	color = ChatColor.LIGHT_PURPLE;
				if (game.GetTeamList().size() == 7) 	color = ChatColor.WHITE;
			}

			GameTeam newTeam = new GameTeam(team, color, game.WorldData.SpawnLocs.get(team));
			game.AddTeam(newTeam);
		}

		//Restrict Kits
		game.RestrictKits();

		//Parse Data
		game.ParseData();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void TeamScoreboardCreation(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Recruit)
			return;

		event.GetGame().CreateScoreboardTeams();
	}

	public void TeamPreferenceJoin(Game game)
	{
		//Preferred Team No Longer Full
		for (GameTeam team : game.GetTeamPreferences().keySet())
		{	
			Iterator<Player> queueIterator = game.GetTeamPreferences().get(team).iterator();

			while (queueIterator.hasNext())
			{
				Player player = queueIterator.next();

				if (!game.CanJoinTeam(team))
					break;
				
				queueIterator.remove();

				if (!game.IsPlaying(player))
				{
					PlayerAdd(game, player, team);
				}
				else
				{
					game.SetPlayerTeam(player, team);
				}
			}
		}
	}

	public void TeamPreferenceSwap(Game game)
	{
		//Preferred Team No Longer Full
		for (GameTeam team : game.GetTeamPreferences().keySet())
		{	
			Iterator<Player> queueIterator = game.GetTeamPreferences().get(team).iterator();

			while (queueIterator.hasNext())
			{
				Player player = queueIterator.next();

				GameTeam currentTeam = game.GetTeam(player);

				//Not on team, cannot swap
				if (currentTeam == null)
					continue;

				// Other without concurrent (order doesn't matter as first case will fire
				if (team == currentTeam)
				{
					queueIterator.remove();
					continue;
				}

				for (Player other : team.GetPlayers(false))
				{
					if (other.equals(player))
						continue;

					GameTeam otherPref = game.GetTeamPreference(other);
					if (otherPref == null)
						continue;

					if (otherPref.equals(currentTeam))
					{
						UtilPlayer.message(player, F.main("Team", "You swapped team with " + F.elem(team.GetColor() + other.getName()) + "."));
						UtilPlayer.message(other, F.main("Team", "You swapped team with " + F.elem(currentTeam.GetColor() + player.getName()) + "."));

						//Player Swap
						queueIterator.remove();
						game.SetPlayerTeam(player, team);

						//Other Swap
						game.SetPlayerTeam(other, currentTeam);
					}
				}		
			}
		}
	}

	public void TeamDefaultJoin(Game game) 
	{
		//Team Default
		for (Player player : UtilServer.getPlayers())
		{
			if (player.isDead())
			{
				player.kickPlayer("Kicked for being AFK");
			}
			else if (!game.IsPlaying(player))
			{
				PlayerAdd(game, player, null);
			}
		}
	}

	@EventHandler
	public void TeamQueueSizeUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		for (GameTeam team : game.GetTeamList())
		{
			int amount = 0;
			if (game.GetTeamPreferences().containsKey(team))
			{
				amount = game.GetTeamPreferences().get(team).size();
			}

			if (team.GetTeamEntity() == null)
				continue;
			
			if (game.GetCountdown() == -1)
			{
				team.GetTeamEntity().setCustomName(team.GetFormattedName() + " Team" + ChatColor.RESET + "  " + amount + " Queued");
			}
			else
			{
				team.GetTeamEntity().setCustomName(team.GetPlayers(false).size() + " Players  " + team.GetFormattedName() + " Team" + ChatColor.RESET + "  " + amount + " Queued");
			}
		}
	}

	public boolean PlayerAdd(Game game, Player player, GameTeam team)
	{
		if (team == null)
			team = game.ChooseTeam(player);

		if (team == null)
			return false;

		game.SetPlayerTeam(player, team);

		//Game Mode
		player.setGameMode(GameMode.SURVIVAL);

		//Clear Inventory
		UtilInv.Clear(player);

		return true;
	}

	@EventHandler
	public void PlayerPrepare(GameStateChangeEvent event)
	{
		Game game = event.GetGame();

		if (event.GetState() != GameState.Prepare)
			return;

		//Teleport
		for (GameTeam team : game.GetTeamList())
			team.SpawnTeleport(); 

		//Save Initial Player Count
		game.SetPlayerCountAtStart(game.GetPlayers(true).size());

		//Announce
		game.AnnounceGame();

		//Prepare Players
		for (Player player : game.GetPlayers(true))
		{
			Manager.Clear(player);
			UtilInv.Clear(player);

			game.ValidateKit(player, game.GetTeam(player));

			game.GetKit(player).ApplyKit(player);
		}
	}

	@EventHandler
	public void PlayerClean(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Dead)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			Manager.Clear(player);
			player.eject();
			player.teleport(Manager.GetLobby().GetSpawn());
		}
	}



	@EventHandler
	public void WorldFireworksUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTEST)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		if (game.GetState() != GameState.End)
			return;

		Color color = Color.GREEN;

		if (game.WinnerTeam != null)
		{
			if (game.WinnerTeam.GetColor() == ChatColor.RED)				color = Color.RED;
			else if (game.WinnerTeam.GetColor() == ChatColor.AQUA)			color = Color.BLUE;
			else if (game.WinnerTeam.GetColor() == ChatColor.YELLOW)		color = Color.YELLOW;
			else															color = Color.LIME;
		}

		FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with(Type.BALL_LARGE).trail(false).build();

		try 
		{
			Manager.GetFirework().playFirework(game.GetSpectatorLocation().clone().add(
					Math.random()*160-80, 10 + Math.random()*20, Math.random()*160-80), effect);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void EndUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		game.EndCheck();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void EndStateChange(PlayerStateChangeEvent event)
	{
		event.GetGame().EndCheck();
	}
}
