package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;

public abstract interface IProperty<ArenaType extends IArena>
{
  public abstract String GetName();
  
  public abstract boolean Parse(ArenaType paramArenaType, String paramString);
}
