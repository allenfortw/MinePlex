package mineplex.core.cosmetic.ui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.cosmetic.CosmeticManager;
import mineplex.core.cosmetic.ui.page.Menu;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;

public class CosmeticShop extends ShopBase<CosmeticManager> implements PluginMessageListener
{
	public CosmeticShop(CosmeticManager plugin, CoreClientManager clientManager, String name)
	{
		super(plugin, clientManager, name, CurrencyType.Gems, CurrencyType.Coins);
		
		plugin.getPlugin().getServer().getMessenger().registerIncomingPluginChannel(plugin.getPlugin(), "MC|ItemName", this);
	}

	@Override
	protected ShopPageBase<CosmeticManager, ? extends ShopBase<CosmeticManager>> buildPagesFor(Player player)
	{
		return new Menu(getPlugin(), this, getClientManager(), player);
	}

	@Override
	public void onPluginMessageReceived(String arg0, Player arg1, byte[] arg2)
	{
		// TODO Auto-generated method stub
		
	}
	
}
