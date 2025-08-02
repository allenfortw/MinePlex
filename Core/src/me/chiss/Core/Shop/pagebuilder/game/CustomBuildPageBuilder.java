package me.chiss.Core.Shop.pagebuilder.game;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.CustomBuildPage;
import me.chiss.Core.Shop.pagebuilder.PageBuilderBase;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.NoWrapper;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;
import mineplex.minecraft.shop.item.ItemPackage;

public class CustomBuildPageBuilder extends PageBuilderBase
{
    protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> ClassLockedSalesPackageMap;
    protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> ClassUnlockedSalesPackageMap;
    
    protected ItemPackage _unlockedEditSavedBuild;
    protected ItemPackage _lockedEditSavedBuild;
    
    protected ItemPackage _unlockedEditDontSaveBuild;
    protected ItemPackage _lockedEditDontSaveBuild;
    
    protected ItemPackage _unlockedDeleteBuild;
    protected ItemPackage _lockedDeleteBuild;
    
    protected List<ICurrencyHandler> CurrencyHandlers;
    
    public CustomBuildPageBuilder(Shop shop, String title, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
        super(shop, title, new NoWrapper(), currencyHandlers);
        
        ClassLockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, ISalesPackage>>();
        ClassUnlockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, ISalesPackage>>();
        
        _unlockedEditSavedBuild = new ItemPackage(new ShopItem(Material.ANVIL, "Edit & Save Build", new String[] { }, 1, false, true), 0, 0, 0, 0, true, -1);
        _lockedEditSavedBuild = new ItemPackage(new ShopItem(Material.ANVIL, "Edit & Save Build", new String[] { }, 1, true, true), 0, 0, 0, 0, true, -1);
        
        _unlockedEditDontSaveBuild = new ItemPackage(new ShopItem(Material.WORKBENCH, "Edit & Don't Save Build", new String[] { }, 1, false, true), 0, 0, 0, 0, true, -1);
        _lockedEditDontSaveBuild = new ItemPackage(new ShopItem(Material.WORKBENCH, "Edit & Don't Save Build", new String[] { }, 1, true, true), 0, 0, 0, 0, true, -1);
        
        _unlockedDeleteBuild = new ItemPackage(new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..."}, 1, false, true), 0, 0, 0, 0, true, -1);
        _lockedDeleteBuild = new ItemPackage(new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..." }, 1, true, true), 0, 0, 0, 0, true, -1);
        
        for (IPvpClass gameClass : gameClassFactory.GetGameClasses())
        {
        	HashMap<Integer, ISalesPackage> lockedClassMap = new HashMap<Integer, ISalesPackage>();
            HashMap<Integer, ISalesPackage> unlockedClassMap = new HashMap<Integer, ISalesPackage>();
            
        	BuildCustomBuildPage(unlockedClassMap, lockedClassMap);
        	
        	ClassLockedSalesPackageMap.put(gameClass, lockedClassMap);
            ClassUnlockedSalesPackageMap.put(gameClass, unlockedClassMap);
        }
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new CustomBuildPage(Shop, Title, CurrencyHandlers, ClassUnlockedSalesPackageMap, ClassLockedSalesPackageMap);
    }
    
    protected void BuildCustomBuildPage(HashMap<Integer, ISalesPackage> unlockedClassMap,  HashMap<Integer, ISalesPackage> lockedClassMap)
    {
    	int slot = 18;
    	
    	for (int i=0; i < 5; i++)
    	{
    		lockedClassMap.put(slot, _lockedEditSavedBuild);
        	unlockedClassMap.put(slot, _unlockedEditSavedBuild);
            
    		lockedClassMap.put(slot + 9, _lockedEditDontSaveBuild);
        	unlockedClassMap.put(slot + 9, _unlockedEditDontSaveBuild);
        	
    		lockedClassMap.put(slot + 27, _lockedDeleteBuild);
        	unlockedClassMap.put(slot + 27, _unlockedDeleteBuild);
            
            slot += 2;
    	}
    }
}