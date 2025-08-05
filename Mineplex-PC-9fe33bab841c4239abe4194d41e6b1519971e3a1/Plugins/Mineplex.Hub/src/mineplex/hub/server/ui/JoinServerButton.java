package mineplex.hub.server.ui;

import org.bukkit.entity.Player;

import mineplex.core.shop.item.IButton;

public class JoinServerButton implements IButton
{
	private ServerNpcPage _page;
	private String _serverName;
	
	public JoinServerButton(ServerNpcPage page, String serverName)
	{
		_page = page;
		_serverName = serverName;
	}
	
	@Override
	public void Clicked(Player player)
	{
		_page.SelectServer(player, _serverName);
	}
}
