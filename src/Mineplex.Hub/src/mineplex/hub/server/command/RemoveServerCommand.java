package mineplex.hub.server.command;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.server.ServerManager;

public class RemoveServerCommand extends CommandBase<ServerManager>
{
	public RemoveServerCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "removeserver");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args.length != 1)
		{
			Plugin.Help(caller, "Invalid arguments");
			return;
		}
		
		Plugin.RemoveServer(args[0]);
		UtilPlayer.message(caller, F.main(Plugin.GetName(), "Removed '" + args[0] + "' from server list."));
	}
}