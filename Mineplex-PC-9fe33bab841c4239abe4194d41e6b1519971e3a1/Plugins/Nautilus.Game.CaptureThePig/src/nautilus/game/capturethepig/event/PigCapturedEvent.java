package nautilus.game.capturethepig.event;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;

public class PigCapturedEvent extends PigEvent 
{
	private ICaptureThePigPlayer _capturer;
	
	public PigCapturedEvent(ICaptureThePigGame game, ICaptureThePigPlayer capturer) 
	{
		super(game);
		
		_capturer = capturer;
	}

	public ICaptureThePigPlayer GetCapturer() 
	{
		return _capturer;
	}
}
