package me.chiss.Core.Server.command;

import org.bukkit.entity.Player;

import me.chiss.Core.Server.Server;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class SpawnSetCommand extends CommandBase<Server>
{
	public SpawnSetCommand(Server plugin)
	{
		super(plugin, Rank.ADMIN, "spawnset");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		caller.getWorld().setSpawnLocation(caller.getLocation().getBlockX(), caller.getLocation().getBlockY(), caller.getLocation().getBlockZ());
	}
}
