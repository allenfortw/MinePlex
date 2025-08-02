package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;

public interface IProperty<ArenaType extends IArena>
{
	String GetName();
	
	boolean Parse(ArenaType arena, String value);
}
