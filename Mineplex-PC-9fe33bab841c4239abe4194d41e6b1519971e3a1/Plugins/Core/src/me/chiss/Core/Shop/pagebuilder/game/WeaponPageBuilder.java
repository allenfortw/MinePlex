package me.chiss.Core.Shop.pagebuilder.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Class.IPvpClass;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.shop.item.ISalesPackage;
import mineplex.minecraft.shop.item.ItemPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.WeaponPage;
import me.chiss.Core.Shop.pagebuilder.IPageBuilder;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Weapon.IWeapon;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;

public class WeaponPageBuilder implements IPageBuilder
{
	protected Shop Shop;
    protected String Title;
    
    private IWeaponFactory _weaponFactory;

    protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> ClassLockedSalesPackageMap;
    protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> ClassUnlockedSalesPackageMap;

    protected List<ICurrencyHandler> CurrencyHandlers;
    
    public WeaponPageBuilder(Shop shop, String title, IWeaponFactory weaponFactory, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
        Shop = shop;
        CurrencyHandlers = Arrays.asList(currencyHandlers);
        Title = title;
        _weaponFactory = weaponFactory;
        
        ClassLockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, ISalesPackage>>();
        ClassUnlockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, ISalesPackage>>();
        
        for (IPvpClass gameClass : gameClassFactory.GetGameClasses())
        {            
            HashMap<Integer, ISalesPackage> lockedClassMap = new HashMap<Integer, ISalesPackage>();
            HashMap<Integer, ISalesPackage> unlockedClassMap = new HashMap<Integer, ISalesPackage>();            
            
            for (IWeapon weapon : _weaponFactory.GetWeapons())
            {
            	int slot = 9;
            	
            	switch (weapon.GetType())
            	{
    	        	case IRON_SWORD:
    	        		slot = 9;
    	        		break;
    	        	case DIAMOND_SWORD:
    	        		slot = 10;
    	        		break;
    	        	case GOLD_SWORD:
    	        		slot = 11;
    	        		break;
    	        	case IRON_AXE:
    	        		slot = 18;
    	        		break;
    	        	case DIAMOND_AXE:
    	        		slot = 19;
    	        		break;
    	        	case GOLD_AXE:
    	        		slot = 20;
    	        		break;
    	        	case BOW:
    	        		if (gameClass.GetType() != ClassType.Assassin && gameClass.GetType() != ClassType.Ranger)
    	        			continue;
    	        		
    	        		slot = 27;
    	        		if (weapon.GetName().contains("Booster"))
    	        			slot ++;
    	        		else if (weapon.GetName().contains("Power"))
    	        			slot += 2;
    	        		break;
    				default:
    					break;
            	}
            	
            	BuildWeaponPackages(weapon, unlockedClassMap, lockedClassMap, slot);        	
            }
            
            ClassLockedSalesPackageMap.put(gameClass, lockedClassMap);
            ClassUnlockedSalesPackageMap.put(gameClass, unlockedClassMap);
        }
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new WeaponPage(Shop, Title, CurrencyHandlers, ClassUnlockedSalesPackageMap, ClassLockedSalesPackageMap);
    }
    
    protected void BuildWeaponPackages(IWeapon weapon, HashMap<Integer, ISalesPackage> unlockedClassMap,  HashMap<Integer, ISalesPackage> lockedClassMap, int slot)
    {
    	List<String> itemLore = new ArrayList<String>();
    	
    	itemLore.add(C.cYellow + weapon.GetTokenCost() + " Tokens");
    	itemLore.add(C.cBlack);
    	
    	itemLore.addAll(Arrays.asList(weapon.GetDesc()));
    	
    	for (int i = 2; i < itemLore.size(); i++)
    	{
    		itemLore.set(i, C.cGray + itemLore.get(i));
    	}
        
        lockedClassMap.put(slot, new ItemPackage(new ShopItem(weapon.GetType(), weapon.GetName(), itemLore.toArray(new String[itemLore.size()]),  weapon.GetAmount(), true), weapon.GetCreditCost(), weapon.GetPointCost(), weapon.GetTokenCost(), weapon.GetEconomyCost(), weapon.IsFree(), weapon.GetSalesPackageId()));
        unlockedClassMap.put(slot, new ItemPackage(new ShopItem(weapon.GetType(), weapon.GetName(), itemLore.toArray(new String[itemLore.size()]), weapon.GetAmount(), false), weapon.GetCreditCost(), weapon.GetPointCost(), weapon.GetTokenCost(), weapon.GetEconomyCost(), weapon.IsFree(), weapon.GetSalesPackageId()));
    }
}