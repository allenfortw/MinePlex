package nautilus.game.lobby.gamequeue;

import java.util.HashSet;

import org.bukkit.entity.Player;

public class GameQueue
{
	private Gamemode _gamemode;
	private QueueType _queueType;

	private HashSet<String> _players;
	
	public GameQueue(Gamemode gamemode, QueueType queueType)
	{
		_gamemode = gamemode;
		_queueType = queueType;
		
		_players = new HashSet<String>();
	}
	
	public Gamemode GetGamemode()
	{
		return _gamemode;
	}
	
	public QueueType GetQueueType()
	{
		return _queueType;
	}
	
	public String GetDescription()
	{
		return _queueType.GetDescription();
	}

	public boolean ContainsPlayer(Player player)
	{
		return _players.contains(player.getName().toLowerCase());
	}
}
