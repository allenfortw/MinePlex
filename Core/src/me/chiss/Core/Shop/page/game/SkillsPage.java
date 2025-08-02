package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.inventory.PlayerInventory;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.ShopPageBase;
import me.chiss.Core.Shop.salespackage.DonationPackage;
import me.chiss.Core.Shop.salespackage.ISalesPackage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Shop.salespackage.SkillPackage;

public class SkillsPage extends ShopPageBase
{
	protected IPvpClass CurrentClass;
    protected HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> UnlockedClassMap;
    protected HashMap<Integer, List<ISalesPackage>> UnlockedGlobalMap;
    protected HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> LockedClassMap;
    protected HashMap<Integer, List<ISalesPackage>> LockedGlobalMap;
    
    public SkillsPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, 
    		HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> unlockedClassMap, HashMap<Integer, List<ISalesPackage>> unlockedGlobalMap,
    		HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> lockedClassMap, HashMap<Integer, List<ISalesPackage>> lockedGlobalMap)
    {
        super(shop, title, currencyHandlers, null, null);
        
        UnlockedClassMap = unlockedClassMap;
        UnlockedGlobalMap = unlockedGlobalMap;
        LockedClassMap = lockedClassMap;
        LockedGlobalMap = lockedGlobalMap;
    }
    
    @Override
    public void OpenForPlayer(CoreClient player)
    {        
        if (CurrentClass == null || CurrentClass != player.Class().GetGameClass())
        {
            CurrentClass = player.Class().GetGameClass();
            
            if (CurrentClass == null)
            	CurrentClass = Shop.GetClassForPlayer(player);
        }
        
        ReconstructPageForPlayer(player);
        
        PlayerInventory playerInv = player.Class().GetInventory();
        
        for (int i = 9; i < 36; i++)
        {
            playerInv.setItem(i, null);
        }
        
        TranslateIntoVirtualInventory(player);
    	
    	UpdateBalance(player);
    	
    	player.Class().OpenInventory(this);
    }
    
    @Override
    public void CloseForPlayer(CoreClient player)
    {
        PlayerInventory playerInv = player.Class().GetInventory();
        
        for (int i = 9; i < 36; i++)
        {
            playerInv.setItem(i, null);
        }
        
        Shop.ShowSkillHotBarForPlayer(player);
        
        player.GetPlayer().closeInventory();
    	this.inventory.onClose((CraftPlayer)player.GetPlayer());
    }
    
    @Override
    public void PlayerWants(CoreClient player, int slot)
    {
        ISalesPackage sellable = GetItem(slot);
        
        if (sellable == null)
        {
        	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
            return;
        }
        
        if (CurrentCurrencyHandler.CanAfford(player, sellable) && sellable.CanFitIn(player))
        {
        	PurchaseSalesPackage(player, sellable, slot);
        }
        else
        {
        	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
        }
    }
    
    protected void PurchaseSalesPackage(CoreClient player, ISalesPackage sellable, int slot)
    {
        if (sellable instanceof SkillPackage)
        {
            SkillPackage skillSellable = GetSkillPackage(sellable);
            
            ISkill existingSkill = player.Class().GetSkillByType(skillSellable.GetSkill().GetSkillType());
            
            if (existingSkill != null)
            {
                for (Entry<Integer, ISalesPackage> salesPackage : SalesPackageMap.entrySet())
                {
                    if (salesPackage.getValue() instanceof SkillPackage)
                    {
                        SkillPackage otherSkillSellable = GetSkillPackage(salesPackage.getValue());
                        
                        if (otherSkillSellable.GetSkill() == existingSkill)
                        {
                            if (existingSkill != skillSellable.GetSkill())
                            {
                            	int skillLevel = player.Class().GetSkillLevel(existingSkill);
                            	for (int i=0; i < skillLevel; i++)
                            	{	
                            		CurrentCurrencyHandler.Return(player, otherSkillSellable);
                            	}
                                
                            	otherSkillSellable.ReturnAllLevels(player);
                                
                                if (slot >= 54)
                                {
                                    UnlockedGlobalMap.get(salesPackage.getKey() - 45).get(0).DeliverTo(player.Class(), salesPackage.getKey() - 45);
                                    SalesPackageMap.put(salesPackage.getKey(), UnlockedGlobalMap.get(salesPackage.getKey() - 45).get(0));
                                }
                                else
                                {
                                    AddItem(UnlockedClassMap.get(CurrentClass).get(salesPackage.getKey()).get(0), salesPackage.getKey());
                                }
                            }
                            
                            break;
                        }
                    }
                }
            }
            
            if (!skillSellable.IsActive() || skillSellable.GetLevel() < skillSellable.GetSkill().GetMaxLevel())
            {
                int index = skillSellable.IsActive() ? skillSellable.GetLevel() + 1 : skillSellable.GetLevel();
                
        		if (player.Donor().Owns(skillSellable.GetSkill().GetSalesPackageId(index)) || skillSellable.GetSkill().IsFree(index) || player.Rank().Has(Rank.DIAMOND, false))
        		{
        			player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ORB_PICKUP, 1f, .6f);
        			
                    if (slot >= 54)
                    {
                    	UpdateGlobalSlot(player, slot, index);
                    }
                    else
                    {
                    	UpdateClassSlot(player, slot, index);
                    }
                    
            		GetItem(slot).PurchaseBy(player);
            		CurrentCurrencyHandler.Deduct(player, GetItem(slot));
        		}
            }
        }
    }
    
    @Override
    public void Reset(CoreClient player)
    {
    	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.SPIDER_WALK, 1, .6f);
    	
        for (Entry<Integer, ISalesPackage> entry : SalesPackageMap.entrySet())
        {
            if (entry.getValue() instanceof SkillPackage)
            {
                SkillPackage skillSellable = (SkillPackage)entry.getValue();
                int skillLevel = player.Class().GetSkillLevel(skillSellable.GetSkill());
                
            	for (int i=0; i < skillLevel; i++)
            		CurrentCurrencyHandler.Return(player, skillSellable);
                
            	skillSellable.ReturnAllLevels(player);
            	
                skillLevel = player.Class().GetSkillLevel(skillSellable.GetSkill());

            	player.Class().AddSkill(skillSellable.GetSkill(), 0);
            	
                if (entry.getKey() >= 54)
                {
                	UpdateGlobalSlot(player, entry.getKey(), player.Class().GetSkillLevel(skillSellable.GetSkill()));
                }
                else
                {
                    UpdateClassSlot(player, entry.getKey(), player.Class().GetSkillLevel(skillSellable.GetSkill()));
                }
            }
        }
        
        player.Donor().ResetSkillTokens();
    }
    
    @Override
    public void PlayerReturning(CoreClient player, int slot)
    {
        ISalesPackage sellable = GetItem(slot);
        
        if (sellable instanceof SkillPackage)
        {
            SkillPackage skillSellable = (SkillPackage)sellable;
            
            if (skillSellable.IsActive())
            {
            	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.SPIDER_WALK, 1, .6f);
            	
                int index = skillSellable.GetLevel() - 1;
                
                if (slot >= 54)
                {
                	UpdateGlobalSlot(player, slot, index);
                }
                else
                {
                    UpdateClassSlot(player, slot, index);
                }
                
                CurrentCurrencyHandler.Return(player, sellable);
            }
        }
    }
    
    public void ResetVisuals()
    {
        CurrentClass = null;
    }
    
    protected void TranslateIntoVirtualInventory(CoreClient player)
    {
        PlayerInventory playerInv = player.Class().GetInventory();
        playerInv.setItem(9, new ShopItem(Material.INK_SACK, (byte)11, "Global Passive A Skills", null, 1, true, true));
        playerInv.setItem(18, new ShopItem(Material.INK_SACK, (byte)2, "Global Passive B Skills", null, 1, true, true));
        playerInv.setItem(27, new ShopItem(Material.INK_SACK, (byte)4, "Global Passive C Skills", null, 1, true, true));
        
        for (Entry<Integer, List<ISalesPackage>> entry : UnlockedGlobalMap.entrySet())
        {
            UpdateGlobalSlot(player, 45 + entry.getKey(), GetIndexForSkill(player, entry, GetSkillPackage(0, entry.getValue())));
        }
    }
    
    private void ReconstructPageForPlayer(CoreClient player)
    {
        clear();
        
        if (PreviousPage != null)
        {
            SetPreviousPage(PreviousPage);
        }
        
        if (NextPage != null)
        {
            SetNextPage(NextPage);
        }
        
        SalesPackageMap.clear();
        HashMap<Integer, List<ISalesPackage>> packageMap = UnlockedClassMap.get(CurrentClass);
        
        AddItem(new ItemPackage(new ShopItem(Material.IRON_SWORD, "Sword Skills", null, 1, true, true), 0, 0, 0, 0, false, -1),  9);
        AddItem(new ItemPackage(new ShopItem(Material.IRON_AXE, "Axe Skills", null, 1, true, true), 0, 0, 0, 0, false, -1), 18);
        AddItem(new ItemPackage(new ShopItem(Material.BOW, "Bow Skills", null, 1, true, true), 0, 0, 0, 0, false, -1), 27);
        AddItem(new ItemPackage(new ShopItem(Material.INK_SACK, (byte)1, "Class Passive A Skills", null, 1, true, true), 0, 0, 0, 0, false, -1), 36);
        AddItem(new ItemPackage(new ShopItem(Material.INK_SACK, (byte)14, "Class Passive B Skills", null, 1, true, true), 0, 0, 0, 0, false, -1), 45);        
        
        if (CurrentClass != null)
        {
	        for (Entry<Integer, List<ISalesPackage>> entry : packageMap.entrySet())
	        {
	            UpdateClassSlot(player, entry.getKey(), GetIndexForSkill(player, entry, GetSkillPackage(0, entry.getValue())));
	        }
        }
    }
    
    protected int GetIndexForSkill(CoreClient player, Entry<Integer, List<ISalesPackage>> entry, SkillPackage skillPackage) 
    {
        return player.Class().GetSkillLevel(skillPackage.GetSkill());
    }
    
    protected SkillPackage GetSkillPackage(int number, List<ISalesPackage> salesPacakgeList)
    {    	
    	return GetSkillPackage(salesPacakgeList.get(number));
    }
    
    protected SkillPackage GetSkillPackage(ISalesPackage salesPackage)
    {
    	SkillPackage skillPackage;
    	if (salesPackage instanceof DonationPackage)
    	{
    		skillPackage = (SkillPackage)((DonationPackage)salesPackage).GetWrappedPackage();
    	}
    	else
    	{
    		skillPackage = (SkillPackage)salesPackage;
    	}
    	
    	return skillPackage;
    }
    
    protected void UpdateGlobalSlot(CoreClient player, int slot, int index)
    {
    	if (player.Donor().Owns(UnlockedGlobalMap.get(slot - 45).get(index).GetSalesPackageId()) || UnlockedGlobalMap.get(slot - 45).get(index).IsFree() || player.Rank().Has(Rank.DIAMOND, false))
    	{
            UnlockedGlobalMap.get(slot - 45).get(index).DeliverTo(player.Class(), slot - 45);
            SalesPackageMap.put(slot, UnlockedGlobalMap.get(slot - 45).get(index));
    	}
    	else
    	{
    		LockedGlobalMap.get(slot - 45).get(index).DeliverTo(player.Class(), slot - 45);
            SalesPackageMap.put(slot, LockedGlobalMap.get(slot - 45).get(index));
    	}
    }
    
    protected void UpdateClassSlot(CoreClient player, int slot, int index)
    {
    	if (player.Donor().Owns(UnlockedClassMap.get(CurrentClass).get(slot).get(index).GetSalesPackageId()) || UnlockedClassMap.get(CurrentClass).get(slot).get(index).IsFree() || player.Rank().Has(Rank.DIAMOND, false))
    	{
    		AddItem(UnlockedClassMap.get(CurrentClass).get(slot).get(index), slot);
    	}
    	else
    	{
    		AddItem(LockedClassMap.get(CurrentClass).get(slot).get(index), slot);
    	}
    }
}
