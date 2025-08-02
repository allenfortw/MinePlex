package nautilus.game.arcade.command;

import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;

public class GameCommand extends MultiCommandBase<ArcadeManager>
{
	public GameCommand(ArcadeManager plugin)
	{
		super(plugin, Rank.ADMIN, "game");
		
		AddCommand(new StartCommand(Plugin));
		AddCommand(new StopCommand(Plugin));
	}

	@Override
	protected void Help(Player caller, String[] args)
	{

	}
}
