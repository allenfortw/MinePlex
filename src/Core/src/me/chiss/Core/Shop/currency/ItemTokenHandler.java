package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class ItemTokenHandler implements ICurrencyHandler 
{
	@Override
	public int GetCost(ISalesPackage salesPackage)
	{
		return salesPackage.GetTokenCost();
	}
	
	@Override
	public void Deduct(CoreClient player, ISalesPackage salesPackage) 
	{
		player.Donor().SetItemTokens(player.Donor().GetItemTokens() - salesPackage.GetTokenCost());
	}

	@Override
	public void Return(CoreClient player, ISalesPackage salesPackage)
	{
		player.Donor().SetItemTokens(player.Donor().GetItemTokens() + salesPackage.ReturnFrom(player) * salesPackage.GetTokenCost());
	}

	@Override
	public String GetName() 
	{
		return "Item Tokens";
	}

	@Override
	public boolean CanAfford(CoreClient player, ISalesPackage salesPackage)
	{
		return player.Donor().GetItemTokens() >= salesPackage.GetTokenCost();
	}

	@Override
	public Material GetItemDisplayType() 
	{
		return Material.EMERALD;
	}

	@Override
	public void ResetBalance(CoreClient player) 
	{
		player.Donor().ResetItemTokens();
	}

	@Override
	public int GetBalance(CoreClient player) 
	{
		return player.Donor().GetItemTokens();
	}
}