package mineplex.core.message.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.MessageManager;

public class ResendAdminCommand extends CommandBase<MessageManager>
{
	public ResendAdminCommand(MessageManager plugin)
	{
		super(plugin, Rank.ALL, "ra");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args == null)
		{			
			Plugin.Help(caller);
		}
		else
		{
			if (!Plugin.GetClientManager().Get(caller).GetRank().Has(caller, Rank.HELPER, true))
				return;

			//Get To
			if (Plugin.Get(caller).LastAdminTo == null)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), "You have not admin messaged anyone recently."));
				return;
			}

			Player to = UtilPlayer.searchOnline(caller, Plugin.Get(caller).LastAdminTo, false);
			if (to == null)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), F.name(Plugin.Get(caller).LastAdminTo) + " is no longer online."));
				return;
			}

			//Parse Message
			if (args.length < 1)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), "Message argument missing."));
				return;
			}

			String message = F.combine(args, 0, null, false);

			//Send
			Plugin.DoMessageAdmin(caller, to, message);
		}
	}
}
