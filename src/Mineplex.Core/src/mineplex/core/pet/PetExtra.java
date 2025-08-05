package mineplex.core.pet;

import mineplex.core.common.CurrencyType;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.pet.repository.token.PetExtraToken;
import mineplex.core.shop.item.SalesPackageBase;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetExtra extends SalesPackageBase
{
	private String _name;
	private Material _material;
	
	public PetExtra(String name, Material material, int cost)
	{
		super(name, material, (byte)0, new String[] { ChatColor.RESET + "" + ChatColor.GRAY + "Right-click pet to apply nametag" });
		
		_name = name;
		_material = material;
		CurrencyCostMap.put(CurrencyType.Gems, cost);
		
		KnownPackage = false;
		OneTimePurchaseOnly = false;
	}
	
	public void Update(PetExtraToken token)
	{

	}

	public String GetName()
	{
		return _name;
	}

	public Material GetMaterial()
	{
		return _material;
	}

	@Override
	public void Sold(Player player, CurrencyType currencyType)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.NAME_TAG, (byte)0, 1, GetName(), GetDescription()));
	}
}
