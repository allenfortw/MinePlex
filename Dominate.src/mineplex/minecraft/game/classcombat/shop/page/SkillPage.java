package mineplex.minecraft.game.classcombat.shop.page;

import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.PurchaseSkillButton;
import mineplex.minecraft.game.classcombat.shop.button.SelectSkillButton;
import mineplex.minecraft.game.classcombat.shop.salespackage.SkillSalesPackage;
import net.minecraft.server.v1_6_R3.IInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SkillPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
  private IPvpClass _pvpClass;
  private boolean _purchasing;
  
  public SkillPage(ClassShopManager plugin, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, IPvpClass pvpClass, boolean purchasing)
  {
    super(plugin, shop, clientManager, donationManager, "    Select Skills", player);
    
    this._pvpClass = pvpClass;
    this._purchasing = purchasing;
    
    BuildPage();
  }
  
  public void PlayerClosed()
  {
    super.PlayerClosed();
    
    if ((this.Player != null) && (this.Player.isOnline()))
    {
      for (int i = 9; i < 36; i++)
      {
        this.Player.getInventory().setItem(i, null);
      }
    }
  }
  

  protected void BuildPage()
  {
    this.ButtonMap.clear();
    clear();
    
    BuildClassSkills(this._pvpClass);
    BuildGlobalSkills();
  }
  

  private void BuildClassSkills(IPvpClass gameClass)
  {
    getInventory().setItem(0, new ShopItem(Material.IRON_SWORD, "Sword Skills", null, 1, true, true).getHandle());
    getInventory().setItem(9, new ShopItem(Material.IRON_AXE, "Axe Skills", null, 1, true, true).getHandle());
    getInventory().setItem(18, new ShopItem(Material.BOW, "Bow Skills", null, 1, true, true).getHandle());
    getInventory().setItem(27, new ShopItem(Material.INK_SACK, (byte)1, "Class Passive A Skills", null, 1, true, true).getHandle());
    getInventory().setItem(36, new ShopItem(Material.INK_SACK, (byte)14, "Class Passive B Skills", null, 1, true, true).getHandle());
    
    int slotNumber = 53;
    
    int swordSlotNumber = 1;
    int axeSlotNumber = 10;
    int bowSlotNumber = 19;
    int passiveASlotNumber = 28;
    int passiveBSlotNumber = 37;
    
    for (ISkill skill : ((ClassShopManager)this.Plugin).GetSkillFactory().GetSkillsFor(gameClass))
    {
      switch (skill.GetSkillType())
      {
      case Class: 
        slotNumber = swordSlotNumber;
        swordSlotNumber++;
        break;
      case Axe: 
        slotNumber = axeSlotNumber;
        axeSlotNumber++;
        break;
      case Bow: 
        slotNumber = bowSlotNumber;
        bowSlotNumber++;
        break;
      case GlobalPassive: 
        slotNumber = passiveASlotNumber;
        passiveASlotNumber++;
        break;
      case PassiveA: 
        slotNumber = passiveBSlotNumber;
        passiveBSlotNumber++;
        break;
      }
      
      


      BuildSkillItem(skill, slotNumber);
    }
  }
  
  private void BuildGlobalSkills()
  {
    getInventory().setItem(45, new ShopItem(Material.INK_SACK, (byte)11, "Global Passive Skills", null, 1, true, true).getHandle());
    
    int slotNumber = 46;
    
    for (ISkill skill : ((ClassShopManager)this.Plugin).GetSkillFactory().GetGlobalSkills())
    {
      BuildSkillItem(skill, slotNumber++);
    }
  }
  
  protected void BuildSkillItem(ISkill skill, int slotNumber)
  {
    List<String> skillLore = new java.util.ArrayList();
    
    if (this._purchasing)
    {
      skillLore.add(C.cYellow + skill.GetCost() + " Gems");
      skillLore.add(C.cBlack);
    }
    
    skillLore.addAll(java.util.Arrays.asList(skill.GetDesc()));
    
    for (int i = 0; i < skillLore.size(); i++)
    {
      skillLore.set(i, C.cGray + (String)skillLore.get(i));
    }
    
    boolean locked = isSkillLocked(skill.GetSalesPackageId().intValue(), skill);
    ShopItem skillItem = new ShopItem((skill.GetUsers().contains(this.Player)) || (this._purchasing) ? Material.WRITTEN_BOOK : locked ? Material.BOOK_AND_QUILL : Material.BOOK, locked ? ChatColor.RED + skill.GetName() + " (Locked)" : skill.GetName(), (String[])skillLore.toArray(new String[skillLore.size()]), 1, locked, true);
    
    if (this._purchasing)
    {
      if (locked) {
        AddButton(slotNumber, skillItem, new PurchaseSkillButton(this, skill));
      } else {
        AddItem(slotNumber, skillItem);
      }
      
    }
    else if (locked) {
      AddItem(slotNumber, skillItem);
    } else {
      AddButton(slotNumber, skillItem, new SelectSkillButton(this, skill));
    }
  }
  

















































  public void SelectSkill(Player player, ISkill skill)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    ISkill existingSkill = clientClass.GetSkillByType(skill.GetSkillType());
    
    if (existingSkill != null)
    {
      clientClass.RemoveSkill(existingSkill);
    }
    
    clientClass.AddSkill(skill);
    
    PlayAcceptSound(player);
    
    BuildPage();
  }
  
  public void PurchaseSkill(Player player, ISkill skill)
  {
    ((ClassCombatShop)this.Shop).OpenPageForPlayer(player, new ConfirmationPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        SkillPage.this.BuildPage();
      }
    }, this, new SkillSalesPackage(skill), CurrencyType.Gems, player));
  }
  
  private boolean isSkillLocked(int skillId, ISkill skill)
  {
    if ((skill.IsFree()) || (this.DonationManager.Get(this.Player.getName()).Owns(Integer.valueOf(skillId))) || (this.ClientManager.Get(this.Player.getName()).GetRank().Has(Rank.ULTRA)) || (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Competitive ULTRA"))) {
      return false;
    }
    return true;
  }
}
