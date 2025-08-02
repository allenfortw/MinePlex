package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import mineplex.minecraft.shop.item.ISalesPackage;

public class PvpCustomBuildPage extends CustomBuildPage
{
	public PvpCustomBuildPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> unlockedClassMap, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> lockedClassMap)
    {
        super(shop, title, currencyHandlers, null, null);
        
        UnlockedClassMap = unlockedClassMap;
        LockedClassMap = lockedClassMap;
        
        equipItems = false;
        equipDefaultArmor = false;
        saveActiveCustomBuild = true;
    }
}
