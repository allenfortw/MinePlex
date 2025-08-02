package nautilus.game.lobby.ServerMenu;

import org.bukkit.entity.Player;

import me.chiss.Core.Shop.IButton;

public class ServerConnectButton implements IButton
{
	private ServerMenu _shop;
	private String _serverName;
	
	public ServerConnectButton(ServerMenu shop, String serverName)
	{
		_shop = shop;
		_serverName = serverName;
	}
	
	@Override
	public void Clicked(Player player)
	{
		_shop.SendPlayerToServer(player, _serverName);
	}
}
