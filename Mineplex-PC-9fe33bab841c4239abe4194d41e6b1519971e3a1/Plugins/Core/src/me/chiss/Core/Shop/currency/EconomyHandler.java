package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class EconomyHandler implements ICurrencyHandler 
{
	@Override
	public int GetCost(ISalesPackage salesPackage)
	{
		return salesPackage.GetEconomyCost();
	}
	
	@Override
	public void Deduct(CoreClient player, ISalesPackage salesPackage) 
	{
		player.Game().SetEconomyBalance(player.Game().GetEconomyBalance() - salesPackage.GetEconomyCost());
	}

	@Override
	public void Return(CoreClient player, ISalesPackage salesPackage)
	{
		player.Game().SetEconomyBalance(player.Game().GetEconomyBalance() + salesPackage.ReturnFrom(player) * salesPackage.GetEconomyCost());
	}

	@Override
	public String GetName() 
	{
		return "Coins";
	}

	@Override
	public boolean CanAfford(CoreClient player, ISalesPackage salesPackage)
	{
		return player.Game().GetEconomyBalance() >= salesPackage.GetEconomyCost();
	}

	@Override
	public Material GetItemDisplayType() 
	{
		return Material.GOLD_INGOT;
	}

	@Override
	public void ResetBalance(CoreClient player)
	{
		//player.Game().SetEconomyBalance(0);
	}

	@Override
	public int GetBalance(CoreClient player) 
	{
		return player.Game().GetEconomyBalance();
	}
}
