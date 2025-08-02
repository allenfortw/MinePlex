package mineplex.core.server.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerServerAssignmentEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private String _playerName;
    private String _serverName;
 
    public PlayerServerAssignmentEvent(String playerName, String serverName)
    {
    	_playerName = playerName;
    	_serverName = serverName;
    }
    
    public String GetPlayerName()
    {
    	return _playerName;
    }
    
    public String GetServerName()
    {
    	return _serverName;
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
