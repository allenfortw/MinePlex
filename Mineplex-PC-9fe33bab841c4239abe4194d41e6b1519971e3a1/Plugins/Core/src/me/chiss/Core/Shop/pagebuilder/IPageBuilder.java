package me.chiss.Core.Shop.pagebuilder;

import me.chiss.Core.Shop.page.IShopPage;
import mineplex.core.account.CoreClient;

public interface IPageBuilder
{
    IShopPage BuildForPlayer(CoreClient player);
}
