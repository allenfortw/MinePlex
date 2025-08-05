package nautilus.game.arcade.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.common.util.C;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.kit.Kit;

public abstract class SoloGame extends Game
{
	protected ArrayList<Player> _places = new ArrayList<Player>();
	
	public SoloGame(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc) 
	{
		super(manager, gameType, kits, gameDesc);
	}
	
	@EventHandler
	public void CustomTeamGeneration(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Recruit)
			return;
		
		this.GetTeamList().get(0).SetColor(ChatColor.YELLOW);
		this.GetTeamList().get(0).SetName("Players");
	}
	
	@EventHandler
	public void EndStateChange(PlayerStateChangeEvent event)
	{
		if (event.GetState() == PlayerState.OUT)
			if (!_places.contains(event.GetPlayer()))
				_places.add(0, event.GetPlayer());		
		
		else
			_places.remove(event.GetPlayer());
	}
	
	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;

		//Add Winner
		if (GetPlayers(true).size() == 1)
		{
			SetPlayerState(GetPlayers(true).get(0), PlayerState.OUT);
			return;
		}
		
		if (GetPlayers(true).size() <= 0)
		{	
			//Announce
			AnnounceEnd(_places);
			
			//Gems
			if (_places.size() >= 1)
				AddGems(_places.get(0), 20, "1st Place", false);
			
			if (_places.size() >= 2)
				AddGems(_places.get(1), 15, "2nd Place", false);
			
			if (_places.size() >= 3)
				AddGems(_places.get(2), 10, "3rd Place", false);
						
			for (Player player : GetPlayers(false))
				if (player.isOnline())
					AddGems(player, 10, "Participation", false);
			
			//End
			SetState(GameState.End);
			
		}
	}
	
	@Override
	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (GetTeamList().isEmpty())
			return;
		
		GameTeam team = GetTeamList().get(0);
					
		GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(team.GetColor() + "Alive")).setScore(team.GetPlayers(true).size());
		GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(C.cRed + "Dead")).setScore(team.GetPlayers(false).size() - team.GetPlayers(true).size());
	}
	
	public ArrayList<Player> GetPlaces()
	{
		return _places;
	}
	
	public int GetScoreboardScore(Player player)
	{
		return 0;
	}
}
