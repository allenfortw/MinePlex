package mineplex.minecraft.game.classcombat.shop;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPage;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClassCombatShop extends ShopBase<ClassShopManager>
{
  private NautHashMap<String, ItemStack[]> _inventoryStorage = new NautHashMap();
  private NautHashMap<String, ItemStack[]> _armorStorage = new NautHashMap();
  
  protected boolean Purchasing = false;
  protected boolean Customizing = false;
  
  public ClassCombatShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name, new CurrencyType[] { CurrencyType.Gems });
  }
  

  protected ShopPageBase<ClassShopManager, ClassCombatShop> BuildPagesFor(Player player)
  {
    return new ArmorPage((ClassShopManager)this.Plugin, this, this.ClientManager, this.DonationManager, player, this.Purchasing);
  }
  

  protected ShopPageBase<ClassShopManager, ? extends ShopBase<ClassShopManager>> GetOpeningPageForPlayer(Player player)
  {
    return new ArmorPage((ClassShopManager)this.Plugin, this, this.ClientManager, this.DonationManager, player, this.Purchasing);
  }
  

  protected void OpenShopForPlayer(Player player)
  {
    if ((this.Purchasing) || (this.Customizing))
    {
      this._inventoryStorage.put(player.getName(), player.getInventory().getContents());
      this._armorStorage.put(player.getName(), player.getInventory().getArmorContents());
      
      player.getInventory().clear();
      player.getInventory().setArmorContents(new ItemStack[4]);
      
      ((CraftPlayer)player).getHandle().updateInventory(((CraftPlayer)player).getHandle().defaultContainer);
    }
  }
  

  protected void CloseShopForPlayer(Player player)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    
    if ((clientClass != null) && (clientClass.IsSavingCustomBuild()))
    {
      clientClass.SaveActiveCustomBuild();
    }
    
    if (player.isOnline())
    {
      if ((this.Purchasing) || (this.Customizing))
      {
        player.getInventory().setContents((ItemStack[])this._inventoryStorage.get(player.getName()));
        player.getInventory().setArmorContents((ItemStack[])this._armorStorage.get(player.getName()));
      }
      
      ((CraftPlayer)player).getHandle().updateInventory(((CraftPlayer)player).getHandle().defaultContainer);
    }
    
    this._inventoryStorage.remove(player.getName());
    this._armorStorage.remove(player.getName());
  }
}
