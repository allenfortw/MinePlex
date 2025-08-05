package mineplex.hub.server.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.server.ServerManager;

public class CreateCommand extends CommandBase<ServerManager>
{
	public CreateCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "create");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args == null || args.length == 0)
		{
			Plugin.Help(caller, "Invalid name for servernpc");
			return;
		}
		
		String serverNpcName = args[0];
		
		for (int i = 1; i < args.length; i++)
		{
			serverNpcName += " " + args[i];
		}
		
		if (Plugin.HasServerNpc(serverNpcName))
		{
			UtilPlayer.message(caller, F.main(Plugin.GetName(), ChatColor.RED + "That ServerNpc already exists."));
		}
		else
		{
			Plugin.AddServerNpc(serverNpcName);
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Created '" + serverNpcName + "' server npc."));
		}
	}
}
