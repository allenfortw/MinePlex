package nautilus.game.lobby.gamequeue.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Shopv2.page.ShopPageBase;
import mineplex.core.common.util.C;
import nautilus.game.lobby.gamequeue.Gamemode;
import nautilus.game.lobby.gamequeue.QueueManager;
import nautilus.game.lobby.gamequeue.GameQueue;

import org.bukkit.entity.Player;

public class QueuePage extends ShopPageBase<QueueManager, QueueMenu>
{
	public QueuePage(QueueManager plugin, QueueMenu shop, Player player)
	{
		super(plugin, shop, "     Queue Menu", player);
		
		BuildPage();
	}

	@Override
	protected void BuildPage()
	{
		int slot = 0;
		boolean locked = false;
		
		for (Gamemode gameType : Plugin.GetGameTypes())
		{
			List<String> itemLore = new ArrayList<String>();
			
			itemLore.addAll(Arrays.asList(gameType.GetDescription()));
			
			for (GameQueue queueType : gameType.GetQueues())
			{
				itemLore.add(C.cBlack + "");
				itemLore.add(queueType.GetDescription());
				
				ShopItem shopItem = new ShopItem(gameType.GetDisplayMaterial(),
					gameType.GetDisplayData(), gameType.GetName(),
					itemLore.toArray(new String[itemLore.size()]), 1, locked,
					false);

				AddButton(slot, shopItem, new GameQueueButton(this, queueType));
				slot++;
			}
		}
	}
	
	public void SelectQueue(Player player, GameQueue queue)
	{
		if (!queue.ContainsPlayer(player))
		{
			Plugin.AddPlayerToQueue(player, queue);
			PlayAcceptSound(player);
		}
		else
		{
			PlayDenySound(player);
		}
	}
}
