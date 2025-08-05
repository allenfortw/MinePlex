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
    
    this._teamOwner = teamOwner;
    this._enemyTeam = enemyTeam;
  }
  
  public IDominateTeam GetTeamOwner()
  {
    return this._teamOwner;
  }
  
  public IDominateTeam GetEnemyTeam()
  {
    return this._enemyTeam;
  }
}
