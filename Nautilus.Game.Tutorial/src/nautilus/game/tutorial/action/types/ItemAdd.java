package nautilus.game.tutorial.action.types;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemAdd extends Action
{
	private ItemStack _item;
	
	public ItemAdd(Part part, ItemStack item) 
	{
		super(part, 1000);
		
		_item = item;
	}

	@Override
	public void CustomAction(Player player)
	{
		player.getInventory().addItem(_item);
		
		//Effect
		player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
	}
}
