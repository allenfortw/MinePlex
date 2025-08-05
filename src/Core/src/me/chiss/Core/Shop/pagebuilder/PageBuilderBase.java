package me.chiss.Core.Shop.pagebuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.IItemWrapper;
import mineplex.minecraft.shop.item.ISalesPackage;

public abstract class PageBuilderBase implements IPageBuilder
{
    protected Shop Shop;
    protected String Title;
    protected IItemWrapper ItemWrapper;
    protected HashMap<Integer, ISalesPackage> LockedSalesPackageMap;
    protected HashMap<Integer, ISalesPackage> UnlockedSalesPackageMap;
    protected List<ICurrencyHandler> CurrencyHandlers;

    public PageBuilderBase(Shop shop, String title, IItemWrapper itemWrapper, ICurrencyHandler...currencyHandlers)
    {
        Shop = shop;
        Title = title;
        ItemWrapper = itemWrapper;
        CurrencyHandlers = Arrays.asList(currencyHandlers);
        
        LockedSalesPackageMap = new HashMap<Integer, ISalesPackage>();
        UnlockedSalesPackageMap = new HashMap<Integer, ISalesPackage>();
    }
    
    protected void AddItem(HashMap<Integer, ISalesPackage> map, ISalesPackage salesItem, int slot)
    {
    	map.put(slot,  ItemWrapper.WrapPackage(salesItem));
    }
    
    protected void AddItem(HashMap<Integer, ISalesPackage> map, ISalesPackage salesItem)
    {
    	map.put(GetNextAvailableSlotNumber(map),  ItemWrapper.WrapPackage(salesItem));
    }
    
    protected int GetNextAvailableSlotNumber(HashMap<Integer, ?> map)
    {
        int slot = 45;
        
        while(map.get(slot) != null)
        {
            slot++;
            slot += slot % 9 == 0 ? -18 : 0;
        }
        
        return slot;
    }
}
