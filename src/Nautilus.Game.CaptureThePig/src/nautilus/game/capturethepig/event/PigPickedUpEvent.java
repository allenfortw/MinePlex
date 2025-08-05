package nautilus.game.capturethepig.event;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;

public class PigPickedUpEvent extends PigEvent 
{
	private ICaptureThePigTeam _teamOwner;
	
	public PigPickedUpEvent(ICaptureThePigGame game, ICaptureThePigTeam teamOwner) 
	{
		super(game);
		
		_teamOwner = teamOwner;
	}

	public ICaptureThePigTeam GetTeamOwner() 
	{
		return _teamOwner;
	}
}