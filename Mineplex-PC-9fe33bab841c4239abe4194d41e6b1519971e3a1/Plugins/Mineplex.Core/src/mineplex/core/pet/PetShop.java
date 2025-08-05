package mineplex.core.pet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.donation.DonationManager;
import mineplex.core.pet.ui.PetPage;
import mineplex.core.pet.ui.PetTagPage;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;

public class PetShop extends ShopBase<PetManager> implements PluginMessageListener
{
    public PetShop(PetManager plugin, CoreClientManager manager, DonationManager donationManager) 
    {
        super(plugin, manager, donationManager, "Pet Shop", CurrencyType.Gems);
        
        plugin.GetPlugin().getServer().getMessenger().registerIncomingPluginChannel(plugin.GetPlugin(), "MC|ItemName", this);
    }

	@Override
	protected ShopPageBase<PetManager, ? extends ShopBase<PetManager>> BuildPagesFor(Player player)
	{
		return new PetPage(Plugin, this, ClientManager, DonationManager, "     Pets", player);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
        if (!channel.equalsIgnoreCase("MC|ItemName"))
            return;
        
        if (PlayerPageMap.containsKey(player.getName()) && PlayerPageMap.get(player.getName()) instanceof PetTagPage)
        {
	        if (message != null && message.length >= 1)
	        {
	            String tagName = new String(message);
	            
	            ((PetTagPage)PlayerPageMap.get(player.getName())).SetTagName(tagName);
	        }
	    }
	}
}