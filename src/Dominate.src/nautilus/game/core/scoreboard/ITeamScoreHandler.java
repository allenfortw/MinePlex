package nautilus.game.core.scoreboard;

import nautilus.game.core.engine.ITeam;
import nautilus.game.core.player.ITeamGamePlayer;

public abstract interface ITeamScoreHandler<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>>
  extends IScoreHandler<PlayerType>
{
  public abstract void RewardForTeamKill(PlayerType paramPlayerType1, PlayerType paramPlayerType2);
  
  public abstract void RewardForKill(PlayerType paramPlayerType1, PlayerType paramPlayerType2, int paramInt);
  
  public abstract void RewardForAssist(PlayerType paramPlayerType1, PlayerType paramPlayerType2);
}
