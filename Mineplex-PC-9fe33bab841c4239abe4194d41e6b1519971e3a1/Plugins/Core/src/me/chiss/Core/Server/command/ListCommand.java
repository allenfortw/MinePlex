package me.chiss.Core.Server.command;

import org.bukkit.entity.Player;

import me.chiss.Core.Server.Server;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;

public class ListCommand extends CommandBase<Server>
{
	public ListCommand(Server plugin)
	{
		super(plugin, Rank.ALL, "list", "playerlist", "who");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		UtilPlayer.message(caller, F.main(Plugin.GetName(), "Listing Online Players:"));

		String staff = "";
		String other = "";
		for (Player cur : UtilServer.getPlayers())
		{
			if (Plugin.GetClientManager().Get(cur).GetRank().Has(cur, Rank.MODERATOR, false))
			{
				staff += C.cWhite + cur.getName() + " ";
			}
			else
			{
				other += C.cWhite + cur.getName() + " ";
			}

		}

		if (staff.length() == 0)	staff = "None";
		else						staff = staff.substring(0, staff.length() - 1);

		if (other.length() == 0)	other = "None";
		else						other = other.substring(0, other.length() - 1);

		UtilPlayer.message(caller, "§c§l" + "Staff");
		UtilPlayer.message(caller, staff);

		UtilPlayer.message(caller, "§a§l" + "Players");
		UtilPlayer.message(caller, other);
	}
}
