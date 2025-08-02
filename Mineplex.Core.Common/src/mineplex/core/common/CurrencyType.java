package mineplex.core.common;

import org.bukkit.Material;

public enum CurrencyType
{
	Tokens(" Tokens", Material.EMERALD),
	Coins(" Coins", Material.GOLD_INGOT), 
	Gems("Gems", Material.DIAMOND);
	
	private String _prefix;
	private Material _displayMaterial;
	
	CurrencyType(String prefix, Material displayMaterial)
	{
		_prefix = prefix;
		_displayMaterial = displayMaterial;
	}
	
	public String Prefix()
	{
		return _prefix;
	}
	
	public Material GetDisplayMaterial()
	{
		return _displayMaterial;
	}
}
