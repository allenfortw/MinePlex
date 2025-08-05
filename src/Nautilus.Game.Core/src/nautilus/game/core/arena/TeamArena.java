package nautilus.game.core.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TeamArena extends Arena implements ITeamArena
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
		
		RedSpawnPoints = new ArrayList<Location>();
		BlueSpawnPoints = new ArrayList<Location>();
		
		RedShopPoints = new ArrayList<Location>();
		BlueShopPoints = new ArrayList<Location>();
	}
	
	@Override
	public void Deactivate()
	{
		super.Deactivate();
		
		RedSpawnPoints.clear();
		RedSpawnPoints = null;
		
		BlueSpawnPoints.clear();
		BlueSpawnPoints = null;
		
		RedShopPoints.clear();
		RedShopPoints = null;
		
		BlueShopPoints.clear();
		BlueShopPoints = null;
	}
	
	@Override
	public boolean IsInArena(Vector location)
	{
		return super.IsInArena(location) || RedSpawnRoom.Contains(location) || BlueSpawnRoom.Contains(location);
	}
	
	public void AddRedSpawnPoint(Vector spawnPoint, float yaw)
	{
	    RedSpawnPoints.add(new Location(World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0f));
	}
	
	public List<Location> GetRedSpawnPoints()
	{
		return RedSpawnPoints;
	}
	
	public void AddBlueSpawnPoint(Vector spawnPoint, float yaw)
	{
		BlueSpawnPoints.add(new Location(World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0f));
	}
	
	public List<Location> GetBlueSpawnPoints()
	{
		return BlueSpawnPoints;
	}

    @Override
    public void SetBlueSpawnRoom(Region region)
    {
        Regions.add(region);
        BlueSpawnRoom = region;
        
        UpdateChunkVars(region);
    }

    @Override
    public void SetRedSpawnRoom(Region region)
    {
        Regions.add(region);
        RedSpawnRoom = region;
        
        UpdateChunkVars(region);
    }

    @Override
    public Region GetBlueSpawnRoom()
    {
        return BlueSpawnRoom;
    }

    @Override
    public Region GetRedSpawnRoom()
    {
        return RedSpawnRoom;
    }

	@Override
	public void AddBlueGameShopPoint(Vector spawnPoint, float yaw)
	{
		BlueShopPoints.add(new Location(World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0f));
	}

	@Override
	public void AddRedGameShopPoint(Vector spawnPoint, float yaw)
	{
		RedShopPoints.add(new Location(World, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), yaw, 0f));
	}

	@Override
	public List<Location> GetBlueShopPoints()
	{
		return BlueShopPoints;
	}

	@Override
	public List<Location> GetRedShopPoints()
	{
		return RedShopPoints;
	}
}
