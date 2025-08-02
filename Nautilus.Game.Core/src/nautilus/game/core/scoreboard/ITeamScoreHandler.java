package nautilus.game.core.scoreboard;

import nautilus.game.core.engine.ITeam;
import nautilus.game.core.player.ITeamGamePlayer;

public interface ITeamScoreHandler<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>> extends IScoreHandler<PlayerType>
{
	void RewardForTeamKill(PlayerType killer, PlayerType victim);
	
	void RewardForKill(PlayerType killer, PlayerType victim, int assists);
	
	void RewardForAssist(PlayerType assistant, PlayerType victim);
}
