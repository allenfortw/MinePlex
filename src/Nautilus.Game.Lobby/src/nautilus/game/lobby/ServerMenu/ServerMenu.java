package nautilus.game.lobby.ServerMenu;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import me.chiss.Core.Portal.Portal;
import me.chiss.Core.Shopv2.ShopBase;
import me.chiss.Core.Shopv2.page.ShopPageBase;

public class ServerMenu extends ShopBase<ServerInfoManager>
{
	private Portal _portal;
	
	public ServerMenu(ServerInfoManager plugin, Portal portal)
	{
		super(plugin, "Server Menu [Right-Click Me]");
		
		_portal = portal;
	}

	@Override
	protected ShopPageBase<ServerInfoManager, ServerMenu> BuildPagesFor(Player player)
	{
		return new ServerMenuPage(Plugin, this, player);
	}
	
	public void SendPlayerToServer(Player player, String server)
	{
		_portal.SendPlayerToServer(player, server);
	}
	
	public void UpdatePages()
	{
		for (Entry<String, ShopPageBase<ServerInfoManager, ? extends ShopBase<ServerInfoManager>>> pageEntry : GetPageMap().entrySet())
		{
			((ServerMenuPage)pageEntry.getValue()).UpdateServerCounts();
		}
	}
}
