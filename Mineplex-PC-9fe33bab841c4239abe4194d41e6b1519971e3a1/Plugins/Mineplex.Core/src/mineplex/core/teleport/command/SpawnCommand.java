package mineplex.core.teleport.command;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.teleport.Teleport;

public class SpawnCommand extends CommandBase<Teleport>
{
	public SpawnCommand(Teleport plugin)
	{
		super(plugin, Rank.ADMIN, "spawn", "s");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args.length == 0)
			Plugin.playerToSpawn(caller, caller.getName());
		else
			Plugin.playerToSpawn(caller, args[0]);
	}
}
