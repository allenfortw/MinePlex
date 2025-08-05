package mineplex.hub.server.command;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.hub.server.ServerManager;

public class ListServersCommand extends CommandBase<ServerManager>
{
	public ListServersCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "listservers");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args == null || args.length < 1)
		{
			Plugin.Help(caller, "Invalid arguments");
			return;
		}
		
		String serverNpcName = args[0];
		
		for (int i = 1; i < args.length; i++)
		{
			serverNpcName += " " + args[i];
		}
		
		Plugin.ListServers(caller, serverNpcName);
	}
}