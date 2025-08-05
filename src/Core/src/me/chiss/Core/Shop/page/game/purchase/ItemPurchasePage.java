package me.chiss.Core.Shop.page.game.purchase;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Sound;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.shop.item.ISalesPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.game.ItemPage;
import me.chiss.Core.Shop.salespackage.ShopItem;

public class ItemPurchasePage extends ItemPage
{
    public ItemPurchasePage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<Integer, ISalesPackage> unlockedMap, HashMap<Integer, ISalesPackage> lockedMap)
    {
        super(shop, title, currencyHandlers, unlockedMap, lockedMap);
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
        	setItem(4, new ShopItem(CurrentCurrencyHandler.GetItemDisplayType(), CurrentCurrencyHandler.GetName(), lore, 0, false, true));    
        }
    }
	
	@Override
    protected void PurchaseSalesPackage(CoreClient player, ISalesPackage sellable)
    {
		if (!player.Donor().Owns(sellable.GetSalesPackageId()) && !sellable.IsFree() && !player.Rank().Has(Rank.EMERALD, false))
			new ConfirmationPage(Shop, this, sellable, CurrentCurrencyHandler, player).OpenForPlayer(player);
		else
		{
			player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
		}
    }
}