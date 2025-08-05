package mineplex.core.pet.ui;

import org.bukkit.entity.Player;

import mineplex.core.pet.Pet;
import mineplex.core.shop.item.IButton;

public class PetButton implements IButton
{
	private Pet _pet;
	private PetPage _page;
	
	public PetButton(Pet pet, PetPage page)
	{
		_pet = pet;
		_page = page;
	}
	
	@Override
	public void Clicked(Player player)
	{
		_page.PurchasePet(player, _pet);
	}
}
