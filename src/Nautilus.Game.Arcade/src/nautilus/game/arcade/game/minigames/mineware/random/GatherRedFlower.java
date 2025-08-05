package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderGather;

public class GatherRedFlower extends OrderGather
{
	public GatherRedFlower(MineWare host) 
	{
		super(host, "Pick 3 Red Roses", 38, -1, 3);
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
