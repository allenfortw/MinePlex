package me.chiss.Core.Shop.pagebuilder.itemwrapper;

import mineplex.minecraft.shop.item.ISalesPackage;

public class NoWrapper implements IItemWrapper
{
	@Override
	public ISalesPackage WrapPackage(ISalesPackage salesPackage) 
	{
		return salesPackage;
	}
}
