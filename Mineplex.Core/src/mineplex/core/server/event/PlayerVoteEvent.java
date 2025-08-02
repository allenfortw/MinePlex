package mineplex.core.server.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVoteEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private String _playerName;
    private int _pointsReceived;
 
    public PlayerVoteEvent(String playerName, int pointsReceived)
    {
    	_playerName = playerName;
    	_pointsReceived = pointsReceived;
    }
    
    public String GetPlayerName()
    {
    	return _playerName;
    }
    
    public int GetPointsReceived()
    {
    	return _pointsReceived;
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