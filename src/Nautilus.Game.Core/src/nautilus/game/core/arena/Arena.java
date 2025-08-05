package nautilus.game.core.arena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mineplex.core.common.util.WorldUtil;
import net.minecraft.server.v1_6_R2.ChunkCoordinates;

import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class Arena implements IArena
{
	private Region _bounds;
	
	protected String Name;
	protected World World;
	protected List<Region> Regions;	
	protected Set<IArenaReloadedListener> Listeners = new HashSet<IArenaReloadedListener>();
	
	protected int ChunkX;
	protected int ChunkZ;
	protected int MaxChunkX;
	protected int MaxChunkZ;
	protected int MinChunkX;
	protected int MinChunkZ;
	
	protected int SpawnChunkX = -196;
	protected int SpawnChunkZ = -196;
	protected int SpawnMaxChunkX = 196;
	protected int SpawnMaxChunkZ = 196;
	
	public Arena(String fileName)
	{
	    Name = fileName;
		
		Regions = new ArrayList<Region>();
		
    	World = WorldUtil.LoadWorld(WorldCreator.name(fileName).environment(org.bukkit.World.Environment.NORMAL).generateStructures(false));
	}
	
	public String MapName;
	public Vector Center;
	public Vector Offset;
	
	public boolean LoadArena(long maxMilliseconds)
	{
		long startTime = System.currentTimeMillis();

    	ChunkCoordinates chunkcoordinates = new ChunkCoordinates(((CraftWorld)World).getHandle().worldData.c(), ((CraftWorld)World).getHandle().worldData.d(), ((CraftWorld)World).getHandle().worldData.e());
        for (; SpawnChunkX <= SpawnMaxChunkX; SpawnChunkX += 16) 
        {
            for (; SpawnChunkZ <= SpawnMaxChunkZ; SpawnChunkZ += 16) 
            {
    			if (System.currentTimeMillis() - startTime >= maxMilliseconds)
    				return false;
                
    			((CraftWorld)World).getHandle().chunkProviderServer.getChunkAt(chunkcoordinates.x + SpawnChunkX >> 4, chunkcoordinates.z + SpawnChunkZ >> 4);
            }
            
            SpawnChunkZ = -196;
        }
		
    	for (; ChunkX <= MaxChunkX; ChunkX++)
    	{
    		for (; ChunkZ <= MaxChunkZ; ChunkZ++)
    		{
    			if (System.currentTimeMillis() - startTime >= maxMilliseconds)
    				return false;
    			
    			World.loadChunk(ChunkX, ChunkZ, false);
    		}
    		
    		ChunkZ = MinChunkZ;
    	}
    	
    	return true;
	}
	
	public void Deactivate()
	{
		_bounds = null;
		World = null;
		
		Regions.clear();
		Regions = null;
		
		Listeners.clear();
		Listeners = null;
	}
	
	public World GetWorld()
	{
		return World;
	}
	
	public Region GetBounds()
	{
		return _bounds;
	}
	
	public void SetBounds(Region region)
	{
		_bounds = region;
		_bounds.SetPriority(-1);
		Regions.add(_bounds);
		
		UpdateChunkVars(_bounds);
		Vector midPoint = _bounds.GetMidPoint();
		World.setSpawnLocation(midPoint.getBlockX(), midPoint.getBlockY(), midPoint.getBlockZ());
	}
	
	public boolean IsInArena(Vector location)
	{
		return _bounds.Contains(location);
	}
	
	public boolean IsChunkInArena(int x, int z)
	{
		return x >= MinChunkX && z >= MinChunkZ && x <= MaxChunkX && z <= MaxChunkZ; 
	}
	
	public boolean CanMove(String playerName, Vector from, Vector to)
	{				
		boolean inRegions = false;
		boolean canMove = false;
		int priority = -999;
		
		for (Region region : Regions)
		{
			if (region.Contains(to))
			{
				inRegions = true;
			}
			
			if (region.GetPriority() > priority)
			{
				if (region.Contains(to))
				{
					canMove = region.CanEnter(playerName);
					priority = region.GetPriority();
				}
			}
		}
		
		if (!inRegions)
		{
			return false;
		}
		
		return canMove;
	}
	
	public boolean CanInteract(String playerName, Block block)
	{	
		boolean inRegions = false;
		boolean canInteract = false;
		int priority = -999;
		
		for (Region region : Regions)
		{
			if (region.Contains(block.getLocation().toVector()))
			{
				inRegions = true;
			}
			
			if (region.GetPriority() > priority)
			{
				if (region.Contains(block.getLocation().toVector()))
				{
					canInteract = region.CanChangeBlocks(playerName);
					priority = region.GetPriority();
				}
			}
		}
		
		if (!inRegions)
		{
			return false;
		}			
		
		return canInteract;
	}
	
	public void ClearRegionOwners()
	{
		for (Region region : Regions)
		{
			region.SetOwners(new ArrayList<String>());
		}
	}
	
	public void ClearItems()
	{
		List<Entity> entities = World.getEntities();
		
		for (Entity entity : entities)
		{
			if (entity instanceof Item)
			{
				if (_bounds.Contains(entity.getLocation().toVector()))
				{
					entity.remove();
				}
			}
		}
	}
	
	public void UpdateChunkVars(Region region)
	{
        if (region.GetMinimumPoint().getBlockX() >> 4 < ChunkX)
        	ChunkX = region.GetMinimumPoint().getBlockX() >> 4;
        
        if (region.GetMaximumPoint().getBlockX() >> 4 > MaxChunkX)
        	MaxChunkX = region.GetMaximumPoint().getBlockX() >> 4;
    	
    	if (region.GetMinimumPoint().getBlockZ() >> 4 < ChunkZ)
    		ChunkZ = region.GetMinimumPoint().getBlockZ() >> 4;
    	
    	if (region.GetMaximumPoint().getBlockZ() >> 4 > MaxChunkZ)
    		MaxChunkZ = region.GetMaximumPoint().getBlockZ() >> 4;
    		
    	MinChunkX = ChunkX;
		MinChunkZ = ChunkZ;
	}

	
	public void AddListener(IArenaReloadedListener listener)
	{
		Listeners.add(listener);
	}

    @Override
    public void SetMapName(String value)
    {
        MapName = value;
    }

    @Override
    public void SetCenter(Vector center)
    {
        Center = center;
    }

    @Override
    public void SetOffset(Vector offset)
    {
        Offset = offset;
    }

    @Override
    public String GetName()
    {
        return MapName;
    }
}
