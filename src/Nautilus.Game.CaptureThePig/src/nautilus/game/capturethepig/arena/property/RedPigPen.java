package nautilus.game.capturethepig.arena.property;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.core.arena.property.RegionPropertyBase;

public class RedPigPen extends RegionPropertyBase<ICaptureThePigArena>
{
    public RedPigPen() 
    {
        super("redpigpen");
    }

    @Override
    public boolean Parse(ICaptureThePigArena arena, String value) 
    {
        arena.SetRedPigPen(ParseRegion(value));
        
        return true;
    }
}