package mineplex.core.portal.commands;

import org.bukkit.entity.Player;

import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import mineplex.core.portal.Portal;

public class PortalCommand extends MultiCommandBase<Portal>
{
	public PortalCommand(Portal plugin)
	{
		super(plugin, Rank.ADMIN, "portal");

		AddCommand(new CreateCommand(plugin));
		AddCommand(new ToggleCommand(plugin));
	}

	@Override
	protected void Help(Player caller, String[] args)
	{
		Plugin.Help(caller);
	}
}
