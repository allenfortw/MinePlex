package mineplex.core.pet.ui;

import org.bukkit.entity.Player;

import mineplex.core.shop.item.IButton;

public class CloseButton implements IButton
{
	@Override
	public void Clicked(Player player)
	{
		player.closeInventory();
	}
}
