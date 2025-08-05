package me.chiss.Core.Plugin;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface IChat 
{
	public void HandleChat(AsyncPlayerChatEvent event, String filteredMessage);
}
