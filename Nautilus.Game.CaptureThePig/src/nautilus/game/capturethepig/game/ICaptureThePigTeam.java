package nautilus.game.capturethepig.game;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;

import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.ITeam;

public interface ICaptureThePigTeam extends ITeam<ICaptureThePigPlayer> 
{
	Region GetPigPen();
	void SetPigPen(Region pigPen);
	void CapturePig(Entity pig);
	Entity RemovePig();
	void ReturnPig();
	boolean HasPig();
	void SpawnPig(Pig pig);
	Pig GetPig();
}
