package mineplex.core.shop.page;

import mineplex.core.MiniPlugin;
import mineplex.core.server.util.TransactionResponse;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ISalesPackage;
import mineplex.core.shop.item.ItemPackage;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ConfirmationPage<PluginType extends MiniPlugin, ShopType extends ShopBase<PluginType>> extends ShopPageBase<PluginType, ShopType> implements Runnable
{
	private Runnable _runnable;
	private ShopPageBase<PluginType, ShopType> _returnPage;
	private SalesPackageBase _salesItem;
	private int _okSquareSlotStart;
	private boolean _processing;
	private int _progressCount;
	private ShopItem _progressItem;
	private int _taskId;
	
	public ConfirmationPage(PluginType plugin, ShopType shop, CoreClientManager clientManager, mineplex.core.donation.DonationManager donationManager, Runnable runnable, ShopPageBase<PluginType, ShopType> returnPage, SalesPackageBase salesItem, CurrencyType currencyType, Player player) 
	{
		super(plugin, shop, clientManager, donationManager, "            Confirmation", player);
		
		_runnable = runnable;
		_returnPage = returnPage;
		_salesItem = salesItem;
		SelectedCurrency = currencyType;
		_progressItem = new ShopItem(Material.LAPIS_BLOCK, (byte)11, ChatColor.BLUE + "Processing", null, 1, false, true);
		_okSquareSlotStart = 27;
		
		if (Shop.CanPlayerAttemptPurchase(player))
		{
			BuildPage();
		}
		else
		{
			BuildErrorPage(new String[] { ChatColor.RED + "You have attempted too many invalid transactions.", ChatColor.RED + "Please wait 10 seconds before retrying." });
			_taskId = plugin.GetScheduler().scheduleSyncRepeatingTask(plugin.GetPlugin(), this, 2L, 2L);
		}
	}
	
	protected void BuildPage()
	{
		this.getInventory().setItem(22, new ShopItem(_salesItem.GetDisplayMaterial(), (byte)0, _salesItem.GetName(), _salesItem.GetDescription(), 1, false, true).getHandle());

		IButton okClicked = new IButton()
		{
			@Override
			public void Clicked(Player player)
			{
				OkClicked(player);
			}
		};
		
		IButton cancelClicked = new IButton()
		{
			@Override
			public void Clicked(Player player)
			{
				CancelClicked(player);
			}
		};
		
		BuildSquareAt(_okSquareSlotStart, new ShopItem(Material.EMERALD_BLOCK, (byte)0, ChatColor.GREEN + "OK", null, 1, false, true), okClicked);
		BuildSquareAt(_okSquareSlotStart + 6, new ShopItem(Material.REDSTONE_BLOCK, (byte)0, ChatColor.RED + "CANCEL", null, 1, false, true), cancelClicked);
		
		this.getInventory().setItem(4, new ShopItem(SelectedCurrency.GetDisplayMaterial(), (byte)0, SelectedCurrency.toString(), new String[] { C.cGray + _salesItem.GetCost(SelectedCurrency) + " " + SelectedCurrency.toString() + " will be deducted from your account balance." }, 1, false, true).getHandle());
	}
	
	protected void OkClicked(Player player)
	{
		ProcessTransaction();
	}
	
	protected void CancelClicked(Player player)
	{
		if (_returnPage != null)
			Shop.OpenPageForPlayer(player, _returnPage);
		else
			player.closeInventory();
		
		Plugin.GetScheduler().cancelTask(_taskId);
	}

	private void BuildSquareAt(int slot, ShopItem item, IButton button)
	{
		BuildSquareAt(slot, item, new ItemPackage(item, 0, false, -1), button);
	}
	
	private void BuildSquareAt(int slot, ShopItem item, ISalesPackage middleItem, IButton button)
	{
		AddButton(slot, item, button);
		AddButton(slot + 1, item, button);
		AddButton(slot + 2, item, button);
		
		slot += 9;
		
		AddButton(slot, item, button);
		AddButton(slot + 1, item, button);
		AddButton(slot + 2, item, button);
		
		slot += 9;
		
		AddButton(slot, item, button);
		AddButton(slot + 1, item, button);
		AddButton(slot + 2, item, button);
	}
	
	private void ProcessTransaction()
	{
		for (int i=_okSquareSlotStart; i < 54; i++)
		{
			clear(i);
		}
		
		_processing = true;
		
		if (_salesItem.IsKnown())
		{
			DonationManager.PurchaseKnownSalesPackage(new Callback<TransactionResponse> ()
			{
				public void run(TransactionResponse response)
				{
					ShowResultsPage(response);
				}
			}, Player.getName(), _salesItem.GetSalesPackageId());
		}
		else
		{
			DonationManager.PurchaseUnknownSalesPackage(new Callback<TransactionResponse> ()
			{
				public void run(TransactionResponse response)
				{
					ShowResultsPage(response);
				}
			}, Player.getName(), _salesItem.GetName(), _salesItem.GetCost(SelectedCurrency), _salesItem.OneTimePurchase());
		}
		
		_taskId = Plugin.GetScheduler().scheduleSyncRepeatingTask(Plugin.GetPlugin(), this, 2L, 2L);
	}

	private void ShowResultsPage(TransactionResponse response)
	{
		_processing = false;
		
		switch (response)
		{
			case Failed:
				BuildErrorPage(ChatColor.RED + "There was an error processing your request.");
				Shop.AddPlayerProcessError(Player);
				break;
			case AlreadyOwns:
				BuildErrorPage(ChatColor.RED + "You already own this package.");
				Shop.AddPlayerProcessError(Player);
				break;
			case InsufficientFunds:
				BuildErrorPage(ChatColor.RED + "Your account has insufficient funds.");
				Shop.AddPlayerProcessError(Player);
				break;
			case Success:
				_salesItem.Sold(Player, SelectedCurrency);
				
				BuildSuccessPage("Your purchase was successful.");
				
				if (_runnable != null)
					_runnable.run();
				
				break;
			default:
				break;
		}
		
		_progressCount = 0;
	}

	private void BuildErrorPage(String...message)
	{
		IButton returnButton = new IButton()
		{
			@Override
			public void Clicked(Player player)
			{
				CancelClicked(player);
			}
		};
		
		ShopItem item = new ShopItem(Material.REDSTONE_BLOCK, (byte)0, ChatColor.RED + "" + ChatColor.UNDERLINE + "ERROR", message, 1, false, true);
		for (int i = 0; i < this.getSize(); i++)
		{
			AddButton(i, item, returnButton);
		}
		
		Player.playSound(Player.getLocation(), Sound.BLAZE_DEATH, 1, .1f);
	}
	
	private void BuildSuccessPage(String message)
	{
		IButton returnButton = new IButton()
		{
			@Override
			public void Clicked(Player player)
			{
				CancelClicked(player);
			}
		};
		
		ShopItem item = new ShopItem(Material.EMERALD_BLOCK, (byte)0, ChatColor.GREEN + message, null, 1, false, true);
		for (int i = 0; i < this.getSize(); i++)
		{
			AddButton(i, item, returnButton);
		}
		
		Player.playSound(Player.getLocation(), Sound.NOTE_PLING, 1, .9f);
	}
	
	@Override
	public void PlayerClosed()
	{
		super.PlayerClosed();
		
		Plugin.GetScheduler().cancelTask(_taskId);
		
		if (_returnPage != null)
			Shop.SetCurrentPageForPlayer(Player, _returnPage);
	}
	
	@Override
	public void run() 
	{
		if (_processing)
		{
			if (_progressCount == 9)
			{
				for (int i=45; i < 54; i++)
				{
					clear(i);
				}
				
				_progressCount = 0;
			}
			
			setItem(45 + _progressCount, _progressItem);
		}
		else
		{
			if (_progressCount >= 20)
			{
				if (_returnPage != null)
					Shop.OpenPageForPlayer(Player, _returnPage);
				else
				{
					Player.closeInventory();
				}
				
				Plugin.GetScheduler().cancelTask(_taskId);
			}
		}
		
		_progressCount++;
	}
}
