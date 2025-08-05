package mineplex.core.cosmetic.ui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import mineplex.core.cosmetic.ui.page.MountPage;
import mineplex.core.mount.Mount;
import mineplex.core.shop.item.IButton;

public class MountButton implements IButton
{
	private MountPage _page;
	
	public MountButton(Mount<?> mount, MountPage page)
	{
		_page = page;
	}

	@Override
	public void onClick(final Player player, ClickType clickType)
	{
		_page.getShop().openPageForPlayer(player, _page);
	}
}
