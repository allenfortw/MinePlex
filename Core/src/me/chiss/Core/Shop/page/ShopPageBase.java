package me.chiss.Core.Shop.page;

import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.salespackage.ISalesPackage;
import me.chiss.Core.Shop.salespackage.ItemPackage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;

public abstract class ShopPageBase extends PageBase<IShopPage> implements IShopPage
{
    protected Shop Shop;
    protected HashMap<Integer, ISalesPackage> SalesPackageMap;
    protected HashMap<Integer, ISalesPackage> UnlockedMap;
    protected HashMap<Integer, ISalesPackage> LockedMap;
    protected List<ICurrencyHandler> CurrencyHandlers;
    protected ICurrencyHandler CurrentCurrencyHandler;
    
    public ShopPageBase(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<Integer, ISalesPackage> unlockedMap, HashMap<Integer, ISalesPackage> lockedMap)
    {
        super(title);
        
        Shop = shop;
        SalesPackageMap = new HashMap<Integer, ISalesPackage>();
        UnlockedMap = unlockedMap;
        LockedMap = lockedMap;
        CurrencyHandlers = currencyHandlers;
        
        if (CurrencyHandlers != null && CurrencyHandlers.size() > 0)
        	CurrentCurrencyHandler = CurrencyHandlers.get(0);
    }
    
    public ISalesPackage GetItem(int i)
    {
        return SalesPackageMap.get(i);
    }
    
    public void UpdateBalance(CoreClient player)
    {
        int balance = CurrentCurrencyHandler.GetBalance(player);
        
    	String[] lore = new String[5];
    	lore[0] = "§rBalance: " + ChatColor.YELLOW + balance + " " + CurrentCurrencyHandler.GetName();
        
    	SetResetButtonLore(balance, lore);
    }

    protected void SetResetButtonLore(int balance, String[] lore)
    {        
    	lore[1] = "§rRight-click to return all items on this page.";
    	
    	ShopItem currencyItem = new ShopItem(CurrentCurrencyHandler.GetItemDisplayType(), CurrentCurrencyHandler.GetName(), lore, 0, false, true);
    	
    	/* Max is 64 still.
    	if (balance <= 128)
    		currencyItem.setAmount(balance);
    	*/
    	
    	setItem(4, currencyItem);
    }
    
    public void ResetVisuals()
    {
        
    }
    
    public void Reset(CoreClient player)
    {
    	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.SPIDER_WALK, 1, .6f);
    	
        for (ISalesPackage salesPackage : SalesPackageMap.values())
        {
            CurrentCurrencyHandler.Return(player, salesPackage);
        }
    }

    public void OpenForPlayer(CoreClient playerClient)
    {
    	PrepSlotsForPlayer(playerClient);
    	
    	UpdateBalance(playerClient);
    	
        playerClient.GetPlayer().openInventory(this);
    }
    
    public void PrepSlotsForPlayer(CoreClient playerClient)
    {
    	for (int slot : UnlockedMap.keySet())
    	{
    		UpdateSlot(playerClient, slot);
    	}
    }
    
    public void CloseForPlayer(CoreClient playerClient)
    {
    	playerClient.GetPlayer().closeInventory();
    	this.inventory.onClose((CraftPlayer)playerClient.GetPlayer());
    }
    
    public void ChangeCurrency(CoreClient playerClient)
    {
    	if (CurrencyHandlers != null)
    	{
	    	if (CurrencyHandlers.size() > 1)
	    	{
	    		playerClient.GetPlayer().playSound(playerClient.GetPlayer().getLocation(), Sound.NOTE_PLING, 1, .6f);
	    	}
	    	else
	    	{
	    		playerClient.GetPlayer().playSound(playerClient.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1, .6f);
	    	}
	    	
	    	CurrentCurrencyHandler = GetNextCurrencyHandler();
    	}
    }
    
    protected ICurrencyHandler GetNextCurrencyHandler()
    {
    	if (CurrencyHandlers.size() > 1)
    	{
    		int currentIndex = CurrencyHandlers.indexOf(CurrentCurrencyHandler);
    		
    		if (currentIndex + 1 < CurrencyHandlers.size())
    		{
    			return CurrencyHandlers.get(currentIndex + 1);
    		}
    		else
    		{
    			return CurrencyHandlers.get(0);
    		}
    	}
    	
    	return CurrentCurrencyHandler;
    }
    
    public void PlayerWants(CoreClient player, int slot)
    {
        ISalesPackage sellable = GetItem(slot);    
        
        if (sellable != null && CanPurchasePackage(player, sellable))
        {
        	PurchaseSalesPackage(player, sellable);
        }
        else
        {
        	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
        }
        
        UpdateSlot(player, slot);
    }
    
    protected boolean CanPurchasePackage(CoreClient player, ISalesPackage sellable)
    {
    	return CurrentCurrencyHandler.CanAfford(player, sellable) && sellable.CanFitIn(player);
    }
    
    protected void PurchaseSalesPackage(CoreClient player, ISalesPackage sellable)
    {
    	player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ORB_PICKUP, 1f, .6f);
        sellable.PurchaseBy(player);
        CurrentCurrencyHandler.Deduct(player, sellable);
    }
    
    public void PlayerReturning(CoreClient player, int slot)
    {
        ISalesPackage sellable = GetItem(slot);
        
        if (sellable != null && CurrentCurrencyHandler != null)
        {        	
        	CurrentCurrencyHandler.Return(player, sellable);
        }
    }
       
    public void AddItem(ISalesPackage salesItem, int slot)
    {        
        for (Integer salesItemslot : salesItem.AddToCategory(getInventory(), slot))
        {
            SalesPackageMap.put(salesItemslot, salesItem);
        }
    }
    
    public void UpdateSlot(CoreClient player, int slot)
    {
    	if (UnlockedMap != null && UnlockedMap.get(slot) != null)
    	{
	        if (ShowUnlockedAtSlot(player, slot))
	        {
	            AddItem(UnlockedMap.get(slot), slot);
	        }
	        else
	        {
	            AddItem(LockedMap.get(slot), slot);
	        }
    	}
    }
    
    protected boolean ShowUnlockedAtSlot(CoreClient player, int slot)
    {
    	return player.Donor().Owns(UnlockedMap.get(slot).GetSalesPackageId()) || UnlockedMap.get(slot).IsFree();
    }
}
