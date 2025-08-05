package mineplex.hub.commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.hub.modules.MountManager;

public class HorseSpawn extends CommandBase<MountManager>
{
	public HorseSpawn(MountManager plugin)
	{
		super(plugin, Rank.OWNER, new String[] {"horse"});
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		Plugin.HorseCommand(caller, args);
	}
}
