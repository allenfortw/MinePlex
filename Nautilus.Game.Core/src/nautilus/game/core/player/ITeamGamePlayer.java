package nautilus.game.core.player;

import nautilus.game.core.engine.ITeam;

public interface ITeamGamePlayer<Team extends ITeam<? extends ITeamGamePlayer<Team>>> extends IGamePlayer
{
    Team GetTeam();
    void SetTeam(Team team);
}
