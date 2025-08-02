package nautilus.game.core.arena;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface ITeamArena extends IArena
{
    void AddRedSpawnPoint(Vector spawnPoint, float yaw);
    List<Location> GetRedSpawnPoints();
    
    void AddBlueSpawnPoint(Vector spawnPoint, float yaw);
    List<Location> GetBlueSpawnPoints();
    
    void SetBlueSpawnRoom(Region region);
    void SetRedSpawnRoom(Region region);
    
    Region GetBlueSpawnRoom();
    Region GetRedSpawnRoom();
    
    void AddBlueGameShopPoint(Vector spawnPoint, float yaw);
    List<Location> GetBlueShopPoints();
    
    void AddRedGameShopPoint(Vector spawnPoint, float yaw);
    List<Location> GetRedShopPoints();
}
