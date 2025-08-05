package nautilus.game.core.arena.property;

import nautilus.game.core.arena.ITeamArena;

public class RedSpawnPoints<ArenaType extends ITeamArena> extends PropertyBase<ArenaType>
{
	public RedSpawnPoints()
	{
		super("redspawnpoints");
	}

	public boolean Parse(ArenaType arena, String value) 
	{		
		for (String vector : value.split(","))
		{
		    arena.AddRedSpawnPoint(ParseVector(vector.trim()), Float.parseFloat(vector.trim().split(" ")[3]) * 90);
		}
		
		return true;
	}
}
