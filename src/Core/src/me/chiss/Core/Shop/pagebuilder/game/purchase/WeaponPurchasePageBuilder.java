package me.chiss.Core.Shop.pagebuilder.game.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.purchase.WeaponPurchasePage;
import me.chiss.Core.Shop.pagebuilder.game.WeaponPageBuilder;
import me.chiss.Core.Shop.salespackage.DonationPackage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Weapon.IWeapon;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.shop.item.ISalesPackage;
import mineplex.minecraft.shop.item.ItemPackage;

public class WeaponPurchasePageBuilder extends WeaponPageBuilder
{
    public WeaponPurchasePageBuilder(Shop shop, String title, IWeaponFactory weaponFactory, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
    	super(shop, title, weaponFactory, gameClassFactory, currencyHandlers);
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new WeaponPurchasePage(Shop, Title, CurrencyHandlers, ClassUnlockedSalesPackageMap, ClassLockedSalesPackageMap);
    }
    
    @Override
    protected void BuildWeaponPackages(IWeapon weapon, HashMap<Integer, ISalesPackage> unlockedClassMap,  HashMap<Integer, ISalesPackage> lockedClassMap, int slot)
    {
    	List<String> itemLore = new ArrayList<String>();
    	
    	itemLore.add(C.cYellow + weapon.GetCreditCost() + "c" + C.cWhite + " or " + C.cYellow + weapon.GetPointCost()+ "p");
    	itemLore.add(C.cBlack);
    	
    	itemLore.addAll(Arrays.asList(weapon.GetDesc()));
    	
    	for (int i = 2; i < itemLore.size(); i++)
    	{
    		itemLore.set(i, C.cGray + itemLore.get(i));
    	}
    	
        lockedClassMap.put(slot, new DonationPackage(new ItemPackage(new ShopItem(weapon.GetType(), weapon.GetName(), itemLore.toArray(new String[itemLore.size()]), weapon.GetAmount(), true), weapon.GetCreditCost(), weapon.GetPointCost(), weapon.GetTokenCost(), weapon.GetEconomyCost(), weapon.IsFree(), weapon.GetSalesPackageId())));
        unlockedClassMap.put(slot, new DonationPackage(new ItemPackage(new ShopItem(weapon.GetType(), weapon.GetName(), itemLore.toArray(new String[itemLore.size()]), weapon.GetAmount(), false), weapon.GetCreditCost(), weapon.GetPointCost(), weapon.GetTokenCost(), weapon.GetEconomyCost(), weapon.IsFree(), weapon.GetSalesPackageId())));
    }
}