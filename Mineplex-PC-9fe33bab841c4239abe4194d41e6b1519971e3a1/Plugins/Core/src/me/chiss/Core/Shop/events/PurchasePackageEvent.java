package me.chiss.Core.Shop.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PurchasePackageEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean _cancelled = false;
 
    private String _playerName;
    private String _itemName;
    private String _reason;
    
    public PurchasePackageEvent(String player, String item)
    {
    	_playerName = player;
    	_itemName = item;
    }
    
    public String GetPlayerName()
    {
    	return _playerName;
    }

    public String GetItemName()
    {
    	return _itemName;
    }
    
	public String GetReason()
	{
		return _reason;
	}
	
    public void SetReason(String reason)
    {
        _reason = reason;
    }
    
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return _cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        _cancelled = cancel;
    }
}
