package mineplex.core.server.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerReadyEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private String _serverPath;
 
    public ServerReadyEvent(String serverName)
    {
    	_serverPath = serverName;
    }
    
    public String GetServerPath()
    {
    	return _serverPath;
    }
    
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}