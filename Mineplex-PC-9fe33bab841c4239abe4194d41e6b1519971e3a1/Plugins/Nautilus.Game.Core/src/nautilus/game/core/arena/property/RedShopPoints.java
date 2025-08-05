package nautilus.game.core.arena.property;

import nautilus.game.core.arena.ITeamArena;

public class RedShopPoints<ArenaType extends ITeamArena> extends PropertyBase<ArenaType>
{
	public RedShopPoints()
	{
		super("redshoppoints");
	}

	public boolean Parse(ArenaType arena, String value) 
	{
		for (String vector : value.split(","))
		{
			arena.AddRedGameShopPoint(ParseVector(vector.trim()), Float.parseFloat(vector.trim().split(" ")[3]) * 90);
		}
		
		return true;
	}
}
