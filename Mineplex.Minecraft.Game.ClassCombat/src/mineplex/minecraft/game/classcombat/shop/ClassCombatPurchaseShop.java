package mineplex.minecraft.game.classcombat.shop;

import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;

public class ClassCombatPurchaseShop extends ClassCombatShop
{
	public ClassCombatPurchaseShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
	{
		super(plugin, clientManager, donationManager, name);
		
		Purchasing = true;
	}
}
