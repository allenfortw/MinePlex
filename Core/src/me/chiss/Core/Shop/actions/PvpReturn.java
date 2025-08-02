package me.chiss.Core.Shop.actions;

import me.chiss.Core.PvpShop.IPvpShopFactory;
import me.chiss.Core.PvpShop.IShopItem;
import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpReturn extends ShopActionBase
{
	private IPvpShopFactory _itemFactory;

	public PvpReturn(JavaPlugin plugin, Shop shop, CoreClientManager clientManager, IPvpShopFactory itemFactory)
	{
		super(plugin, shop, clientManager);
		_itemFactory = itemFactory;
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent event)
	{	
		if (!Shop.ClickedCurrentPage(event))
			return;

		if (!event.isRightClick())
			return;

		if (event.isShiftClick())
			return;

		if (event.getRawSlot() <= 0)
			return;

		if (event.getCurrentItem().getType() == Material.AIR)
			return;

		if (event.getRawSlot() >= 54 && event.getRawSlot() <= 89)
			SellStack(event);

		if (event.getRawSlot() >= 9 && event.getRawSlot() <= 53)
			SellAll(event);
	}

	public void SellAll(InventoryClickEvent event)
	{
		event.setCancelled(true);

		CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
		ItemStack playerItem = event.getCurrentItem();

		if (playerItem == null || playerItem.getType() == Material.AIR)
			return;

		boolean durable = (playerItem.getType().getMaxDurability() > 0);

		IShopItem sellItem = null;

		for(IShopItem item : _itemFactory.GetItems())
		{
			if (item.GetType() != playerItem.getType())	
				continue;

			//Compare Data for Non-Durable Only
			if (!durable)
				if (playerItem.getData() != null)
					if (item.GetData() != playerItem.getData().getData())
						continue;

			sellItem = item;
			break;
		}

		if (sellItem == null)
			return;

		byte data = sellItem.GetData();
		if (durable) 
			data = -1;

		double cost = (double)sellItem.GetEconomyCost() / (double)sellItem.GetAmount() * sellItem.GetReturnPercent(); 
		int count = UtilInv.removeAll((Player)event.getWhoClicked(), sellItem.GetType(), data);

		//Set Balance
		player.Game().SetEconomyBalance(player.Game().GetEconomyBalance() + (int)(count * cost));

		//Update Balance
		Shop.GetPage(player).UpdateBalance(player);

		//Effect
		player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.PISTON_RETRACT, 0.5f, 2f);

		//Inform
		UtilPlayer.message(event.getWhoClicked(), F.main("Shop", "You sold " + 
				F.item(count + " " + sellItem.GetName()) + " for " + F.count((int)(count * cost) + " Coins") + "."));
	}

	public void SellStack(InventoryClickEvent event)
	{
		CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
		ItemStack playerItem = event.getCurrentItem();

		if (playerItem == null || playerItem.getType() == Material.AIR)
			return;

		for(IShopItem item : _itemFactory.GetItems())
		{
			if (item.GetType() != playerItem.getType())	
				continue;

			//Compare Data for Durable Only
			if (playerItem.getType().getMaxDurability() == 0)
				if (playerItem.getData() != null)
					if (item.GetData() != playerItem.getData().getData())
						continue;

			double cost = (double)item.GetEconomyCost() / (double)item.GetAmount() * item.GetReturnPercent();
			int count = playerItem.getAmount();

			//Set Balance
			player.Game().SetEconomyBalance(player.Game().GetEconomyBalance() + (int)(count * cost));

			//Remove Item
			event.setCurrentItem(null);

			//Update Balance
			Shop.GetPage(player).UpdateBalance(player);

			//Effect
			player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.PISTON_RETRACT, 0.5f, 2f);

			//Inform
			UtilPlayer.message(event.getWhoClicked(), F.main("Shop", "You sold " + 
					F.item(count + " " + item.GetName()) + " for " + F.count((int)(count * cost) + " Coins") + "."));

			event.setCancelled(true);
			break;
		}
	}
}
