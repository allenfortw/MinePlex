package nautilus.game.core.arena;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract interface ITeamArena
  extends IArena
{
  public abstract void AddRedSpawnPoint(Vector paramVector, float paramFloat);
  
  public abstract List<Location> GetRedSpawnPoints();
  
  public abstract void AddBlueSpawnPoint(Vector paramVector, float paramFloat);
  
  public abstract List<Location> GetBlueSpawnPoints();
  
  public abstract void SetBlueSpawnRoom(Region paramRegion);
  
  public abstract void SetRedSpawnRoom(Region paramRegion);
  
  public abstract Region GetBlueSpawnRoom();
  
  public abstract Region GetRedSpawnRoom();
  
  public abstract void AddBlueGameShopPoint(Vector paramVector, float paramFloat);
  
  public abstract List<Location> GetBlueShopPoints();
  
  public abstract void AddRedGameShopPoint(Vector paramVector, float paramFloat);
  
  public abstract List<Location> GetRedShopPoints();
}
