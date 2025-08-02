package me.chiss.Core.Shop.salespackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.chiss.Core.Class.IPvpClass;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.shop.item.ISalesPackage;
import me.chiss.Core.ClientData.IClientClass;
import mineplex.core.account.CoreClient;
import net.minecraft.server.v1_6_R2.IInventory;

public class DefaultClassPackage implements ISalesPackage
{
    private IPvpClass _gameClass;
    private HashMap<Integer, ShopItem> _shopItemMap;
    private int _itemCost = 120;
    private int _skillCost = 120;
    private boolean _locked;
    
    private HashMap<Integer, ShopItem> _defaultItems;
    private List<ShopItem> _defaultSkills;
    
    public DefaultClassPackage(IPvpClass gameClass, String[] desc, boolean donation, boolean locked)
    {
        _gameClass = gameClass;
        _locked = locked;
        _shopItemMap = new HashMap<Integer, ShopItem>();
        _defaultItems = new HashMap<Integer, ShopItem>();
        _defaultSkills = new ArrayList<ShopItem>();
 
        AddDefaultItem(new ShopItem(Material.IRON_SWORD, "Standard Sword", 1, false), 0);
        AddDefaultItem(new ShopItem(Material.IRON_AXE, "Standard Axe", 1, false), 1);
        
        if (_gameClass.GetType() == ClassType.Knight)
        {
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 2);
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 3);
        }
        else if (_gameClass.GetType() == ClassType.Ranger)
        {
            AddDefaultItem(new ShopItem(Material.BOW, "Standard Bow", 1, false), 2);
            AddDefaultItem(new ShopItem(Material.ARROW, "Arrow", 30, false), 3);
        }
        else if (_gameClass.GetType() == ClassType.Brute)
        {
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 2);
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 3);
        }
        else if (_gameClass.GetType() == ClassType.Assassin)
        {
            AddDefaultItem(new ShopItem(Material.BOW, "Standard Bow", 1, false), 2);
            AddDefaultItem(new ShopItem(Material.ARROW, "Arrow", 30, false), 3);
        }
        else if (_gameClass.GetType() == ClassType.Mage)
        {
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 2);
            AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 3);
        }
        
        AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 4);
        AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 5);
        AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 6);
        AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 7);
        AddDefaultItem(new ShopItem(Material.MUSHROOM_SOUP, "Mushroom Soup", 1, false), 8);
        
        
        String name = _gameClass.GetName() + (donation ? " Class" : " Default Build");
        
        SetHelmet(new ShopItem(_gameClass.GetHead(), name, desc, 1, _locked));
        SetChestplate(new ShopItem(_gameClass.GetChestplate(), name, desc, 1, _locked));
        SetLeggings(new ShopItem(_gameClass.GetLeggings(), name, desc, 1, _locked));
        SetBoots(new ShopItem(_gameClass.GetBoots(), name, desc, 1, _locked));
    }
    
    public IPvpClass GetGameClass()
    {
    	return _gameClass;
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
        
        for (Entry<Integer, ShopItem> defaultItem : _defaultItems.entrySet())
        {
            ShopItem deliverable = defaultItem.getValue().clone();
            deliverable.SetDeliverySettings();
            
            player.GetInventory().setItem(defaultItem.getKey(), deliverable);
            player.PutDefaultItem(deliverable.clone(), defaultItem.getKey());
        }
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
            player.Class().SetGameClass(_gameClass);
            DeliverTo(player.Class());
            player.Donor().PurchaseItem(_itemCost);
            player.Donor().PurchaseSkill(_skillCost);
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
        inventory.setItem(slot, GetHelmet().getHandle());
        slot += 9;

        slotList.add(slot);
        inventory.setItem(slot, GetChestplate().getHandle());
        slot += 9;

        slotList.add(slot);
        inventory.setItem(slot, GetLeggings().getHandle());
        slot += 9;

        slotList.add(slot);
        inventory.setItem(slot, GetBoots().getHandle());
        slot += 9;
         
        return slotList;
    }

    public void AddDefaultItem(ShopItem shopItem, int slot)
    {
        _defaultItems.put(slot, shopItem);
    }
    
    public void AddDefaultSkill(ShopItem shopItem)
    {
        _defaultSkills.add(shopItem);
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
