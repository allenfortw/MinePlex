package me.chiss.Core.PvpShop;

import org.bukkit.Material;

public interface IShopItem
{
    Material GetType();
    byte GetData();
    int GetAmount();
    int GetTokenCost();
    int GetPointCost();
    int GetCreditCost();
    int GetEconomyCost();
    boolean IsFree();
    int GetSalesPackageId();
    String GetName();
	String[] GetDesc();
	int GetSlot();
	String GetDeliveryName();
	float GetReturnPercent();
}
