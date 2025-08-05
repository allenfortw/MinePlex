package nautilus.game.arcade.gui.privateServer.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import mineplex.core.shop.item.IButton;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.managers.GameHostManager;

public class KillButton implements IButton
{
	private GameHostManager _manager;

	public KillButton(ArcadeManager arcadeManager)
	{
		_manager = arcadeManager.GetGameHostManager();
	}

	@Override
	public void onClick(Player player, ClickType clickType)
	{
		if (clickType == ClickType.SHIFT_RIGHT)
		{
			_manager.setHostExpired(true, "The host has closed this Mineplex Private Server.");
		}
	}
}
