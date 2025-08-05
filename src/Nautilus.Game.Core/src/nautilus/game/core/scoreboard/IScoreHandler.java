package nautilus.game.core.scoreboard;

import nautilus.game.core.player.IGamePlayer;

public interface IScoreHandler<PlayerType extends IGamePlayer>
{
	void RewardForDeath(PlayerType player);
}
