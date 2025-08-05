package mineplex.core.map.commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.map.Map;

public class MapImage extends CommandBase<Map>
{
	public MapImage(Map plugin)
	{
		super(plugin, Rank.OWNER, new String[] {"mi"});
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		Plugin.SpawnMap(caller, args);
	}
}
