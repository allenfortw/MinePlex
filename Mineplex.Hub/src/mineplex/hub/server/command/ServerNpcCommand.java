package mineplex.hub.server.command;

import org.bukkit.entity.Player;

import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import mineplex.hub.server.ServerManager;

public class ServerNpcCommand extends MultiCommandBase<ServerManager>
{
	public ServerNpcCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "servernpc");

		AddCommand(new CreateCommand(plugin));
		AddCommand(new DeleteCommand(plugin));
		AddCommand(new AddServerCommand(plugin));
		AddCommand(new RemoveServerCommand(plugin));
		AddCommand(new ListNpcsCommand(plugin));
		AddCommand(new ListServersCommand(plugin));
		AddCommand(new ListOfflineCommand(plugin));
	}

	@Override
	protected void Help(Player caller, String args[])
	{
		Plugin.Help(caller);
	}
}