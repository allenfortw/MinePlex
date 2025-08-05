package nautilus.game.core.player;

import nautilus.game.core.engine.ITeam;

public abstract interface ITeamGamePlayer<Team extends ITeam<? extends ITeamGamePlayer<Team>>>
  extends IGamePlayer
{
  public abstract Team GetTeam();
  
  public abstract void SetTeam(Team paramTeam);
}
