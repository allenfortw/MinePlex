package nautilus.game.core.events.team;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.events.GameFinishedEvent;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;

public class TeamGameFinishedEvent<GameType extends ITeamGame<? extends ITeamArena, PlayerType, PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>> extends GameFinishedEvent<GameType>
{
  private PlayerTeamType _winner;
  
  public TeamGameFinishedEvent(GameType game, PlayerTeamType winner)
  {
    super(game);
    
    this._winner = winner;
  }
  
  public PlayerTeamType GetWinningTeam()
  {
    return this._winner;
  }
}
