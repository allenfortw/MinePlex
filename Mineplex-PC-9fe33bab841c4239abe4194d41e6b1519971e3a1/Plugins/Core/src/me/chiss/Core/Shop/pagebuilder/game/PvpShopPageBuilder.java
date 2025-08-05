package me.chiss.Core.Shop.pagebuilder.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.PvpShop.IPvpShopFactory;
import me.chiss.Core.PvpShop.IShopItem;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.ShopItemPage;
import me.chiss.Core.Shop.pagebuilder.PageBuilderBase;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.IItemWrapper;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.NoWrapper;
import me.chiss.Core.Shop.salespackage.PvpItemPackage;
import me.chiss.Core.Shop.salespackage.PvpShopItem;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;

public class PvpShopPageBuilder extends PageBuilderBase
{
    private IPvpShopFactory _shopFactory;
    
    public PvpShopPageBuilder(Shop shop, String title, IItemWrapper itemWrapper, IPvpShopFactory shopFactory, ICurrencyHandler...currencyHandlers)
    {
        super(shop, title, itemWrapper, currencyHandlers);
        
        _shopFactory = shopFactory;
        
        for (IShopItem item : _shopFactory.GetItems())
        	AddItemPackage(item, item.GetSlot());
    }

    public PvpShopPageBuilder(Shop shop, String title, IPvpShopFactory shopFactory, ICurrencyHandler...currencyHandlers)
    {
    	this(shop, title, new NoWrapper(), shopFactory, currencyHandlers);
    }
    
    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new ShopItemPage(Shop, Title, CurrencyHandlers, UnlockedSalesPackageMap, LockedSalesPackageMap);
    }
    
    protected void AddItemPackage(IShopItem item, int slot)
    {
    	List<String> itemLore = new ArrayList<String>();
    	
    	itemLore.add(C.cGreen + item.GetEconomyCost() + " Coins");
    	itemLore.add(C.cBlack);
    	
    	itemLore.addAll(Arrays.asList(item.GetDesc()));
    	
    	for (int i = 2; i < itemLore.size(); i++)
    	{
    		itemLore.set(i, C.cGray + itemLore.get(i));
    	}
        
        AddItem(UnlockedSalesPackageMap, new PvpItemPackage(new PvpShopItem(item.GetType(), item.GetData(), item.GetName(), item.GetDeliveryName(), itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), false, true), 
        		item.GetCreditCost(), item.GetPointCost(), item.GetTokenCost(), item.GetEconomyCost(), item.IsFree(), item.GetSalesPackageId()), slot);
    }
}
