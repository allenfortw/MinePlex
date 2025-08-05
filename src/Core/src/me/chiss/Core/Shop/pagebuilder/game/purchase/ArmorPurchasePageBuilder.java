package me.chiss.Core.Shop.pagebuilder.game.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.purchase.ArmorPurchasePage;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.DonationWrapper;
import me.chiss.Core.Shop.salespackage.DefaultClassPackage;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPageBuilder;

public class ArmorPurchasePageBuilder extends ArmorPageBuilder
{
	public ArmorPurchasePageBuilder(Shop shop, String title, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
        super(shop, title, new DonationWrapper(), gameClassFactory, currencyHandlers);
    }
	
    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new ArmorPurchasePage(Shop, Title, CurrencyHandlers, UnlockedSalesPackageMap, LockedSalesPackageMap);
    }
    
    protected void PrepareCachedMaps(IPvpClass gameClass, int slot)
    {
    	List<String> lockedClassDesc = new ArrayList<String>();
    	List<String> unlockedClassDesc = new ArrayList<String>();

    	lockedClassDesc.add(C.cYellow + gameClass.GetCreditCost() + "c" + C.cWhite + " or " + C.cYellow + gameClass.GetPointCost() + "p ");
    	unlockedClassDesc.add(C.cBlack);
    	
    	lockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    	unlockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    	
    	for (int i = 2; i < lockedClassDesc.size(); i++)
    	{
    		lockedClassDesc.set(i, C.cGray + lockedClassDesc.get(i));
    	}
    	
    	for (int i = 1; i < unlockedClassDesc.size(); i++)
    	{
    		unlockedClassDesc.set(i, C.cGray + unlockedClassDesc.get(i));
    	}

        
    	AddItem(LockedSalesPackageMap, new DefaultClassPackage(gameClass, lockedClassDesc.toArray(new String[lockedClassDesc.size()]), true, true), slot);
        AddItem(UnlockedSalesPackageMap, new DefaultClassPackage(gameClass, unlockedClassDesc.toArray(new String[unlockedClassDesc.size()]), true, false), slot);
    }
}
