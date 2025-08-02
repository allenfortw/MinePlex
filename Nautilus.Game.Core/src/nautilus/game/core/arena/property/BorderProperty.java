package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.arena.Region;

public class BorderProperty<ArenaType extends IArena> extends RegionPropertyBase<ArenaType>
{	
	public BorderProperty()
	{
		super("border");
	}
	
	public boolean Parse(ArenaType arena, String value)
	{
		Region boundsRegion = ParseRegion(value);
		
		arena.SetBounds(boundsRegion);
		
		return true;
	}
}
