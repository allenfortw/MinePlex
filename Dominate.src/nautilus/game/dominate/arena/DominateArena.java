package nautilus.game.dominate.arena;

import java.util.ArrayList;
import java.util.List;
import nautilus.game.core.arena.Region;
import nautilus.game.core.arena.TeamArena;
import org.bukkit.util.Vector;

public class DominateArena
  extends TeamArena
  implements IDominateArena
{
  private List<Region> _controlPointAreas;
  private List<Vector> _pointPowerUpPoints;
  private List<Vector> _resupplyPowerUpPoints;
  
  public DominateArena(String name)
  {
    super(name);
    
    this._controlPointAreas = new ArrayList();
    this._pointPowerUpPoints = new ArrayList();
    this._resupplyPowerUpPoints = new ArrayList();
  }
  

  public void Deactivate()
  {
    super.Deactivate();
    
    this._controlPointAreas.clear();
    this._controlPointAreas = null;
    
    this._pointPowerUpPoints.clear();
    this._pointPowerUpPoints = null;
    
    this._resupplyPowerUpPoints.clear();
    this._resupplyPowerUpPoints = null;
  }
  

  public void AddControlPointArea(Region region)
  {
    this.Regions.add(region);
    this._controlPointAreas.add(region);
  }
  

  public List<Region> GetControlPointAreas()
  {
    return this._controlPointAreas;
  }
  

  public void AddResupplyPowerUp(Vector parseVector)
  {
    this._resupplyPowerUpPoints.add(parseVector);
  }
  

  public List<Vector> GetResupplyPowerUpPoints()
  {
    return this._resupplyPowerUpPoints;
  }
  

  public void AddPointPowerUp(Vector parseVector)
  {
    this._pointPowerUpPoints.add(parseVector);
  }
  

  public List<Vector> GetPointPowerUpPoints()
  {
    return this._pointPowerUpPoints;
  }
}
