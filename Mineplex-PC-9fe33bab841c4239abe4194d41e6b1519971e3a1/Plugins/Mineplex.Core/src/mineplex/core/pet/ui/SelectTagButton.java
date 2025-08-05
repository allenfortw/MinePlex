package mineplex.core.pet.ui;

import org.bukkit.entity.Player;

import mineplex.core.shop.item.IButton;

public class SelectTagButton implements IButton
{
	private PetTagPage _page;
	
	public SelectTagButton(PetTagPage page)
	{
		_page = page;
	}
	
	@Override
	public void Clicked(Player player)
	{
		_page.SelectTag();
	}
}
