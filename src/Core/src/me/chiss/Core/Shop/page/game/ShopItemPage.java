package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Sound;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.ShopPageBase;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class ShopItemPage extends ShopPageBase
{
    public ShopItemPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<Integer, ISalesPackage> unlockedMap, HashMap<Integer, ISalesPackage> lockedMap)
    {
        super(shop, title, currencyHandlers, unlockedMap, lockedMap);
    }
    
    @Override
    protected void SetResetButtonLore(int balance, String[] lore)
    {        
    	setItem(4, new ShopItem(CurrentCurrencyHandler.GetItemDisplayType(), CurrentCurrencyHandler.GetName(), lore, 0, false, true));
    }
    
    @Override
    protected void PurchaseSalesPackage(CoreClient player, ISalesPackage sellable)
    {
    	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ORB_PICKUP, 1f, .6f);
        sellable.PurchaseBy(player);
        CurrentCurrencyHandler.Deduct(player, sellable);
    }
}