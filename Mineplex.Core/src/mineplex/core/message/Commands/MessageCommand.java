package mineplex.core.message.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.MessageManager;

public class MessageCommand extends CommandBase<MessageManager>
{
	public MessageCommand(MessageManager plugin)
	{
		super(plugin, Rank.ALL, "m","msg","message","tell","t");
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
			if (args.length == 0)
			{
				UtilPlayer.message(caller, F.main(Plugin.GetName(), "Player argument missing."));
				return;
			}

			//Parse To
			Player to = UtilPlayer.searchOnline(caller, args[0], true);
			if (to == null)
				return;

			//Parse Message
			String message = "Beep!";
			if (args.length > 1)
			{
				message = F.combine(args, 1, null, false);
			}
			else 
			{
				message = Plugin.GetRandomMessage();
			}

			//Send!
			Plugin.DoMessage(caller, to, message);
		}
	}
}
