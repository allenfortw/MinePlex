package me.chiss.Core.Shop.page.game.purchase;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.game.purchase.ConfirmationPage;
import me.chiss.Core.Shop.salespackage.DefaultClassPackage;
import me.chiss.Core.Shop.salespackage.DonationPackage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPage;
import mineplex.minecraft.shop.item.ISalesPackage;

public class ArmorPurchasePage extends ArmorPage
{
    public ArmorPurchasePage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<Integer, ISalesPackage> unlockedMap, HashMap<Integer, ISalesPackage> lockedMap)
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
        player.Class().GetInventory().clear();
        
        for (ICurrencyHandler allCurrencyHandler : CurrencyHandlers)
        {
        	allCurrencyHandler.ResetBalance(player);
        }
        
        player.Class().SetGameClass(null);
        
    	player.Class().ClearDefaults();

    	if (player.Donor().Owns(sellable.GetSalesPackageId()) || sellable.IsFree())
    	{
        	DefaultClassPackage wrappedPackage = (DefaultClassPackage)((DonationPackage)sellable).GetWrappedPackage();
        	Shop.SetClassForPlayer(player, wrappedPackage.GetGameClass());
        	
            SetNextPage(NextPage);
            Shop.TurnToNextPage(player);
    	}
    	else
    	{
    		new ConfirmationPage(Shop, this, sellable, CurrentCurrencyHandler, player).OpenForPlayer(player);
    	}
    }
	
    @Override
    public void UpdateBalance(CoreClient player)
    {
        int balance = CurrentCurrencyHandler.GetBalance(player);
        
    	String[] lore = new String[5];
    	lore[0] = "§rBalance: " + ChatColor.YELLOW + balance + " " + CurrentCurrencyHandler.GetName();
        
    	SetResetButtonLore(balance, lore);
    }
}
