package mineplex.minecraft.game.classcombat.shop.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.C;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.SelectClassButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class ArmorPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
  private boolean _purchasing;
  
  public ArmorPage(ClassShopManager shopManager, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, boolean purchasing)
  {
    super(shopManager, shop, clientManager, donationManager, "       Armor", player);
    
    this._purchasing = purchasing;
    
    BuildPage();
  }
  
  public void SelectClass(Player player, IPvpClass pvpClass)
  {
    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.6F);
    
    if (this._purchasing) {
      ((ClassCombatShop)this.Shop).OpenPageForPlayer(this.Player, new SkillPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, this.Player, pvpClass, true));
    }
    else {
      ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
      
      player.getInventory().clear();
      
      clientClass.SetGameClass(pvpClass);
      pvpClass.ApplyArmor(player);
      clientClass.ClearDefaults();
      
      ((ClassCombatShop)this.Shop).OpenPageForPlayer(this.Player, new CustomBuildPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, player));
    }
  }
  

  protected void BuildPage()
  {
    int slot = 9;
    
    for (IPvpClass gameClass : ((ClassShopManager)this.Plugin).GetClassManager().GetGameClasses())
    {
      BuildArmorSelectPackage(gameClass, slot);
      
      slot += 2;
    }
  }
  
  private void BuildArmorSelectPackage(IPvpClass gameClass, int slot)
  {
    List<String> lockedClassDesc = new ArrayList();
    List<String> unlockedClassDesc = new ArrayList();
    
    lockedClassDesc.add(C.cBlack);
    unlockedClassDesc.add(C.cBlack);
    
    lockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    unlockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    
    for (int i = 1; i < lockedClassDesc.size(); i++)
    {
      lockedClassDesc.set(i, C.cGray + (String)lockedClassDesc.get(i));
    }
    
    for (int i = 1; i < unlockedClassDesc.size(); i++)
    {
      unlockedClassDesc.set(i, C.cGray + (String)unlockedClassDesc.get(i));
    }
    
    AddButton(slot, new ShopItem(gameClass.GetHead(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    AddButton(slot + 9, new ShopItem(gameClass.GetChestplate(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    AddButton(slot + 18, new ShopItem(gameClass.GetLeggings(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    AddButton(slot + 27, new ShopItem(gameClass.GetBoots(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
  }
}
