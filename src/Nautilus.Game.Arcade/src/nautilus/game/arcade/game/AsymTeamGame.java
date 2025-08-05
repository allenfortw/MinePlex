package nautilus.game.arcade.game;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.kit.Kit;

public abstract class AsymTeamGame extends TeamGame
{
	private HashMap<String, ChatColor> _pastTeams = null;
	
	public AsymTeamGame(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc, HashMap<String, ChatColor> pastTeams)
	{
		super(manager, gameType, kits, gameDesc);
		
		_pastTeams = pastTeams;
	}
	
	public HashMap<String, ChatColor> GetPastTeams()
	{
		return _pastTeams;
	} 
	
	@EventHandler(priority = EventPriority.HIGH)
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Recruit)
			return;
		
		if (GetPastTeams() != null)
		{
			this.SetCountdown(30);
			this.SetCountdownForce(true);
		}
	}

	@Override
	public GameTeam ChooseTeam(Player player)  
	{
		//Get Past Team
		ChatColor pastTeam = null;
		if (_pastTeams != null)
			pastTeam = _pastTeams.get(player.getName());
				
		GameTeam newTeam = null;
		
		for (int i=0 ; i<GetTeamList().size() ; i++)
			if (newTeam == null || GetTeamList().get(i).GetSize() < newTeam.GetSize())
				if (pastTeam == null || GetTeamList().get(i).GetColor() != pastTeam)
					newTeam = GetTeamList().get(i);
		
		return newTeam;
	}
}
