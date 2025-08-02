package nautilus.game.capturethepig.event;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;

public class PigStolenEvent extends PigEvent 
{
	private ICaptureThePigPlayer _thief;
	private ICaptureThePigTeam _previousTeamOwner;
	
	public PigStolenEvent(ICaptureThePigGame game, ICaptureThePigPlayer thief, ICaptureThePigTeam previousTeamOwner) 
	{
		super(game);
		
		_thief = thief;
		_previousTeamOwner = previousTeamOwner;
	}

	public ICaptureThePigPlayer GetThief() 
	{
		return _thief;
	}
	
	public ICaptureThePigTeam GetPreviousTeamOwner() 
	{
		return _previousTeamOwner;
	}
}