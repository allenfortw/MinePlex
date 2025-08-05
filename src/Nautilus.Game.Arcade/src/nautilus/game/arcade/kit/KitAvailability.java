package nautilus.game.arcade.kit;

import org.bukkit.ChatColor;

public enum KitAvailability
{
	Free(ChatColor.YELLOW),
	Green(ChatColor.GREEN),
	Blue(ChatColor.AQUA), 
	Hide(ChatColor.YELLOW),
	Null(ChatColor.BLACK);
	
	ChatColor _color;
	
	KitAvailability(ChatColor color)
	{
		_color = color;
	}
	
	public ChatColor GetColor()
	{
		return _color;
	}
}
