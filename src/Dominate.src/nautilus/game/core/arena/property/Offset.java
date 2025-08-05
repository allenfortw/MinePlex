package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;

public class Offset<ArenaType extends IArena> extends PropertyBase<ArenaType>
{
  public Offset()
  {
    super("offset");
  }
  

  public boolean Parse(ArenaType arena, String value)
  {
    arena.SetOffset(ParseVector(value));
    
    return true;
  }
}
