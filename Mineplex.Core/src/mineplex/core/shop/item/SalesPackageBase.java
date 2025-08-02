package mineplex.core.shop.item;


import org.bukkit.Material;
import org.bukkit.entity.Player;

import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.repository.GameSalesPackageToken;

public abstract class SalesPackageBase implements ICurrencyPackage, IDisplayPackage
{
	private Material _displayMaterial;
	private byte _displayData;
	
	private String _name;
	private String[] _description;
	
	protected int SalesPackageId;
	protected boolean Free;
	protected NautHashMap<CurrencyType, Integer> CurrencyCostMap;
	protected boolean KnownPackage = true;
	protected boolean OneTimePurchaseOnly = true;
	
	public SalesPackageBase(String name, Material material, String...description)
	{
		this(name, material, (byte)0, description);
	}
	
	public SalesPackageBase(String name, Material material, byte displayData, String...description)
	{
		CurrencyCostMap = new NautHashMap<CurrencyType, Integer>();
		
		_name = name;
		_description = description;
		_displayMaterial = material;
		_displayData = displayData;
	}
	
	public abstract void Sold(Player player, CurrencyType currencyType);
	
	@Override
	public String GetName()
	{
		return _name;
	}
	
	@Override
	public String[] GetDescription()
	{
		return _description;
	}
	
	@Override
	public int GetCost(CurrencyType currencyType)
	{		
		return CurrencyCostMap.containsKey(currencyType) ? CurrencyCostMap.get(currencyType) : 0;
	}

	@Override
	public int GetSalesPackageId()
	{
		return SalesPackageId;
	}

	@Override
	public boolean IsFree()
	{
		return Free;
	}
	
	@Override
	public Material GetDisplayMaterial()
	{
		return _displayMaterial;
	}
	
	@Override
	public byte GetDisplayData()
	{
		return _displayData;
	}
	
	@Override
	public void Update(GameSalesPackageToken token)
	{
		SalesPackageId = token.GameSalesPackageId;
		Free = token.Free;
		
		if (token.Gems > 0)
		{
			CurrencyCostMap.put(CurrencyType.Gems, token.Gems);
		}
	}

	public boolean IsKnown()
	{
		return KnownPackage;
	}

	public boolean OneTimePurchase()
	{
		return OneTimePurchaseOnly;
	}
}
