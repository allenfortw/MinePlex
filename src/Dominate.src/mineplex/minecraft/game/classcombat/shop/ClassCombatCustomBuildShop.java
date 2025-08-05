package mineplex.minecraft.game.classcombat.shop;

import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;

public class ClassCombatCustomBuildShop extends ClassCombatShop
{
  public ClassCombatCustomBuildShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name);
    
    this.Customizing = true;
  }
}
