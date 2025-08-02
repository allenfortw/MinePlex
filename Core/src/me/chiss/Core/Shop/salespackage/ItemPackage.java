package me.chiss.Core.Shop.salespackage;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import me.chiss.Core.ClientData.IClientClass;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Utility.InventoryUtil;
import mineplex.core.account.CoreClient;
import net.minecraft.server.v1_6_R2.IInventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;

public class ItemPackage implements ISalesPackage
{
    private ShopItem _shopItem;
    private boolean _restrictToHotbar;
    private int _creditCost;
    private int _pointCost;
    private int _tokenCost;
    private int _economyCost;
    private boolean _free;
    private int _salesPackageId;

    public ItemPackage(ShopItem shopItem, int creditCost, int pointCost, int tokenCost, int economyCost, boolean isFree, int salesPackageId)
    {
        this(shopItem, true, creditCost, pointCost, tokenCost, economyCost, isFree, salesPackageId);
    }
    
    public ItemPackage(ShopItem shopItem, boolean restrictToHotbar, int creditCost, int pointCost, int tokenCost, int economyCost, boolean isFree, int salesPackageId)
    {
        _shopItem = shopItem;
        _restrictToHotbar = restrictToHotbar;
        _creditCost = creditCost;
        _pointCost = pointCost;
        _economyCost = economyCost;
        _tokenCost = tokenCost;
        _free = isFree;
        _salesPackageId = salesPackageId;
    }

    @Override
    public String GetName()
    {
    	return _shopItem.GetName();
    }
    
    @Override
    public int GetSalesPackageId()
    {
    	return _salesPackageId;
    }
    
    public int GetCreditCost()
    {
        return _creditCost;
    }
    
    public int GetPointCost()
    {
        return _pointCost;
    }
    
    public int GetTokenCost()
    {
        return _tokenCost;
    }
    
    public int GetEconomyCost()
    {
        return _economyCost;
    }
    
    @Override
    public boolean IsFree()
    {
    	return _free;
    }

    @Override
    public boolean CanFitIn(CoreClient player)
    {
        if (_shopItem.IsLocked() && !IsFree())
            return false;
        
        for (ItemStack itemStack : player.Class().GetInventory())
        {
            if (itemStack != null && itemStack.getType() == _shopItem.getType() && (itemStack.getAmount() + _shopItem.getAmount()) <= (itemStack.getType() == Material.ARROW ? itemStack.getMaxStackSize() : 1))
            {
                return true;
            }
        }
        
        if (_tokenCost == 0 && _creditCost == 0 && _pointCost == 0)
        	return true;
        
        if (InventoryUtil.first((CraftInventory)player.Class().GetInventory(), _restrictToHotbar ? 9 : player.Class().GetInventory().getSize(), null, true) == -1)
        	return false;
        else
        	return true;
    }

    @Override 
    public void DeliverTo(IClientClass player)
    {
        ShopItem shopItem = _shopItem.clone();
        shopItem.SetDeliverySettings(); 
        
        if (shopItem.getType() == Material.ARROW)
        {
        	int firstEmpty = player.GetInventory().firstEmpty();
        	
            player.GetInventory().addItem(shopItem);
            
            if (player.GetInventory().firstEmpty() != firstEmpty)
            {
            	player.PutDefaultItem(player.GetInventory().getItem(firstEmpty), firstEmpty);
            }
            
            for (Entry<Integer, ? extends ItemStack> entry : player.GetInventory().all(Material.ARROW).entrySet())
    		{
            	player.PutDefaultItem(entry.getValue().clone(), entry.getKey());
    		}
        }
        else
        {
            int emptySlot = player.GetInventory().firstEmpty();
            
            player.GetInventory().setItem(emptySlot, shopItem);
            player.PutDefaultItem(shopItem.clone(), emptySlot);
        }
    }
    
    @Override 
    public void DeliverTo(IClientClass player, int slot)
    {
        ShopItem shopItem = _shopItem.clone();
        shopItem.SetDeliverySettings();
        
        player.GetInventory().setItem(slot, shopItem);
        player.PutDefaultItem(shopItem.clone(), slot);
    }
    
    @Override
    public void PurchaseBy(CoreClient player)
    {
        DeliverTo(player.Class());
    }

    @Override
    public int ReturnFrom(CoreClient player)
    {
        if (_shopItem.IsDisplay())
            return 0;
        
        ShopItem shopItem = _shopItem.clone();
        shopItem.SetDeliverySettings();
        
        int count = 0;
        
        count = InventoryUtil.GetCountOfObjectsRemoved((CraftInventory)player.Class().GetInventory(), 9, (ItemStack)shopItem);
        
        for (int i=0; i < 9; i++)
        {
        	player.Class().PutDefaultItem(player.Class().GetInventory().getItem(i), i);
        }
        
        return count;
    }
    
    @Override
    public List<Integer> AddToCategory(IInventory inventory, int slot)
    {
        inventory.setItem(slot, _shopItem.getHandle());        
        
        return Arrays.asList(slot);
    }

    public ShopItem GetItem()
    {
        return _shopItem;
    }
}
