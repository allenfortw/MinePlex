package nautilus.game.dominate.events;

import java.util.List;

import nautilus.game.dominate.engine.IControlPoint;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.player.IDominatePlayer;

public class ControlPointLostEvent extends ControlPointEvent 
{
    private IDominateTeam _previousOwner;
    private List<IDominatePlayer> _playersInvolved;
 
    public ControlPointLostEvent(IDominateGame game, IControlPoint controlPoint, IDominateTeam previousOwner, List<IDominatePlayer> playersInvolved)
    {
        super(game, controlPoint);
        
        _previousOwner = previousOwner;
        _playersInvolved = playersInvolved;
    }
 
    public IDominateTeam GetPreviousTeamOwner()
    {
        return _previousOwner;
    }

	public List<IDominatePlayer> GetPlayersInvolved() 
	{
		return _playersInvolved;
	}
}
