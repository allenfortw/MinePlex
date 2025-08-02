package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;

public class MapName<ArenaType extends IArena> extends PropertyBase<ArenaType> 
{
	public MapName() 
	{
		super("mapname");
	}

	@Override
	public boolean Parse(ArenaType arena, String value) 
	{
		arena.SetMapName(value);
		return true;
	}
}
