package mineplex.hub.server.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.server.ServerManager;

public class DeleteCommand extends CommandBase<ServerManager>
{
	public DeleteCommand(ServerManager plugin)
	{
		super(plugin, Rank.OWNER, "delete");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args.length != 1)
		{
			Plugin.Help(caller, "Invalid name for servernpc");
			return;
		}
		
		if (!Plugin.HasServerNpc(args[0]))
		{
			UtilPlayer.message(caller, F.main(Plugin.GetName(), ChatColor.RED + "That ServerNpc doesn't exist."));
		}
		else
		{
			Plugin.RemoveServerNpc(args[0]);
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Removed '" + args[0] + "' server npc."));
		}
	}
}