package nautilus.game.core.arena;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public abstract interface IArena
{
  public abstract World GetWorld();
  
  public abstract Region GetBounds();
  
  public abstract boolean IsInArena(Vector paramVector);
  
  public abstract boolean IsChunkInArena(int paramInt1, int paramInt2);
  
  public abstract boolean CanMove(String paramString, Vector paramVector1, Vector paramVector2);
  
  public abstract boolean CanInteract(String paramString, Block paramBlock);
  
  public abstract boolean LoadArena(long paramLong);
  
  public abstract void Deactivate();
  
  public abstract String GetName();
  
  public abstract void SetMapName(String paramString);
  
  public abstract void SetBounds(Region paramRegion);
  
  public abstract void SetCenter(Vector paramVector);
  
  public abstract void SetOffset(Vector paramVector);
}
