package nautilus.game.core.arena;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TeamArena
  extends Arena
  implements ITeamArena
{
  protected Region BlueSpawnRoom;
  protected Region RedSpawnRoom;
  protected List<Location> RedSpawnPoints;
  protected List<Location> BlueSpawnPoints;
  protected List<Location> RedShopPoints;
  protected List<Location> BlueShopPoints;
  
  public TeamArena(String name)
  {
    super(name);
    
    this.RedSpawnPoints = new ArrayList();
    this.BlueSpawnPoints = new ArrayList();
    
    this.RedShopPoints = new ArrayList();
    this.BlueShopPoints = new ArrayList();
  }
  

  public void Deactivate()
  {
    super.Deactivate();
    
    this.RedSpawnPoints.clear();
    this.RedSpawnPoints = null;
    
    this.BlueSpawnPoints.clear();
    this.BlueSpawnPoints = null;
    
    this.RedShopPoints.clear();
    this.RedShopPoints = null;
    
    this.BlueShopPoints.clear();
    this.BlueShopPoints = null;
  }
  

  public boolean IsInArena(Vector location)
  {
    return (super.IsInArena(location)) || (this.RedSpawnRoom.Contains(location).booleanValue()) || (this.BlueSpawnRoom.Contains(location).booleanValue());
  }
  
  public void AddRedSpawnPoint(Vector spawnPoint, float yaw)
  {
    this.RedSpawnPoints.add(new Location(this.World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0.0F));
  }
  
  public List<Location> GetRedSpawnPoints()
  {
    return this.RedSpawnPoints;
  }
  
  public void AddBlueSpawnPoint(Vector spawnPoint, float yaw)
  {
    this.BlueSpawnPoints.add(new Location(this.World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0.0F));
  }
  
  public List<Location> GetBlueSpawnPoints()
  {
    return this.BlueSpawnPoints;
  }
  

  public void SetBlueSpawnRoom(Region region)
  {
    this.Regions.add(region);
    this.BlueSpawnRoom = region;
    
    UpdateChunkVars(region);
  }
  

  public void SetRedSpawnRoom(Region region)
  {
    this.Regions.add(region);
    this.RedSpawnRoom = region;
    
    UpdateChunkVars(region);
  }
  

  public Region GetBlueSpawnRoom()
  {
    return this.BlueSpawnRoom;
  }
  

  public Region GetRedSpawnRoom()
  {
    return this.RedSpawnRoom;
  }
  

  public void AddBlueGameShopPoint(Vector spawnPoint, float yaw)
  {
    this.BlueShopPoints.add(new Location(this.World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0.0F));
  }
  

  public void AddRedGameShopPoint(Vector spawnPoint, float yaw)
  {
    this.RedShopPoints.add(new Location(this.World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0.0F));
  }
  

  public List<Location> GetBlueShopPoints()
  {
    return this.BlueShopPoints;
  }
  

  public List<Location> GetRedShopPoints()
  {
    return this.RedShopPoints;
  }
}
