package me.chiss.Core.Shop.currency;


import org.bukkit.Material;

import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;

public interface ICurrencyHandler 
{
	int GetCost(ISalesPackage salesPackage);
	void Deduct(CoreClient player, ISalesPackage salesPackage);
	void Return(CoreClient player, ISalesPackage salesPackage);
	String GetName();
	Material GetItemDisplayType();
	boolean CanAfford(CoreClient player, ISalesPackage salesPackage);
	void ResetBalance(CoreClient player);
	int GetBalance(CoreClient player);
}
