package mineplex.hub.server.ui;

import org.bukkit.entity.Player;

import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerManager;

public class ServerNpcShop extends ShopBase<ServerManager>
{	
	public ServerNpcShop(ServerManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
	{
		super(plugin, clientManager, donationManager, name);
	}

	@Override
	protected ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> BuildPagesFor(Player player)
	{
		return new ServerNpcPage(Plugin, this, ClientManager, DonationManager, Name, player, Name);
	}
	
	public void UpdatePages()
	{
		for (ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> page : PlayerPageMap.values())
		{
			if (page instanceof ServerNpcPage)
			{
				((ServerNpcPage)page).Update();
			}
		}
	}
}
