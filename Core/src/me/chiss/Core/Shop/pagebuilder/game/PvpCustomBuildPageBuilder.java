package me.chiss.Core.Shop.pagebuilder.game;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.PvpCustomBuildPage;
import mineplex.core.account.CoreClient;

public class PvpCustomBuildPageBuilder extends CustomBuildPageBuilder
{
	public PvpCustomBuildPageBuilder(Shop shop, String title, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
	{
		super(shop, title, gameClassFactory, currencyHandlers);  
	}
 
	@Override
	public IShopPage BuildForPlayer(CoreClient player)
	{
		return new PvpCustomBuildPage(Shop, Title, CurrencyHandlers, ClassUnlockedSalesPackageMap, ClassLockedSalesPackageMap);
	}
}
