package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.arena.Region;
import org.bukkit.util.Vector;

public abstract class RegionPropertyBase<ArenaType extends IArena>
  extends PropertyBase<ArenaType>
{
  public RegionPropertyBase(String name)
  {
    super(name);
  }
  
  protected Region ParseRegion(String value)
  {
    String[] parts = value.split(",");
    
    Vector pointOne = ParseVector(parts[0].trim());
    Vector pointTwo = ParseVector(parts[1].trim());
    
    return new Region(parts.length == 3 ? parts[2].trim() : this.Name, pointOne, pointTwo);
  }
}
