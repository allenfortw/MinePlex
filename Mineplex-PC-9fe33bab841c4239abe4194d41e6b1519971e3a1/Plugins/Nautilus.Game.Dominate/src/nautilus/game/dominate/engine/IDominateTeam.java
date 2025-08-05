package nautilus.game.dominate.engine;

import java.util.List;

import nautilus.game.core.engine.ITeam;
import nautilus.game.dominate.player.IDominatePlayer;

public interface IDominateTeam extends ITeam<IDominatePlayer> 
{
    void RemoveControlPoint(IControlPoint point);
    void AddControlPoint(IControlPoint point);
    List<IControlPoint> GetControlPoints();
}
