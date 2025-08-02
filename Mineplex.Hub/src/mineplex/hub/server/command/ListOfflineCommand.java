package mineplex.hub.server.command;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.hub.server.ServerManager;

public class ListOfflineCommand extends CommandBase<ServerManager>
{
	public ListOfflineCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "listoffline");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args != null)
		{
			Plugin.Help(caller, "Invalid arguments");
			return;
		}
		
		Plugin.ListOfflineServers(caller);
	}
}