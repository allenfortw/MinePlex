package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderGather;

public class GatherSand extends OrderGather
{
	public GatherSand(MineWare host) 
	{
		super(host, "Pick up 16 Sand", 12, -1, 16);
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
