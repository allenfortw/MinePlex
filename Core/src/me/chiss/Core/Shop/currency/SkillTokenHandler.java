package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public class SkillTokenHandler implements ICurrencyHandler 
{
	@Override
	public int GetCost(ISalesPackage salesPackage)
	{
		return salesPackage.GetTokenCost();
	}
	
	@Override
	public void Deduct(CoreClient player, ISalesPackage salesPackage) 
	{
		player.Donor().SetSkillTokens(player.Donor().GetSkillTokens() - salesPackage.GetTokenCost());
	}

	@Override
	public void Return(CoreClient player, ISalesPackage salesPackage)
	{
		player.Donor().SetSkillTokens(player.Donor().GetSkillTokens() + salesPackage.ReturnFrom(player) * salesPackage.GetTokenCost());
	}

	@Override
	public String GetName() 
	{
		return "Skill Tokens";
	}

	@Override
	public boolean CanAfford(CoreClient player, ISalesPackage salesPackage)
	{
		return player.Donor().GetSkillTokens() >= salesPackage.GetTokenCost();
	}

	@Override
	public Material GetItemDisplayType() 
	{
		return Material.DIAMOND;
	}

	@Override
	public void ResetBalance(CoreClient player) 
	{
		player.Donor().ResetSkillTokens();
	}

	@Override
	public int GetBalance(CoreClient player) 
	{
		return player.Donor().GetSkillTokens();
	}
}
