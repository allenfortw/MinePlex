package mineplex.hub.server;

import java.util.Comparator;

public class ServerSorter implements Comparator<ServerInfo>
{
	public int compare(ServerInfo a, ServerInfo b) 
	{
		if ((a.MOTD.contains("Restarting")))
			return 1;
		
		if ((b.MOTD.contains("Restarting")))
			return -1;
		
		if ((a.MOTD.contains("Recruiting") || a.MOTD.contains("Waiting") || a.MOTD.contains("Cup")) && !b.MOTD.contains("Recruiting") && !b.MOTD.contains("Waiting") && !b.MOTD.contains("Cup"))
			return -1;

		if ((b.MOTD.contains("Recruiting") || b.MOTD.contains("Waiting") || b.MOTD.contains("Cup")) && !a.MOTD.contains("Recruiting") && !a.MOTD.contains("Waiting") && !a.MOTD.contains("Cup"))
			return 1;
		
		if (a.CurrentPlayers > b.CurrentPlayers)
			return -1;
		
		if (b.CurrentPlayers > a.CurrentPlayers)
			return 1;
		
		if (Integer.parseInt(a.Name.split("-")[1]) < Integer.parseInt(b.Name.split("-")[1]))
			return -1;

		return 1;
	}
}