package me.chiss.Core.Shop.page.game.purchase;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Sound;

import me.chiss.Core.Class.IPvpClass;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.shop.item.ISalesPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.game.SkillsPage;
import me.chiss.Core.Shop.salespackage.DonationPackage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Shop.salespackage.SkillPackage;

public class SkillsPurchasePage extends SkillsPage
{    
    public SkillsPurchasePage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, 
    		HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> unlockedClassMap, HashMap<Integer, List<ISalesPackage>> unlockedGlobalMap,
    		HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> lockedClassMap, HashMap<Integer, List<ISalesPackage>> lockedGlobalMap)
    {
        super(shop, title, currencyHandlers, unlockedClassMap, unlockedGlobalMap, lockedClassMap, lockedGlobalMap);
    }
    
	@Override
    protected void SetResetButtonLore(int balance, String[] lore)
    {
    	if (CurrencyHandlers.size() > 1)
    	{
    		lore[1] = "§rClick to change to " + GetNextCurrencyHandler().GetName();
    	}
    	
        if (balance <= 0)
        {
        	setItem(4, new ShopItem(CurrentCurrencyHandler.GetItemDisplayType(), CurrentCurrencyHandler.GetName() + "(None)", lore, 0, false, true));
        }
        else
        {
        	ShopItem currencyItem = new ShopItem(CurrentCurrencyHandler.GetItemDisplayType(), CurrentCurrencyHandler.GetName(), lore, 0, false, true);
        	
        	/* Max is still 64
        	if (balance <= 128)
        		currencyItem.setAmount(balance);
        	*/
        	
        	setItem(4, currencyItem);    
        }
    }
    
    @Override
    protected void PurchaseSalesPackage(CoreClient player, ISalesPackage sellable, int slot)
    {
        if (sellable instanceof DonationPackage)
        {
            SkillPackage skillSellable = GetSkillPackage(sellable);
            
    		if (player.Rank().Has(Rank.DIAMOND, false))
    		{
    			player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
    			return;
    		}
            
            if (!skillSellable.IsActive() || skillSellable.GetLevel() < skillSellable.GetSkill().GetMaxLevel())
            {
                int index = skillSellable.IsActive() ? skillSellable.GetLevel() + 1 : skillSellable.GetLevel();
                              
                if (slot >= 54)
                {
                	sellable = LockedGlobalMap.get(slot - 45).get(index);
                }
                else
                {
                	sellable = LockedClassMap.get(CurrentClass).get(slot).get(index);
                }
                
        		new ConfirmationPage(Shop, this, sellable, CurrentCurrencyHandler, player).OpenForPlayer(player);
             }
        }
    }
    
    @Override
    protected int GetIndexForSkill(CoreClient player, Entry<Integer, List<ISalesPackage>> entry, SkillPackage skillPackage) 
    {
    	int index = 0;
    	
        if (player.Donor().Owns(entry.getValue().get(index).GetSalesPackageId()) || entry.getValue().get(index).IsFree() || player.Rank().Has(Rank.DIAMOND, false))
        {
        	for (int i = 1; i <= skillPackage.GetSkill().GetMaxLevel(); i++)
        	{
        		skillPackage = GetSkillPackage(entry.getValue().get(i));
        		
        		if (!player.Donor().Owns(skillPackage.GetSalesPackageId()) && !skillPackage.IsFree() && !player.Rank().Has(Rank.DIAMOND, false))
        			break;
        		
        		index = i;
        	}
        }
        else
        {
            index = player.Class().GetSkillLevel(skillPackage.GetSkill());
        }
        
        return index;
    }
}
