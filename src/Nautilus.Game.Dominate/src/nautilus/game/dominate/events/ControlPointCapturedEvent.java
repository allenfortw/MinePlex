package nautilus.game.dominate.events;

import java.util.List;

import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.player.IDominatePlayer;

public class ControlPointCapturedEvent extends ControlPointEvent 
{
    private IDominateTeam _newTeamOwner;
    private List<IDominatePlayer> _playersInvolved;
 
    public ControlPointCapturedEvent(IDominateGame game, IControlPoint controlPoint, IDominateTeam newTeamOwner, List<IDominatePlayer> playersInvolved)
    {
        super(game, controlPoint);
        
        _newTeamOwner = newTeamOwner;
        _playersInvolved = playersInvolved;
    }
 
    public IDominateTeam GetNewTeamOwner()
    {
        return _newTeamOwner;
    }
    
    public List<IDominatePlayer> GetPlayersInvolved()
    {
        return _playersInvolved;
    }
}
