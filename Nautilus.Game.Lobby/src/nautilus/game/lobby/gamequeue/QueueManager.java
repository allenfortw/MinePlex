package nautilus.game.lobby.gamequeue;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.server.ServerBroadcaster;
import mineplex.core.server.packet.PlayerGameRequestPacket;

public class QueueManager extends MiniPlugin
{
	private ServerBroadcaster _serverTalker;
	private List<Gamemode> _gameTypes;
	
	public QueueManager(JavaPlugin plugin, String webServerAddress)
	{
		super("Game Queue", plugin);
		
		_gameTypes = new ArrayList<Gamemode>();
		
		Gamemode dominate = new Gamemode(Material.BAKED_POTATO, (byte)0, "Dominate!", "Capture control points to win!");
		dominate.AddGameQueue(QueueType.SOLO);
		
		_gameTypes.add(dominate);
		
		_serverTalker = new ServerBroadcaster(webServerAddress);
		_serverTalker.start();
	}

	public List<Gamemode> GetGameTypes()
	{
		return _gameTypes;
	}

	public void AddPlayerToQueue(Player player, GameQueue queue)
	{
		_serverTalker.QueuePacket(new PlayerGameRequestPacket(player.getName()));
	}
}