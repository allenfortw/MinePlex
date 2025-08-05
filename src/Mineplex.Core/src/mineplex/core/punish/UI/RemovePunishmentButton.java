package mineplex.core.punish.UI;

import org.bukkit.entity.Player;

import mineplex.core.punish.Punishment;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ShopItem;

public class RemovePunishmentButton implements IButton
{
	private PunishPage _punishPage;
	private Punishment _punishment;
	private ShopItem _item;
	
	public RemovePunishmentButton(PunishPage punishPage, Punishment punishment, ShopItem item)
	{
		_punishPage = punishPage;
		_punishment = punishment;
		_item = item;
	}
	
	public void Clicked(Player player)
	{
		_punishPage.RemovePunishment(_punishment, _item);
	}
}
