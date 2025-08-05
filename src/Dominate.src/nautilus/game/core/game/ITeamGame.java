package nautilus.game.core.game;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.player.ITeamGamePlayer;

public abstract interface ITeamGame<ArenaType extends ITeamArena, PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>>
  extends IGame<ArenaType, PlayerType>
{
  public abstract PlayerTeamType GetBlueTeam();
  
  public abstract PlayerTeamType GetRedTeam();
  
  public abstract void ActivatePlayer(PlayerType paramPlayerType);
}
