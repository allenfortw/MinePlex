package mineplex.core.pet.ui;

import org.bukkit.entity.Player;

import mineplex.core.pet.PetExtra;
import mineplex.core.shop.item.IButton;

public class PetExtraButton implements IButton
{
	private PetExtra _petExtra;
	private PetPage _petPage;
	
	public PetExtraButton(PetExtra petExtra, PetPage petPage)
	{
		_petExtra = petExtra;
		_petPage = petPage;
	}

	@Override
	public void Clicked(Player player)
	{
		_petPage.PurchasePetExtra(player, _petExtra);
	}
}
