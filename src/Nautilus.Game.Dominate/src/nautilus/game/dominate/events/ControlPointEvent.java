package nautilus.game.dominate.events;

import nautilus.game.core.events.GameEvent;
import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;

public class ControlPointEvent extends GameEvent<IDominateGame>
{
    private IControlPoint _controlPoint;
 
    public ControlPointEvent(IDominateGame game, IControlPoint controlPoint)
    {
        super(game);
        _controlPoint = controlPoint;
    }
 
    public IControlPoint GetControlPoint()
    {
        return _controlPoint;
    }
}
