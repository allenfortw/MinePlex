package nautilus.game.lobby.gamequeue.menu;

import nautilus.game.lobby.gamequeue.GameQueue;

import org.bukkit.entity.Player;

import me.chiss.Core.Shop.IButton;

public class GameQueueButton implements IButton
{
	private QueuePage _queuePage;
	private GameQueue _queue;
	
	public GameQueueButton(QueuePage queuePage, GameQueue queue)
	{
		_queuePage = queuePage;
		_queue = queue;
	}

	@Override
	public void Clicked(Player player)
	{
		_queuePage.SelectQueue(player, _queue);
	}
}
