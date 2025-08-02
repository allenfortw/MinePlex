package me.chiss.Core.Shop.salespackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.ClientData.IClientClass;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;
import net.minecraft.server.v1_6_R2.IInventory;

public class ArmorPackage implements ISalesPackage
{
    private IPvpClass _gameClass;
    private HashMap<Integer, ShopItem> _shopItemMap;
    private int _cost = 0;
    private boolean _locked;
    
    public ArmorPackage(IPvpClass gameClass, boolean locked)
    {
        this(gameClass, 0, locked);
    }
    
    public ArmorPackage(IPvpClass gameClass, int creditCost, boolean locked)
    {
        _gameClass = gameClass;
        _cost = creditCost;
        _locked = locked;
        _shopItemMap = new HashMap<Integer, ShopItem>();
        
        SetHelmet(new ShopItem(_gameClass.GetHead(), _gameClass.GetName(), 1, _locked));
        SetChestplate(new ShopItem(_gameClass.GetChestplate(), _gameClass.GetName(), 1, _locked));
        SetLeggings(new ShopItem(_gameClass.GetLeggings(), _gameClass.GetName(), 1, _locked));
        SetBoots(new ShopItem(_gameClass.GetBoots(), _gameClass.GetName(), 1, _locked));
    }
    
    @Override
    public String GetName()
    {
    	return _gameClass.GetName();
    }
    
    @Override
    public int GetSalesPackageId()
    {
    	return _gameClass.GetSalesPackageId();
    }
    
    public void SetHelmet(ShopItem shopItem)
    {
        _shopItemMap.put(0, shopItem);
    }
    
    public ShopItem GetHelmet()
    {
        return _shopItemMap.get(0);
    }

    public void SetChestplate(ShopItem shopItem)
    {
        _shopItemMap.put(1, shopItem);
    }
    
    public ShopItem GetChestplate()
    {
        return _shopItemMap.get(1);
    }
    
    public void SetLeggings(ShopItem shopItem)
    {
        _shopItemMap.put(2, shopItem);
    }
    
    public ShopItem GetLeggings()
    {
        return _shopItemMap.get(2);
    }
    
    public void SetBoots(ShopItem shopItem)
    {
        _shopItemMap.put(3, shopItem);
    }
    
    public ShopItem GetBoots()
    {
        return _shopItemMap.get(3);
    }
    
    public int GetCost()
    {
        return _cost;
    }
    
    @Override
    public boolean IsFree()
    {
    	return _gameClass.IsFree();
    }
    
    @Override
    public boolean CanFitIn(CoreClient player)
    {
        return !_locked || _gameClass.IsFree();
    }

    @Override 
    public void DeliverTo(IClientClass player)
    {
        ShopItem head = GetHelmet().clone();
        head.SetDeliverySettings();
        
        ShopItem chest = GetChestplate().clone();
        chest.SetDeliverySettings();
        
        ShopItem legs = GetLeggings().clone();
        legs.SetDeliverySettings();
        
        ShopItem boots = GetBoots().clone();
        boots.SetDeliverySettings();
        
        player.GetInventory().setHelmet(head.clone());
        player.GetInventory().setChestplate(chest.clone());
        player.GetInventory().setLeggings(legs.clone());
        player.GetInventory().setBoots(boots.clone());
        
        player.SetDefaultHead(head);
        player.SetDefaultChest(chest);
        player.SetDefaultLegs(legs);
        player.SetDefaultFeet(boots);
    }
    
    @Override 
    public void DeliverTo(IClientClass player, int slot)
    {
        
    }
    
    @Override
    public void PurchaseBy(CoreClient player)
    {
        if (player.Class().GetGameClass() == null)
        {
            player.Class().SetGameClass(_gameClass, false);
            DeliverTo(player.Class());
        }
    }

    @Override
    public int ReturnFrom(CoreClient playerClient)
    {
        IClientClass player = playerClient.Class();
        
        ShopItem head = GetHelmet().clone();
        head.SetDeliverySettings();
        
        ShopItem chest = GetChestplate().clone();
        chest.SetDeliverySettings();
        
        ShopItem legs = GetLeggings().clone();
        legs.SetDeliverySettings();
        
        ShopItem boots = GetBoots().clone();
        boots.SetDeliverySettings();
        
        if ((player.GetInventory().getHelmet() != null && player.GetInventory().getHelmet().equals(head))
            && (player.GetInventory().getChestplate() != null && player.GetInventory().getChestplate().equals(chest))
            && (player.GetInventory().getLeggings() != null && player.GetInventory().getLeggings().equals(legs))
            && (player.GetInventory().getBoots() != null && player.GetInventory().getBoots().equals(boots)))  
        {

            player.GetInventory().setArmorContents(new ItemStack[4]);
            player.AddDefaultArmor(player.GetInventory().getArmorContents());
            player.SetGameClass(null);
            
            return 1;
        }
        
        return 0;
    }

    @Override
    public List<Integer> AddToCategory(IInventory inventory, int slot)
    {
        List<Integer> slotList = new ArrayList<Integer>();

        slotList.add(slot);
        inventory.setItem(slot, new ShopItem(Material.ANVIL, _gameClass.GetName() + " Custom Build", null, 0, _locked, true).getHandle());
        
        return slotList;
    }
    
	@Override
	public int GetTokenCost() 
	{
		return 0;
	}

	@Override
	public int GetCreditCost() 
	{
		return _gameClass.GetCreditCost();
	}

	@Override
	public int GetPointCost() 
	{
		return _gameClass.GetPointCost();
	}
	
	@Override
	public int GetEconomyCost() 
	{
		return 0;
	}
}
