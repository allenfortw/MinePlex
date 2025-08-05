package nautilus.game.tutorial.action.types;

import mineplex.core.common.util.UtilInv;
import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRemove extends Action
{
	private ItemStack _item;
	
	public ItemRemove(Part part, ItemStack item) 
	{
		super(part, 1000);
		
		_item = item;
	}

	@Override
	public void CustomAction(Player player)
	{
		if (_item.getData() != null)
			UtilInv.removeAll(player, _item.getType(), _item.getData().getData());
		else
			UtilInv.removeAll(player, _item.getType(), (byte)0);
	}
}
