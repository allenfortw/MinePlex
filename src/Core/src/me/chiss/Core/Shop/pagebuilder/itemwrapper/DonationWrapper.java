package me.chiss.Core.Shop.pagebuilder.itemwrapper;

import me.chiss.Core.Shop.salespackage.DonationPackage;
import mineplex.minecraft.shop.item.ISalesPackage;

public class DonationWrapper implements IItemWrapper
{
	@Override
	public ISalesPackage WrapPackage(ISalesPackage salesPackage) 
	{
		return new DonationPackage(salesPackage);
	}
}
