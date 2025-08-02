package nautilus.game.arcade.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.AsymTeamGame;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class GameCreationManager implements Listener
{
	ArcadeManager Manager;
	
	private ArrayList<Game> _ended = new ArrayList<Game>();
	
	private GameType _nextGame = null;
	private HashMap<String, ChatColor> _nextGameTeams = null;
	
	private String _lastMap = "";
	private GameType _lastGame = GameType.SnowFight;
	
	public GameCreationManager(ArcadeManager manager)
	{
		Manager = manager;
		
		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}
	
	public String GetLastMap()
	{
		return _lastMap;
	}

	public void SetLastMap(String file) 
	{
		_lastMap = file;
	}

	@EventHandler
	public void NextGame(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (Manager.GetGameList().isEmpty())
			return;
		
		if (Manager.GetGame() == null && _ended.isEmpty())
		{
			CreateGame(null);
		}

		//Archive Game
		if (Manager.GetGame() != null)
		{
			if (Manager.GetGame().GetState() == GameState.Dead)
			{
				HandlerList.unregisterAll(Manager.GetGame());

				//Schedule Cleanup
				_ended.add(Manager.GetGame());

				//Lobby Display
				Manager.GetLobby().DisplayLast(Manager.GetGame());

				//Prepare Round 2 - If Applicable
				if (Manager.GetGame() instanceof AsymTeamGame)
				{
					if (((AsymTeamGame) Manager.GetGame()).GetPastTeams() == null)
					{
						_nextGame = Manager.GetGame().GetType();
						_nextGameTeams = new HashMap<String, ChatColor>();
						
						for (GameTeam team : Manager.GetGame().GetTeamList())
							for (Player player : team.GetPlayers(false))
								_nextGameTeams.put(player.getName(), team.GetColor());
					}
				}

				Manager.SetGame(null);
			}
		}

		//Clean Archived Games
		Iterator<Game> gameIterator = _ended.iterator();

		while (gameIterator.hasNext())
		{	
			Game game = gameIterator.next();

			HandlerList.unregisterAll(game);
			
			//Cleaned
			if (game.WorldData == null)
			{
				gameIterator.remove();
				continue;
			}

			if (game.WorldData.World == null)
			{
				gameIterator.remove();
				continue;
			}

			//Kick Players 
			if (UtilTime.elapsed(game.GetStateTime(), 10000))
			{
				for (Player player : game.WorldData.World.getPlayers())
					player.kickPlayer("Dead World");
			}

			//Clean
			if (game.WorldData.World.getPlayers().isEmpty())
			{
				game.WorldData.Uninitialize();
				game.WorldData = null;
			}
		}
	}

	private void CreateGame(GameType gameType) 
	{
		//Reset Damage Changes
		Manager.GetDamage().DisableDamageChanges = false;
		Manager.GetCreature().SetDisableCustomDrops(false);
		
		HashMap<String, ChatColor> pastTeams = null;
		
		//Round 2
		if (_nextGame != null && _nextGameTeams != null)
		{
			gameType = _nextGame;
			pastTeams = _nextGameTeams;
			
			_nextGame = null;
			_nextGameTeams = null;
		}

		//Pick Game
		if (gameType == null)
		{
			for (int i=0 ; i<50 ; i++)
			{
				gameType = Manager.GetGameList().get(UtilMath.r(Manager.GetGameList().size()));

				if (gameType != _lastGame)
					break;
			}
		}
		
		_lastGame = gameType;

		//Make Game
		Manager.SetGame(Manager.GetGameFactory().CreateGame(gameType, pastTeams));

		if (Manager.GetGame() == null)
		{
			return;
		}

		Manager.GetLobby().DisplayNext(Manager.GetGame(), pastTeams);

		UtilServer.getServer().getPluginManager().registerEvents(Manager.GetGame(), Manager.GetPlugin());
	}
}
