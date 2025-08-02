package mineplex.core.shop.page;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ShopItem;

public abstract class ShopPageBase<PluginType extends MiniPlugin, ShopType extends ShopBase<PluginType>> extends CraftInventoryCustom implements Listener
{
	protected PluginType Plugin;
	protected CoreClientManager ClientManager;
	protected DonationManager DonationManager;
	protected ShopType Shop;
	protected Player Player;
	protected CoreClient Client;
	protected CurrencyType SelectedCurrency;
	protected NautHashMap<Integer, IButton> ButtonMap;
	protected boolean ShowCurrency = false;
	
	protected int CurrencySlot = 4;
	
	public ShopPageBase(PluginType plugin, ShopType shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player) 
	{
		this(plugin, shop, clientManager, donationManager, name, player, 54);
	}
	
	public ShopPageBase(PluginType plugin, ShopType shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player, int slots) 
	{
		super(null, slots, name);
		
		Plugin = plugin;
		ClientManager = clientManager;
		DonationManager = donationManager;
		Shop = shop;
		Player = player;
		ButtonMap = new NautHashMap<Integer, IButton>();
		
		Client = ClientManager.Get(player);
				
		if (shop.GetAvailableCurrencyTypes().size() > 0)
		{
			SelectedCurrency = shop.GetAvailableCurrencyTypes().get(0);
		}
	}

	protected void ChangeCurrency(Player player)
	{
		PlayAcceptSound(player);
    	
		int currentIndex = Shop.GetAvailableCurrencyTypes().indexOf(SelectedCurrency);
		
		if (currentIndex + 1 < Shop.GetAvailableCurrencyTypes().size())
		{
			SelectedCurrency = Shop.GetAvailableCurrencyTypes().get(currentIndex + 1);
		}
		else
		{
			SelectedCurrency = Shop.GetAvailableCurrencyTypes().get(0);
		}
	}

	protected abstract void BuildPage();
	
	protected void AddItem(int slot, ShopItem item)
	{
		if (slot > inventory.getSize() - 1)
		{
			// Magic slot conversion
			int playerSlot = slot >= (inventory.getSize() + 27) ? slot - (inventory.getSize() + 27) : slot - (inventory.getSize() - 9);
			Player.getInventory().setItem(playerSlot, item);
		}
		else
		{
			getInventory().setItem(slot, item.getHandle());
		}
	}
	
	protected void AddButton(int slot, ShopItem item, IButton button)
	{
		AddItem(slot, item);
		
		ButtonMap.put(slot, button);
	}
	
	protected void RemoveButton(int slot)
	{
		getInventory().setItem(slot, null);
		ButtonMap.remove(slot);
	}

	public void PlayerClicked(InventoryClickEvent event)
	{
		if (ButtonMap.containsKey(event.getRawSlot()))
		{
			ButtonMap.get(event.getRawSlot()).Clicked(Player);
		}
		else if (event.getRawSlot() != -999)
		{
			if (event.getInventory() == inventory && (inventory.getSize() <= event.getSlot() || inventory.getItem(event.getSlot()) != null))
			{
				PlayDenySound(Player);
			}
			else if (event.getInventory() == Player.getInventory() && Player.getInventory().getItem(event.getSlot()) != null)
			{
				PlayDenySound(Player);
			}
		}
	}
	
	public void PlayerOpened()
	{
		
	}

	public void PlayerClosed()
	{
		this.inventory.onClose((CraftPlayer)Player);
	}
	
	public void PlayAcceptSound(Player player)
	{
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, .6f);
	}

	public void PlayDenySound(Player player)
	{
		player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, .6f);
	}
	
	public void Dispose()
	{
		Player = null;
		Client = null;
		Shop = null;
		Plugin = null;
	}
}
