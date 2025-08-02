package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderGather;

public class GatherCobble extends OrderGather
{
	public GatherCobble(MineWare host) 
	{
		super(host, "Pick up 10 Cobblestone", 4, -1, 10);
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
