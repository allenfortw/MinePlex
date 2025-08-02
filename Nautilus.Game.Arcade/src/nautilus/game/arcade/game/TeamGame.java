package nautilus.game.arcade.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.kit.Kit;

public abstract class TeamGame extends Game
{
	public TeamGame(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc) 
	{
		super(manager, gameType, kits, gameDesc);
	}

	public void EndCheck()
	{
		if (!IsLive())
			return;

		ArrayList<GameTeam> teamsAlive = new ArrayList<GameTeam>();

		for (GameTeam team : this.GetTeamList())
			if (team.GetPlayers(true).size() > 0)
				teamsAlive.add(team);

		if (teamsAlive.size() <= 1)
		{
			//Announce
			if (teamsAlive.size() > 0)
				AnnounceEnd(teamsAlive.get(0));
			
			for (GameTeam team : GetTeamList())
			{
				if (WinnerTeam != null && team.equals(WinnerTeam))
				{
					for (Player player : team.GetPlayers(false))
						AddGems(player, 10, "Winning Team", false);
				}
				
				for (Player player : team.GetPlayers(false))
					if (player.isOnline())
						AddGems(player, 10, "Participation", false);
			}
			
			//End
			SetState(GameState.End);	
		}
	}
}
