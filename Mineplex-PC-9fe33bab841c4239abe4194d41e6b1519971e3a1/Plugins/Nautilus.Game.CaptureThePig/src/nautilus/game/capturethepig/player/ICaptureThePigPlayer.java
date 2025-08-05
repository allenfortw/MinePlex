package nautilus.game.capturethepig.player;

import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.core.player.ITeamGamePlayer;

public interface ICaptureThePigPlayer extends ITeamGamePlayer<ICaptureThePigTeam> 
{
	void AddCapture();
	int GetCaptures();	
}
