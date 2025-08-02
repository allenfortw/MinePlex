package nautilus.game.capturethepig.arena.property;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.core.arena.property.RegionPropertyBase;

public class BluePigPen extends RegionPropertyBase<ICaptureThePigArena>
{
    public BluePigPen() 
    {
        super("bluepigpen");
    }

    @Override
    public boolean Parse(ICaptureThePigArena arena, String value) 
    {
        arena.SetBluePigPen(ParseRegion(value));
        
        return true;
    }
}