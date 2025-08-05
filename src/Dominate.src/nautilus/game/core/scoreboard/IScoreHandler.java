package nautilus.game.core.scoreboard;

import nautilus.game.core.player.IGamePlayer;

public abstract interface IScoreHandler<PlayerType extends IGamePlayer>
{
  public abstract void RewardForDeath(PlayerType paramPlayerType);
}
