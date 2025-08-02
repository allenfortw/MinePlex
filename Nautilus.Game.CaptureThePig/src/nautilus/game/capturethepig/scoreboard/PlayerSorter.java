package nautilus.game.capturethepig.scoreboard;

import java.util.Comparator;

import nautilus.game.capturethepig.player.ICaptureThePigPlayer;

public class PlayerSorter implements Comparator<ICaptureThePigPlayer>
{
	public int compare(ICaptureThePigPlayer a, ICaptureThePigPlayer b) 
	{		
		if (a.GetCaptures() > b.GetCaptures())
			return -1;
	
		boolean capturesEqual = a.GetCaptures() == b.GetCaptures();
		
		if (capturesEqual && a.GetKills() > b.GetKills())
			return -1;
		
		boolean killsEqual = a.GetKills() == b.GetKills();
		
		if (capturesEqual && killsEqual && a.GetAssists() > b.GetAssists())
			return -1;
		
		boolean assistsEqual = a.GetAssists() == b.GetAssists();
		
		if (capturesEqual && killsEqual && assistsEqual && a.GetDeaths() < b.GetDeaths())
			return -1;
		
		if (capturesEqual && killsEqual && assistsEqual && a.GetDeaths() == b.GetDeaths())
			return 0;
		
		return 1;
	}
}