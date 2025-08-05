package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;

public class Center<ArenaType extends IArena> extends PropertyBase<ArenaType>
{
  public Center()
  {
    super("center");
  }
  

  public boolean Parse(ArenaType arena, String value)
  {
    arena.SetCenter(ParseVector(value));
    
    return true;
  }
}
