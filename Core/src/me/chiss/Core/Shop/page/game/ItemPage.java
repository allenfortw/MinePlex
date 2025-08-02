package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.shop.item.ISalesPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.ShopPageBase;

public class ItemPage extends ShopPageBase
{
    public ItemPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<Integer, ISalesPackage> unlockedMap, HashMap<Integer, ISalesPackage> lockedMap)
    {
        super(shop, title, currencyHandlers, unlockedMap, lockedMap);
    }
    
    protected boolean ShowUnlockedAtSlot(CoreClient player, int slot)
    {
    	return super.ShowUnlockedAtSlot(player, slot) || player.Rank().Has(Rank.EMERALD, false);
    }
}