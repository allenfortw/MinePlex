package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class CreditHandler implements ICurrencyHandler 
{
	@Override
	public int GetCost(ISalesPackage salesPackage)
	{
		return salesPackage.GetCreditCost();
	}
	
	@Override
	public void Deduct(CoreClient player, ISalesPackage salesPackage) 
	{
		player.Donor().SetCredits(player.Donor().GetBlueGems() - salesPackage.GetCreditCost());
	}

	@Override
	public void Return(CoreClient player, ISalesPackage salesPackage)
	{
		player.Donor().SetCredits(player.Donor().GetBlueGems() + salesPackage.ReturnFrom(player) * salesPackage.GetCreditCost());
	}

	@Override
	public String GetName() 
	{
		return "Credits";
	}

	@Override
	public boolean CanAfford(CoreClient player, ISalesPackage salesPackage)
	{
		return player.Donor().GetBlueGems() >= salesPackage.GetCreditCost();
	}

	@Override
	public Material GetItemDisplayType() 
	{
		return Material.DIAMOND;
	}

	@Override
	public void ResetBalance(CoreClient player)
	{
	}

	@Override
	public int GetBalance(CoreClient player) 
	{
		return player.Donor().GetBlueGems();
	}
}
