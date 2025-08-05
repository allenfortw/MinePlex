package nautilus.game.arcade.command;

import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class StartCommand extends CommandBase<ArcadeManager>
{
	public StartCommand(ArcadeManager plugin)
	{
		super(plugin, Rank.ADMIN, "start");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (Plugin.GetGame() == null)
			return;

		Plugin.GetGameManager().StateCountdown(Plugin.GetGame(), 10, true);

		caller.sendMessage("Force Starting Game!");
	}
}
