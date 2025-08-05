package nautilus.game.arcade.shop;

import org.bukkit.entity.Player;

import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import nautilus.game.arcade.kit.Kit;

public class KitPackage extends SalesPackageBase
{
	public KitPackage(String gameName, Kit kit)
	{
		super(gameName + " " + kit.GetName(), kit.getDisplayMaterial(), kit.GetDesc());
		KnownPackage = false;
		CurrencyCostMap.put(CurrencyType.Gems, kit.GetCost());
	}

	@Override
	public void Sold(Player player, CurrencyType currencyType)
	{

	}
}
