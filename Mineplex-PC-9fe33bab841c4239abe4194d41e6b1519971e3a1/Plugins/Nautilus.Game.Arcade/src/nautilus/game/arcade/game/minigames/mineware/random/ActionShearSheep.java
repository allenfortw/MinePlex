package nautilus.game.arcade.game.minigames.mineware.random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.Order;

public class ActionShearSheep extends Order 
{
	public ActionShearSheep(MineWare host) 
	{
		super(host, "shear a sheep");
	}

	@Override
	public void Initialize() 
	{
		for (Player player : Host.GetPlayers(true))
		{
			if (!player.getInventory().contains(Material.SHEARS))
				player.getInventory().addItem(new ItemStack(Material.SHEARS));	
		}
	}

	@Override
	public void Uninitialize()
	{
		
	}

	@Override
	public void FailItems(Player player) 
	{
		
	}
	
	@EventHandler
	public void Update(PlayerShearEntityEvent event)
	{		
		SetCompleted(event.getPlayer());
	}
}
