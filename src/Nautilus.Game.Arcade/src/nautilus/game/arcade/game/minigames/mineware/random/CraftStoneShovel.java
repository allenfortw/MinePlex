package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderCraft;

public class CraftStoneShovel extends OrderCraft
{
	public CraftStoneShovel(MineWare host) 
	{
		super(host, "Craft a stone shovel", 273, -1, 1);
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
