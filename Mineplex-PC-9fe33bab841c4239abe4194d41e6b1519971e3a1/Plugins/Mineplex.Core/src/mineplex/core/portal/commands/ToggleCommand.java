package mineplex.core.portal.commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.portal.Portal;

public class ToggleCommand extends CommandBase<Portal>
{
	public ToggleCommand(Portal plugin)
	{
		super(plugin, Rank.ADMIN, "toggle");
	}

	@Override
	public void Execute(final Player caller, String[] args)
	{		
		Plugin.ToggleSetupAdmin(caller);
	}
}
