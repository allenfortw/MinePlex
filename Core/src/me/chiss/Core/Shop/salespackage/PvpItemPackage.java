package me.chiss.Core.Shop.salespackage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.chiss.Core.ClientData.IClientClass;
import me.chiss.Core.Utility.InventoryUtil;
import mineplex.core.account.CoreClient;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.shop.item.ISalesPackage;
import net.minecraft.server.v1_6_R2.IInventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;

public class PvpItemPackage implements ISalesPackage
{
    private ShopItem _shopItem;
    private int _creditCost;
    private int _pointCost;
    private int _tokenCost;
    private int _economyCost;
    private boolean _free;
    private int _salesPackageId;

    public PvpItemPackage(ShopItem shopItem, int creditCost, int pointCost, int tokenCost, int economyCost, boolean isFree, int salesPackageId)
    {
        _shopItem = shopItem;
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
        
        if (InventoryUtil.first((CraftInventory)player.Class().GetInventory(), 9, null, true) == -1)
        	return false;
        else
        	return true;
    }

    public ItemStack GetStack(String player)
    {
    	byte data = 0;
    	if (_shopItem.getData() != null)
    		data =_shopItem.getData().getData();
    	
    	ItemStack stack;
		if (_shopItem.getType().getMaxStackSize() == 1)
			stack = ItemStackFactory.Instance.CreateStack(_shopItem.getType(), data, _shopItem.getAmount(), 
					null, new String[] {}, player + " Shop");
		else
			stack = ItemStackFactory.Instance.CreateStack(_shopItem.getType(), data, _shopItem.getAmount());
		
		return stack;
    }
    
    @Override 
    public void DeliverTo(IClientClass player)
    {
        player.GetInventory().addItem(GetStack(player.GetName()));
    }
    
    public final String DATE_FORMAT_DAY = "yyyy-MM-dd";
	public String date() 
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DAY);
		return sdf.format(cal.getTime());
	}
    
    @Override 
    public void DeliverTo(IClientClass player, int slot)
    {
        player.GetInventory().setItem(slot, GetStack(player.GetName()));
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
