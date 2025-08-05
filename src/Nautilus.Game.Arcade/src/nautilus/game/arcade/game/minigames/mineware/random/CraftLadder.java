package nautilus.game.arcade.game.minigames.mineware.random;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.OrderCraft;

public class CraftLadder extends OrderCraft
{
	public CraftLadder(MineWare host) 
	{
		super(host, "Craft some ladders", 65, -1, 1);
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
