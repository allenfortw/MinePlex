package nautilus.game.dominate.engine;

import java.util.ArrayList;
import java.util.List;

import nautilus.game.core.engine.TeamType;
import nautilus.game.core.game.Team;
import nautilus.game.dominate.player.IDominatePlayer;

public class DominateTeam extends Team<IDominatePlayer, IDominateTeam> implements IDominateTeam
{
    private List<IControlPoint> _controlPoints;
    
    public DominateTeam(TeamType teamType)
    {
    	super(teamType);

        _controlPoints = new ArrayList<IControlPoint>();
    }
    
    @Override
    public void AddPlayer(IDominatePlayer player)
    {
        player.SetTeam(this);
        Players.add(player);
    }

    @Override
    public void AddControlPoint(IControlPoint point)
    {
        _controlPoints.add(point);
    }
    
    @Override
    public void RemoveControlPoint(IControlPoint point)
    {
        _controlPoints.remove(point);
    }
    
    @Override
    public List<IControlPoint> GetControlPoints()
    {
        return _controlPoints;
    }
}
