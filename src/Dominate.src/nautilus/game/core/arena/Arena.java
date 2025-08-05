package nautilus.game.core.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mineplex.core.common.util.WorldUtil;
import net.minecraft.server.v1_6_R3.ChunkCoordinates;
import net.minecraft.server.v1_6_R3.ChunkProviderServer;
import net.minecraft.server.v1_6_R3.WorldData;
import net.minecraft.server.v1_6_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Arena implements IArena
{
  private Region _bounds;
  protected String Name;
  protected World World;
  protected List<Region> Regions;
  protected Set<IArenaReloadedListener> Listeners = new java.util.HashSet();
  
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
  public String MapName;
  
  public Arena(String fileName) {
    this.Name = fileName;
    
    this.Regions = new ArrayList();
    
    this.World = WorldUtil.LoadWorld(WorldCreator.name(fileName).environment(World.Environment.NORMAL).generateStructures(false));
  }
  

  public Vector Center;
  
  public Vector Offset;
  public boolean LoadArena(long maxMilliseconds)
  {
    long startTime = System.currentTimeMillis();
    
    ChunkCoordinates chunkcoordinates = new ChunkCoordinates(((CraftWorld)this.World).getHandle().worldData.c(), ((CraftWorld)this.World).getHandle().worldData.d(), ((CraftWorld)this.World).getHandle().worldData.e());
    for (; this.SpawnChunkX <= this.SpawnMaxChunkX; this.SpawnChunkX += 16)
    {
      for (; this.SpawnChunkZ <= this.SpawnMaxChunkZ; this.SpawnChunkZ += 16)
      {
        if (System.currentTimeMillis() - startTime >= maxMilliseconds) {
          return false;
        }
        ((CraftWorld)this.World).getHandle().chunkProviderServer.getChunkAt(chunkcoordinates.x + this.SpawnChunkX >> 4, chunkcoordinates.z + this.SpawnChunkZ >> 4);
      }
      
      this.SpawnChunkZ = -196;
    }
    for (; 
        this.ChunkX <= this.MaxChunkX; this.ChunkX += 1)
    {
      for (; this.ChunkZ <= this.MaxChunkZ; this.ChunkZ += 1)
      {
        if (System.currentTimeMillis() - startTime >= maxMilliseconds) {
          return false;
        }
        this.World.loadChunk(this.ChunkX, this.ChunkZ, false);
      }
      
      this.ChunkZ = this.MinChunkZ;
    }
    
    return true;
  }
  
  public void Deactivate()
  {
    this._bounds = null;
    this.World = null;
    
    this.Regions.clear();
    this.Regions = null;
    
    this.Listeners.clear();
    this.Listeners = null;
  }
  
  public World GetWorld()
  {
    return this.World;
  }
  
  public Region GetBounds()
  {
    return this._bounds;
  }
  
  public void SetBounds(Region region)
  {
    this._bounds = region;
    this._bounds.SetPriority(-1);
    this.Regions.add(this._bounds);
    
    UpdateChunkVars(this._bounds);
    Vector midPoint = this._bounds.GetMidPoint();
    this.World.setSpawnLocation(midPoint.getBlockX(), midPoint.getBlockY(), midPoint.getBlockZ());
  }
  
  public boolean IsInArena(Vector location)
  {
    return this._bounds.Contains(location).booleanValue();
  }
  
  public boolean IsChunkInArena(int x, int z)
  {
    return (x >= this.MinChunkX) && (z >= this.MinChunkZ) && (x <= this.MaxChunkX) && (z <= this.MaxChunkZ);
  }
  
  public boolean CanMove(String playerName, Vector from, Vector to)
  {
    boolean inRegions = false;
    boolean canMove = false;
    int priority = -999;
    
    for (Region region : this.Regions)
    {
      if (region.Contains(to).booleanValue())
      {
        inRegions = true;
      }
      
      if (region.GetPriority() > priority)
      {
        if (region.Contains(to).booleanValue())
        {
          canMove = region.CanEnter(playerName).booleanValue();
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
    
    for (Region region : this.Regions)
    {
      if (region.Contains(block.getLocation().toVector()).booleanValue())
      {
        inRegions = true;
      }
      
      if (region.GetPriority() > priority)
      {
        if (region.Contains(block.getLocation().toVector()).booleanValue())
        {
          canInteract = region.CanChangeBlocks(playerName).booleanValue();
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
    for (Region region : this.Regions)
    {
      region.SetOwners(new ArrayList());
    }
  }
  
  public void ClearItems()
  {
    List<Entity> entities = this.World.getEntities();
    
    for (Entity entity : entities)
    {
      if ((entity instanceof org.bukkit.entity.Item))
      {
        if (this._bounds.Contains(entity.getLocation().toVector()).booleanValue())
        {
          entity.remove();
        }
      }
    }
  }
  
  public void UpdateChunkVars(Region region)
  {
    if (region.GetMinimumPoint().getBlockX() >> 4 < this.ChunkX) {
      this.ChunkX = (region.GetMinimumPoint().getBlockX() >> 4);
    }
    if (region.GetMaximumPoint().getBlockX() >> 4 > this.MaxChunkX) {
      this.MaxChunkX = (region.GetMaximumPoint().getBlockX() >> 4);
    }
    if (region.GetMinimumPoint().getBlockZ() >> 4 < this.ChunkZ) {
      this.ChunkZ = (region.GetMinimumPoint().getBlockZ() >> 4);
    }
    if (region.GetMaximumPoint().getBlockZ() >> 4 > this.MaxChunkZ) {
      this.MaxChunkZ = (region.GetMaximumPoint().getBlockZ() >> 4);
    }
    this.MinChunkX = this.ChunkX;
    this.MinChunkZ = this.ChunkZ;
  }
  

  public void AddListener(IArenaReloadedListener listener)
  {
    this.Listeners.add(listener);
  }
  

  public void SetMapName(String value)
  {
    this.MapName = value;
  }
  

  public void SetCenter(Vector center)
  {
    this.Center = center;
  }
  

  public void SetOffset(Vector offset)
  {
    this.Offset = offset;
  }
  

  public String GetName()
  {
    return this.MapName;
  }
}
