package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderPlace;

public class PlaceDoor extends OrderPlace
{
	public PlaceDoor(MineWare host) 
	{
		super(host, "Place a wooden door", 64, -1, 1);
	}

	@Override
	public void Initialize()
	{
		
	}

	@Override
	public void Uninitialize()
	{
		
	}
}
