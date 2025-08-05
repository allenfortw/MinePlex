package mineplex.minecraft.BungeeSigns;

import org.bukkit.Location;

public class ServerInfo
{
	public String BungeeName;
	public String DisplayName;
	public Location Location;
	
	public ServerInfo(String bungeeName, String displayName, Location location)
	{
		BungeeName = bungeeName;
		DisplayName = displayName;
		Location = location;
	}
}