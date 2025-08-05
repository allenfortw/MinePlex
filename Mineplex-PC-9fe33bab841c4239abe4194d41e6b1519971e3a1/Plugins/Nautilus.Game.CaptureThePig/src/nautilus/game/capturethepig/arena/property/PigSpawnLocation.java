package nautilus.game.capturethepig.arena.property;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.core.arena.property.PropertyBase;

public class PigSpawnLocation extends PropertyBase<ICaptureThePigArena>
{
    public PigSpawnLocation()
    {
        super("pigspawnlocation");
    }

    public boolean Parse(ICaptureThePigArena arena, String value) 
    {
    	arena.SetPigSpawnPoint(ParseVector(value));
        
        return true;
    }
}