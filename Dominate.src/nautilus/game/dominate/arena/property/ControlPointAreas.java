package nautilus.game.dominate.arena.property;

import nautilus.game.core.arena.Region;
import nautilus.game.core.arena.property.RegionPropertyBase;
import nautilus.game.dominate.arena.IDominateArena;
import org.bukkit.Location;

public class ControlPointAreas
  extends RegionPropertyBase<IDominateArena>
{
  public ControlPointAreas()
  {
    super("controlpointareas");
  }
  

  public boolean Parse(IDominateArena arena, String value)
  {
    for (String region : value.split("&"))
    {
      arena.AddControlPointArea(ParseRegion(region));
    }
    
    return true;
  }
  
  public boolean Parse(IDominateArena arena, String name, Location start, Location stop)
  {
    arena.AddControlPointArea(new Region(name, start.toVector(), stop.toVector()));
    
    return true;
  }
}
