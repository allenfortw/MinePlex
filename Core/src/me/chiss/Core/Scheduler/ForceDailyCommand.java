package me.chiss.Core.Scheduler;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class ForceDailyCommand extends CommandBase<Scheduler>
{

	public ForceDailyCommand(Scheduler plugin) 
	{
		super(plugin, Rank.ADMIN, "forcedaily");
	}

	@Override
	public void Execute(Player caller, String[] args) 
	{
		Plugin.ResetStartOfDay();
	}

}
