package nautilus.game.arcade.command;

import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.world.WorldParser;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class ParseCommand extends CommandBase<ArcadeManager>
{
	public ParseCommand(ArcadeManager plugin)
	{
		super(plugin, Rank.ADMIN, "parse");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		caller.sendMessage("Parsing World");
		WorldParser parser = new WorldParser();
		parser.Parse(caller, args);
	}
}
