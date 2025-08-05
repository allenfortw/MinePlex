package nautilus.game.core.arena;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public interface IArena
{
	World GetWorld();
	Region GetBounds();
    boolean IsInArena(Vector location);
    boolean IsChunkInArena(int x, int z);
    boolean CanMove(String playerName, Vector from, Vector to);
    boolean CanInteract(String playerName, Block withBlock);

    boolean LoadArena(long maxMilliseconds);
    void Deactivate();
    String GetName();

    void SetMapName(String value);
    void SetBounds(Region boundsRegion);
    void SetCenter(Vector center);
    void SetOffset(Vector offset);
}
