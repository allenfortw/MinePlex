package me.chiss.Core.Server.command;

import org.bukkit.entity.Player;

import me.chiss.Core.Server.Server;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class WaterSpreadCommand extends CommandBase<Server>
{
	public WaterSpreadCommand(Server plugin)
	{
		super(plugin, Rank.ADMIN, "waterspread");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		Plugin.ToggleLiquidSpread();
		caller.sendMessage("Liquid Spread: " + Plugin.GetLiquidSpread());
	}
}
