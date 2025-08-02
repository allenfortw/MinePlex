package nautilus.game.core.game;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.player.ITeamGamePlayer;

public interface ITeamGame<ArenaType extends ITeamArena, PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>> extends IGame<ArenaType, PlayerType>
{
	PlayerTeamType GetBlueTeam();
	PlayerTeamType GetRedTeam();
	
	void ActivatePlayer(PlayerType player);
}