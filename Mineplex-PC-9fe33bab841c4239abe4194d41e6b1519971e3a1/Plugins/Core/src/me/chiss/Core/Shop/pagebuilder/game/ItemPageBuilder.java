package me.chiss.Core.Shop.pagebuilder.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.ItemPage;
import me.chiss.Core.Shop.pagebuilder.PageBuilderBase;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.IItemWrapper;
import me.chiss.Core.Shop.pagebuilder.itemwrapper.NoWrapper;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.classcombat.item.IItem;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;
import mineplex.minecraft.shop.item.ItemPackage;

public class ItemPageBuilder extends PageBuilderBase
{
    private IItemFactory _itemFactory;
    
    public ItemPageBuilder(Shop shop, String title, IItemWrapper itemWrapper, IItemFactory itemFactory, ICurrencyHandler...currencyHandlers)
    {
        super(shop, title, itemWrapper, currencyHandlers);
        
        _itemFactory = itemFactory;
        
        for (IItem item : _itemFactory.GetItems())
        {
        	int slot = 9;
        	
        	switch (item.GetType())
        	{
	        	case POTATO_ITEM:
	        		slot = 9;
	        		break;
	        	case APPLE:
	        		slot = 10;
	        		break;
	        	case CARROT_ITEM:
	        		slot = 11;
	        		break;
	        	case MUSHROOM_SOUP:
	        		slot = 12;
	        		break;
	        	case COMMAND:
	        		slot = 18;
	        		break;
	        	case NOTE_BLOCK:
	        		slot = 19;
	        		break;
	        	case REDSTONE_LAMP_ON:
	        		slot = 20;
	        		break;
	        	case POTION:
	        		slot = 27;
	        		break;
	        	case SLIME_BALL:
	        		slot = 28;
	        		break;
	        	case ENDER_PEARL:
	        		slot = 29;
	        		break;
	        	case STONE_AXE:
	        		slot = 30;
	        		break;
	        	case NETHER_STAR:
	        		slot = 31;
	        		break;
	        	case MELON_SEEDS:
	        		slot = 32;
	        		break;
	        	case SHEARS:
	        		slot = 36;
	        		break;
	        	case ARROW:
	        		slot = 45;
	        		break;
	        	case WEB:
	        		slot = 46;
	        		break;
				default:
					break;
        	}
        	
        	AddItemPackage(item, slot); 
        }
    }

    public ItemPageBuilder(Shop shop, String title, IItemFactory itemFactory, ICurrencyHandler...currencyHandlers)
    {
    	this(shop, title, new NoWrapper(), itemFactory, currencyHandlers);
    }
    
    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        return new ItemPage(Shop, Title, CurrencyHandlers, UnlockedSalesPackageMap, LockedSalesPackageMap);
    }
    
    protected void AddItemPackage(IItem item, int slot)
    {
    	List<String> itemLore = new ArrayList<String>();
    	
    	itemLore.add(C.cYellow + item.GetTokenCost() + " Tokens");
    	itemLore.add(C.cBlack);
    	
    	itemLore.addAll(Arrays.asList(item.GetDesc()));
    	
    	for (int i = 2; i < itemLore.size(); i++)
    	{
    		itemLore.set(i, C.cGray + itemLore.get(i));
    	}
        
        AddItem(LockedSalesPackageMap, new ItemPackage(new ShopItem(item.GetType(), item.GetName(), itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), true), item.GetCreditCost(), item.GetPointCost(), item.GetTokenCost(), item.GetEconomyCost(), item.IsFree(), item.GetSalesPackageId()), slot);
        AddItem(UnlockedSalesPackageMap, new ItemPackage(new ShopItem(item.GetType(), item.GetName(), itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), false), item.GetCreditCost(), item.GetPointCost(), item.GetTokenCost(), item.GetEconomyCost(), item.IsFree(), item.GetSalesPackageId()), slot);
    }
}
