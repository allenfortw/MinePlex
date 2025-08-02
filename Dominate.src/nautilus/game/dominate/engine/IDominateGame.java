package nautilus.game.dominate.engine;

import java.util.List;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.IDominatePlayer;

public abstract interface IDominateGame
  extends ITeamGame<IDominateArena, IDominatePlayer, IDominateTeam>
{
  public abstract List<IControlPoint> GetControlPoints();
}
