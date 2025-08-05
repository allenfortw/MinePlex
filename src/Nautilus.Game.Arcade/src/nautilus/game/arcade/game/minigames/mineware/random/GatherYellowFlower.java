package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderGather;

public class GatherYellowFlower extends OrderGather
{
	public GatherYellowFlower(MineWare host) 
	{
		super(host, "Pick 4 Yellow Flowers", 37, -1, 4);
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
