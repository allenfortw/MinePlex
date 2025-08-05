package me.chiss.Core.Weapon;

import nautilus.minecraft.core.webserver.token.Server.WeaponToken;

import org.bukkit.Material;

public interface IWeapon
{
    Material GetType();
    int GetAmount();
    int GetGemCost();
    int GetEconomyCost();
    boolean IsFree();
    int GetSalesPackageId();
    String GetName();
	void Update(WeaponToken weaponToken);
	String[] GetDesc();
}
