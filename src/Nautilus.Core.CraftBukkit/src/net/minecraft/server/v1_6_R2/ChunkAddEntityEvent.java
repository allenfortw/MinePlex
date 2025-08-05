package net.minecraft.server.v1_6_R2;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChunkAddEntityEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private org.bukkit.entity.Entity _entity;
    
    public ChunkAddEntityEvent(org.bukkit.entity.Entity entity)
    {
    	_entity = entity;
    }
 
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public org.bukkit.entity.Entity GetEntity()
    {
    	return _entity;
    }
}