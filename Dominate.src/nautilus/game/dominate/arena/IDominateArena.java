package nautilus.game.dominate.arena;

import java.util.List;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.Region;
import org.bukkit.util.Vector;

public abstract interface IDominateArena
  extends ITeamArena
{
  public abstract void AddControlPointArea(Region paramRegion);
  
  public abstract List<Region> GetControlPointAreas();
  
  public abstract void AddResupplyPowerUp(Vector paramVector);
  
  public abstract void AddPointPowerUp(Vector paramVector);
  
  public abstract List<Vector> GetResupplyPowerUpPoints();
  
  public abstract List<Vector> GetPointPowerUpPoints();
}
