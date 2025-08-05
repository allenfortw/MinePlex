package nautilus.game.lobby.gamequeue.menu;

import org.bukkit.entity.Player;

import nautilus.game.lobby.gamequeue.QueueManager;
import me.chiss.Core.Shopv2.ShopBase;
import me.chiss.Core.Shopv2.page.ShopPageBase;

public class QueueMenu extends ShopBase<QueueManager>
{
	public QueueMenu(QueueManager plugin)
	{
		super(plugin, "Game Queue");
	}

	@Override
	protected ShopPageBase<QueueManager, ? extends ShopBase<QueueManager>> BuildPagesFor(Player player)
	{
		return new QueuePage(Plugin, this, player);
	}
}
