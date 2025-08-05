package me.chiss.Core.Weapon;

import nautilus.minecraft.core.webserver.token.Server.WeaponToken;

import org.bukkit.Material;

public class Weapon implements IWeapon
{
    private int _salesPackageId;
    private String _customName;
    private Material _type;
    private int _amount;
    private int _gemCost;
    private int _economyCost;
    private boolean _free;
    private String[] _desc;
    
    public Weapon(String customName, String[] desc, Material type, int amount, int gemCost)
    {
        _customName = customName;
        _desc = desc;
        _type = type;
        _amount = amount;
        _gemCost = gemCost;
        _economyCost = 0;
    }
    
    @Override
    public Material GetType()
    {
        return _type;
    }

    @Override
    public int GetAmount()
    {
        return _amount;
    }

    @Override
    public int GetGemCost()
    {
        return _gemCost;
    }
    
    @Override
    public int GetEconomyCost()
    {
        return _economyCost;
    }

    @Override
    public int GetSalesPackageId()
    {
        return _salesPackageId;
    }

    @Override
    public String GetName()
    {
        return (_customName != null ? _customName :_type.name());
    }

    @Override
    public boolean IsFree()
    {
    	return _free;
    }
    
	@Override
	public void Update(WeaponToken weaponToken) 
	{
		_salesPackageId = weaponToken.SalesPackage.GameSalesPackageId;
		_gemCost = weaponToken.SalesPackage.Gems;
		_free = weaponToken.SalesPackage.Free;
	}
	
	@Override
	public String[] GetDesc() 
	{
		return _desc;
	}
}
