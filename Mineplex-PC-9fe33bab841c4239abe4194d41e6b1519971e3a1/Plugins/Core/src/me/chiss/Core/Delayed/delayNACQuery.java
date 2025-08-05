package me.chiss.Core.Delayed;

import org.bukkit.entity.Player;

import me.chiss.Core.Frame.ADelay;
import me.chiss.Core.NAC.NACTask;

public class delayNACQuery extends ADelay
{
	private NACTask _task;
	public delayNACQuery(Player player, String data, NACTask task) 
	{
		super(player, data);
		_task = task;
	}

	@Override
	public void delayed() 
	{
		if (player == null)
			return;
		
		_task.sendQuery(player, false);
	}

}
