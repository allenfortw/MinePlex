package nautilus.game.core.arena.property;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.Region;
import org.bukkit.Location;

public class RedSpawnRoom<ArenaType extends ITeamArena>
  extends RegionPropertyBase<ArenaType>
{
  public RedSpawnRoom()
  {
    super("redspawnroom");
  }
  

  public boolean Parse(ArenaType arena, String value)
  {
    arena.SetRedSpawnRoom(ParseRegion(value));
    return true;
  }
  
  public boolean Parse(ArenaType arena, Location start, Location stop)
  {
    arena.SetRedSpawnRoom(new Region("redspawnroom", start.toVector(), stop.toVector()));
    
    return true;
  }
}
