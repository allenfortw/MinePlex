package mineplex.hub.server.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.server.ServerManager;

public class AddServerCommand extends CommandBase<ServerManager>
{
	public AddServerCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "addserver");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args.length < 2)
		{
			Plugin.Help(caller, "Invalid arguments");
			return;
		}
		
		String argsCombined = args[0];
		String serverNpcName = "";
		String serverName = "";
		
		for (int i = 1; i < args.length; i++)
		{
			argsCombined += " " + args[i];
		}
		
		if (!argsCombined.contains("|"))
		{
			Plugin.Help(caller, "Invalid arguments");
		}
		
		serverNpcName = argsCombined.substring(0, argsCombined.indexOf("|")).trim();
		serverName = argsCombined.substring(argsCombined.indexOf("|") + 1).trim();
		
		if (!Plugin.HasServerNpc(serverNpcName))
		{
			UtilPlayer.message(caller, F.main(Plugin.GetName(), ChatColor.RED + "That ServerNpc doesn't exist."));
		}
		else
		{
			Plugin.AddServer(serverNpcName, serverName);
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Added '" + serverName + "' to '" + serverNpcName + "' server list."));
		}
	}
}