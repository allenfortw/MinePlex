package nautilus.game.capturethepig.arena;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.Region;

public interface ICaptureThePigArena extends ITeamArena 
{
	Region GetRedPigPen();
	Region GetBluePigPen();
	
	Location GetPigSpawnPoint();
	
	void SetRedPigPen(Region pen);
	void SetBluePigPen(Region pen);
	
	void SetPigSpawnPoint(Vector location);
}
