package mineplex.minecraft.game.classcombat.shop.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.PurchaseSkillButton;
import mineplex.minecraft.game.classcombat.shop.button.SelectSkillButton;
import mineplex.minecraft.game.classcombat.shop.salespackage.SkillSalesPackage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
	private IPvpClass _pvpClass;
	private boolean _purchasing;
	
	public SkillPage(ClassShopManager plugin, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, IPvpClass pvpClass, boolean purchasing)
	{
		super(plugin, shop, clientManager, donationManager, "    Select Skills", player);
		
		_pvpClass = pvpClass;
		_purchasing = purchasing;
		
		BuildPage();
	}
	
	public void PlayerClosed()
	{
		super.PlayerClosed();
		
		if (Player != null && Player.isOnline())
		{
			for (int i = 9; i < 36; i++)
			{
				Player.getInventory().setItem(i, null);
			}
		}
	}

	@Override
	protected void BuildPage()
	{
		ButtonMap.clear();
		clear();
		
		BuildClassSkills(_pvpClass);
		BuildGlobalSkills();
		//BuildItemPacks();
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
        
        for (ISkill skill : Plugin.GetSkillFactory().GetSkillsFor(gameClass))
        {                
            switch (skill.GetSkillType())
            {
                case Sword:
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
                case PassiveA:
                    slotNumber = passiveASlotNumber;
                    passiveASlotNumber++;
                    break;
                case PassiveB:
                    slotNumber = passiveBSlotNumber;
                    passiveBSlotNumber++;
                    break;

                default:
                    continue;
            }
            
            BuildSkillItem(skill, slotNumber);
        }
    }
    
    private void BuildGlobalSkills()
    {
        getInventory().setItem(45, new ShopItem(Material.INK_SACK, (byte)11, "Global Passive Skills", null, 1, true, true).getHandle());
        
        int slotNumber = 46;
        
        for (ISkill skill : Plugin.GetSkillFactory().GetGlobalSkills())
        {                
            BuildSkillItem(skill, slotNumber++);
        }
    }
    
    protected void BuildSkillItem(ISkill skill, int slotNumber)
    {	
    	List<String> skillLore = new ArrayList<String>();
    	
    	if (_purchasing)
    	{
        	skillLore.add(C.cYellow + skill.GetCost() + " Gems");
        	skillLore.add(C.cBlack);
    	}
    	
    	skillLore.addAll(Arrays.asList(skill.GetDesc()));
    	
    	for (int i = 0; i < skillLore.size(); i++)
    	{
    		skillLore.set(i, C.cGray + skillLore.get(i));
    	}
    	
    	boolean locked = isSkillLocked(skill.GetSalesPackageId(), skill);
    	ShopItem skillItem = new ShopItem(locked ? Material.BOOK_AND_QUILL : ((skill.GetUsers().contains(Player) || _purchasing) ? Material.WRITTEN_BOOK : Material.BOOK), (locked ? ChatColor.RED + skill.GetName() + " (Locked)" : skill.GetName()), skillLore.toArray(new String[skillLore.size()]), 1, locked, true);
    	
    	if (_purchasing)    		
    	{
    		if (locked)
    			AddButton(slotNumber, skillItem, new PurchaseSkillButton(this, skill));
    		else
    			AddItem(slotNumber, skillItem);
    	}
    	else
    	{
    		if (locked)
    			AddItem(slotNumber, skillItem);
    		else
    			AddButton(slotNumber, skillItem, new SelectSkillButton(this, skill));
    	}
    }
    
    /*
    private void BuildItemPacks()
    {
        Player.getInventory().setItem(18, new ShopItem(Material.WORKBENCH, (byte)11, "Item packs", null, 1, true, true));
        
        int slotNumber = 19;
        
        for (ItemPack item : Plugin.GetItemPackFactory().GetItemPacks(_pvpClass))
        {                
        	BuildItemPack(item, slotNumber++);
        }
    }
    
    private void BuildItemPack(ItemPack itemPack, int slotNumber)
    {	
    	List<String> skillLore = new ArrayList<String>();
    	
    	if (_purchasing)
    	{
        	skillLore.add(C.cYellow + itemPack.GetCost() + " Gems");
        	skillLore.add(C.cBlack);
    	}
    	
    	skillLore.addAll(Arrays.asList(itemPack.GetDesc()));
    	
    	for (int i = 0; i < skillLore.size(); i++)
    	{
    		skillLore.set(i, C.cGray + skillLore.get(i));
    	}
    	
    	boolean locked = !itemPack.IsFree() && !DonationManager.Get(Player.getName()).OwnsUnknownPackage("Competitive itempack " + itemPack.GetName()) && !Client.GetRank().Has(Rank.ULTRA);
    	ShopItem skillItem = new ShopItem(locked ? Material.BOOK_AND_QUILL : ((skill.GetUsers().contains(Player) || _purchasing) ? Material.WRITTEN_BOOK : Material.BOOK), (locked ? ChatColor.RED + skill.GetName() + " (Locked)" : skill.GetName()), skillLore.toArray(new String[skillLore.size()]), 1, locked, true);
    	
    	if (_purchasing)    		
    	{
    		if (locked)
    			AddButton(slotNumber, skillItem, new PurchaseItemPackButton(this, itemPack));
    		else
    			AddItem(slotNumber, skillItem);
    	}
    	else
    	{
    		if (locked)
    			AddItem(slotNumber, skillItem);
    		else
    			AddButton(slotNumber, skillItem, new SelectItemPackButton(this, itemPack));
    	}
    }
*/
	public void SelectSkill(Player player, ISkill skill)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(player);
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
		Shop.OpenPageForPlayer(player, new ConfirmationPage<ClassShopManager, ClassCombatShop>(Plugin, Shop, ClientManager, DonationManager, new Runnable()
		{
			public void run()
			{
				BuildPage();
			}
		}, this, new SkillSalesPackage(skill), CurrencyType.Gems, player));
	}
	
	private boolean isSkillLocked(int skillId, ISkill skill)
	{
    	if (skill.IsFree() || DonationManager.Get(Player.getName()).Owns(skillId) || ClientManager.Get(Player.getName()).GetRank().Has(Rank.ULTRA) || DonationManager.Get(Player.getName()).OwnsUnknownPackage("Competitive ULTRA"))
    		return false;
    	
    	return true;
	}
}
