package mineplex.core.portal.commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.portal.Portal;

public class CreateCommand extends CommandBase<Portal>
{
	public CreateCommand(Portal plugin)
	{
		super(plugin, Rank.ADMIN, "create");
	}

	@Override
	public void Execute(final Player caller, String[] args)
	{
		if (args == null)
		{			
			Plugin.Help(caller);
		}
		else
		{
			if (!Plugin.IsAdminPortalValid(caller))
			{
				Plugin.Help(caller, "You don't have two points set. Use Blaze rod to set them.");
			}
			else
			{
				String serverName = args[0];
				
				Plugin.CreatePortal(caller, serverName);
			}
		}
	}
}
