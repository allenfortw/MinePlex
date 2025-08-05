package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class PointHandler implements ICurrencyHandler 
{
	@Override
	public int GetCost(ISalesPackage salesPackage)
	{
		return salesPackage.GetPointCost();
	}
	
	@Override
	public void Deduct(CoreClient player, ISalesPackage salesPackage) 
	{
		player.Donor().SetPoints(player.Donor().GetGreenGems() - salesPackage.GetPointCost());
	}

	@Override
	public void Return(CoreClient player, ISalesPackage salesPackage)
	{
		player.Donor().SetPoints(player.Donor().GetGreenGems() + salesPackage.ReturnFrom(player) * salesPackage.GetPointCost());
	}

	@Override
	public String GetName() 
	{
		return "Points";
	}

	@Override
	public boolean CanAfford(CoreClient player, ISalesPackage salesPackage)
	{
		return player.Donor().GetGreenGems() >= salesPackage.GetPointCost();
	}

	@Override
	public Material GetItemDisplayType() 
	{
		return Material.EMERALD;
	}

	@Override
	public void ResetBalance(CoreClient player) 
	{
	}

	@Override
	public int GetBalance(CoreClient player) 
	{
		return player.Donor().GetGreenGems();
	}
}