package mineplex.core.message.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.MessageManager;

public class ResendCommand extends CommandBase<MessageManager>
{
	public ResendCommand(MessageManager plugin)
	{
		super(plugin, Rank.ALL, "r");
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
			//Get To
			if (Plugin.Get(caller).LastTo == null)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), "You have not messaged anyone recently."));
				return;
			}

			Player to = UtilPlayer.searchOnline(caller, Plugin.Get(caller).LastTo, false);
			if (to == null)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), F.name(Plugin.Get(caller).LastTo) + " is no longer online."));
				return;
			}

			//Parse Message
			String message = "Beep!";
			if (args.length > 0)
			{
				message = F.combine(args, 0, null, false);
			}
			else 
			{
				message = Plugin.GetRandomMessage();
			}

			//Send
			Plugin.DoMessage(caller, to, message);
		}
	}
}
