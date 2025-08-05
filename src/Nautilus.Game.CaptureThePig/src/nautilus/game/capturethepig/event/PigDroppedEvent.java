package nautilus.game.capturethepig.event;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;

public class PigDroppedEvent extends PigEvent 
{
	private ICaptureThePigTeam _previousTeamOwner;
	
	public PigDroppedEvent(ICaptureThePigGame game, ICaptureThePigTeam previousTeamOwner) 
	{
		super(game);
		
		_previousTeamOwner = previousTeamOwner;
	}

	public ICaptureThePigTeam GetPreviousTeamOwner() 
	{
		return _previousTeamOwner;
	}
}