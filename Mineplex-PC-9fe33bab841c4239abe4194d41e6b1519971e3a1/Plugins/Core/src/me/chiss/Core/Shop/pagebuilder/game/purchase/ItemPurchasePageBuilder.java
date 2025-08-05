package me.chiss.Core.Shop.pagebuilder.game.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.purchase.ItemPurchasePage;
import me.chiss.Core.Shop.pagebuilder.game.ItemPageBuilder;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.DonationWrapper;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.classcombat.item.IItem;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;
import mineplex.minecraft.shop.item.ItemPackage;

public class ItemPurchasePageBuilder extends ItemPageBuilder
{
    public ItemPurchasePageBuilder(Shop shop, String title, IItemFactory itemFactory, ICurrencyHandler...currencyHandlers)
    {
        super(shop, title, new DonationWrapper(), itemFactory, currencyHandlers);
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new ItemPurchasePage(Shop, Title, CurrencyHandlers, UnlockedSalesPackageMap, LockedSalesPackageMap);
    }
    
    @Override
    protected void AddItemPackage(IItem item, int slot)
    {
    	List<String> itemLore = new ArrayList<String>();

    	itemLore.add(C.cYellow + item.GetCreditCost() + "c" + C.cWhite + " or " + C.cYellow + item.GetPointCost() + "p");
    	itemLore.add(C.cBlack);
    	
    	itemLore.addAll(Arrays.asList(item.GetDesc()));
    	
    	for (int i = 2; i < itemLore.size(); i++)
    	{
    		itemLore.set(i, C.cGray + itemLore.get(i));
    	}
        
        AddItem(LockedSalesPackageMap, new ItemPackage(new ShopItem(item.GetType(), item.GetName(), itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), true), item.GetCreditCost(), item.GetPointCost(), item.GetTokenCost(), item.GetEconomyCost(), item.IsFree(), item.GetSalesPackageId()), slot);
        AddItem(UnlockedSalesPackageMap, new ItemPackage(new ShopItem(item.GetType(), item.GetName(), itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), false), item.GetCreditCost(), item.GetPointCost(), item.GetTokenCost(), item.GetEconomyCost(), item.IsFree(), item.GetSalesPackageId()), slot);
    }
}
