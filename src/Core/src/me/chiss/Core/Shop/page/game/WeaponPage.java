package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.chiss.Core.Class.IPvpClass;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.shop.item.ISalesPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.ShopPageBase;

public class WeaponPage extends ShopPageBase
{
	private IPvpClass _currentClass;
	
    private HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> UnlockedClassMap;
    private HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> LockedClassMap;
	
    public WeaponPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> unlockedClassMap, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> lockedClassMap)
    {
        super(shop, title, currencyHandlers, null, null);
        
        UnlockedClassMap = unlockedClassMap;
        LockedClassMap = lockedClassMap;
    }
    
    @Override
    public void OpenForPlayer(CoreClient player)
    {        
        if (_currentClass == null || _currentClass != player.Class().GetGameClass())
        {
            _currentClass = player.Class().GetGameClass();
            
            if (_currentClass == null)
            	_currentClass = Shop.GetClassForPlayer(player);
        }
        
        ReconstructPageForPlayer(player);

    	UpdateBalance(player);
    	
    	player.Class().OpenInventory(this);
    }

    private void ReconstructPageForPlayer(CoreClient player)
    {
        SalesPackageMap.clear();
        HashMap<Integer, ISalesPackage> packageMap = UnlockedClassMap.get(_currentClass);
        
        if (_currentClass != null)
        {
        	for (Entry<Integer, ISalesPackage> entry : packageMap.entrySet())
	        {            
	            UpdateClassSlot(player, entry.getKey());
	        }
        }
    }
    
    protected void UpdateClassSlot(CoreClient player, int slot)
    {
    	if (player.Donor().Owns(UnlockedClassMap.get(_currentClass).get(slot).GetSalesPackageId()) || UnlockedClassMap.get(_currentClass).get(slot).IsFree() || player.Rank().Has(Rank.EMERALD, false))
    	{
    		AddItem(UnlockedClassMap.get(_currentClass).get(slot), slot);
    	}
    	else
    	{
    		AddItem(LockedClassMap.get(_currentClass).get(slot), slot);
    	}
    }
    
    protected boolean ShowUnlockedAtSlot(CoreClient player, int slot)
    {
    	return super.ShowUnlockedAtSlot(player, slot) || player.Rank().Has(Rank.EMERALD, false);
    }
}
