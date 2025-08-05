package mineplex.minecraft.game.classcombat.itempack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPack
{
	private List<ItemStack> _items = new ArrayList<ItemStack>(9);
	
	public void AddItem(ItemStack itemStack)
	{
		_items.add(itemStack);
	}
	
	public void ApplyToPlayer(Player player)
	{
		for (int i = 0; i < 9; i++)
		{
			player.getInventory().setItem(i, _items.size() > i ? _items.get(i) : null);
		}
	}

	public int GetCost()
	{
		return 1000;
	}
}
