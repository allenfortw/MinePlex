package nautilus.game.core.arena.property;

import nautilus.game.core.arena.ITeamArena;

public class BlueSpawnPoints<ArenaType extends ITeamArena> extends PropertyBase<ArenaType>
{
	public BlueSpawnPoints()
	{
		super("bluespawnpoints");
	}

	public boolean Parse(ArenaType arena, String value) 
	{
		for (String vector : value.split(","))
		{
			arena.AddBlueSpawnPoint(ParseVector(vector.trim()), Float.parseFloat(vector.trim().split(" ")[3]) * 90);
		}
		
		return true;
	}
}
