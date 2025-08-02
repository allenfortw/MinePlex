package nautilus.game.dominate.engine;

import java.util.ArrayList;
import java.util.List;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.game.Team;
import nautilus.game.dominate.player.IDominatePlayer;

public class DominateTeam
  extends Team<IDominatePlayer, IDominateTeam> implements IDominateTeam
{
  private List<IControlPoint> _controlPoints;
  
  public DominateTeam(TeamType teamType)
  {
    super(teamType);
    
    this._controlPoints = new ArrayList();
  }
  

  public void AddPlayer(IDominatePlayer player)
  {
    player.SetTeam(this);
    this.Players.add(player);
  }
  

  public void AddControlPoint(IControlPoint point)
  {
    this._controlPoints.add(point);
  }
  

  public void RemoveControlPoint(IControlPoint point)
  {
    this._controlPoints.remove(point);
  }
  

  public List<IControlPoint> GetControlPoints()
  {
    return this._controlPoints;
  }
}
