package nautilus.game.dominate.engine;

import java.util.List;
import nautilus.game.core.engine.ITeam;
import nautilus.game.dominate.player.IDominatePlayer;

public abstract interface IDominateTeam
  extends ITeam<IDominatePlayer>
{
  public abstract void RemoveControlPoint(IControlPoint paramIControlPoint);
  
  public abstract void AddControlPoint(IControlPoint paramIControlPoint);
  
  public abstract List<IControlPoint> GetControlPoints();
}
