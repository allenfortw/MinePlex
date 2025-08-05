package nautilus.game.dominate.events;

import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;

public class ControlPointEnemyCapturingEvent extends ControlPointEvent 
{
    private IDominateTeam _teamOwner;
    private IDominateTeam _enemyTeam;
 
    public ControlPointEnemyCapturingEvent(IDominateGame game, IControlPoint controlPoint, IDominateTeam teamOwner, IDominateTeam enemyTeam)
    {
        super(game, controlPoint);
        
        _teamOwner = teamOwner;
        _enemyTeam = enemyTeam;
    }
 
    public IDominateTeam GetTeamOwner()
    {
        return _teamOwner;
    }
    
    public IDominateTeam GetEnemyTeam()
    {
        return _enemyTeam;
    }
}
