package net.minecraft.server.v1_6_R2;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChunkPreLoadEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean _cancelled;
    private org.bukkit.World _world;
    private int _x;
    private int _z;
    
    public ChunkPreLoadEvent(org.bukkit.World world, int x, int z)
    {
    	_world = world;
    	_x = x;
    	_z = z;
    }
 
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public org.bukkit.World GetWorld()
    {
    	return _world;
    }
    
	public int GetX() 
	{
		return _x;
	}

	public int GetZ() 
	{
		return _z;
	}

	@Override
	public boolean isCancelled() 
	{
		return _cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) 
	{
		_cancelled = arg0;
	}
}