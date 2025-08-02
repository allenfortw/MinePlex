package me.chiss.Core.Shop.page.game.purchase;

import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.CreditHandler;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.events.PurchasePackageEvent;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.server.util.TransactionResponse;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.minecraft.shop.item.ISalesPackage;
import mineplex.minecraft.shop.item.ItemPackage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryCustom;
import org.bukkit.inventory.ItemStack;

public class ConfirmationPage extends CraftInventoryCustom implements IShopPage, Runnable
{
	private Shop _shop;
	private Runnable _runnable;
	private IShopPage _returnCategory;
	private ISalesPackage _salesItem;
	private ICurrencyHandler _currencyHandler;
	private CoreClient _player;
	private String _title;
	private int _okSquareSlotStart;
	private boolean _processing;
	private boolean _receivedResult;
	private int _progressCount;
	private ShopItem _progressItem;
	private int _taskId;
	
	public ConfirmationPage(Shop shop, IShopPage returnCategory, ISalesPackage salesItem, ICurrencyHandler currencyHandler, CoreClient player) 
	{
		this(shop, null, returnCategory, salesItem, currencyHandler, player);
	}
	
	public ConfirmationPage(Shop shop, Runnable runnable, IShopPage returnCategory, ISalesPackage salesItem, ICurrencyHandler currencyHandler, CoreClient player) 
	{
		super(null, 54, "            Confirmation");
		
		_shop = shop;
		_runnable = runnable;
		_title = "            Confirmation";
		_returnCategory = returnCategory;
		_salesItem = salesItem;
		_currencyHandler = currencyHandler;
		_player = player;
		_progressItem = new ShopItem(Material.WOOL, (byte)11, ChatColor.BLUE + "Processing", null, 1, false, true);
		_okSquareSlotStart = 27;
		
		if (_shop.CanPlayerAttemptPurchase(player))
		{
			BuildPage();
		}
		else
		{
			BuildErrorPage("You have attempted too many invalid transactions.  Please wait 10 seconds before retrying.");
			_taskId = _shop.GetPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(_shop.GetPlugin(), this, 2L, 2L);
		}
	}
	
	private void BuildPage()
	{
		_salesItem.AddToCategory(this.getInventory(), 22);
		
		this.getInventory().setItem(4, new ShopItem(_currencyHandler.GetItemDisplayType(), (byte)0, _currencyHandler.GetName(), new String[] { C.cGray + _currencyHandler.GetCost(_salesItem) + " " + _currencyHandler.GetName() + " will be deducted from your account balance." }, 1, false, true).getHandle());
		
		BuildSquareAt(_okSquareSlotStart, new ShopItem(Material.WOOL, (byte)5, ChatColor.GREEN + "OK", null, 1, false, true));
		BuildSquareAt(_okSquareSlotStart + 6, new ShopItem(Material.WOOL, (byte)14, ChatColor.RED + "CANCEL", null, 1, false, true));
	}
	
	private void BuildSquareAt(int slot, ShopItem item)
	{
		BuildSquareAt(slot, item, new ItemPackage(item, 0, 0, 0, 0, false, -1));
	}
	
	private void BuildSquareAt(int slot, ShopItem item, ISalesPackage middleItem)
	{
		this.setItem(slot, item);
		this.setItem(slot + 1, item);
		this.setItem(slot + 2, item);
		
		slot += 9;
		
		this.setItem(slot, item);
		List<Integer> slotsAddedTo = middleItem.AddToCategory(this.getInventory(), slot + 1);
		this.setItem(slot + 2, item);
		
		boolean crossedItem = true;
		
		while (crossedItem)
		{
			crossedItem = false;
			
			slot += 9;
			
			if (!slotsAddedTo.contains(slot))
				this.setItem(slot, item);
			else
				crossedItem = true;
			
			if (!slotsAddedTo.contains(slot + 1))
				this.setItem(slot + 1, item);
			else
				crossedItem = true;
			
			if (!slotsAddedTo.contains(slot + 2))
				this.setItem(slot + 2, item);
			else
				crossedItem = true;
		}
	}

	@Override
	public ISalesPackage GetItem(int itemSlot) 
	{
		return null;
	}

	@Override
	public void PrepSlotsForPlayer(CoreClient clicker)
	{
		
	}
	
	@Override
	public void OpenForPlayer(CoreClient clicker) 
	{
		_shop.SetPage(clicker, this);
		
		clicker.Class().OpenInventory(this);
	}

	@Override
	public void CloseForPlayer(CoreClient clicker) 
	{
		clicker.Class().CloseInventory();
		_processing = false;
	}

	@Override
	public String GetTitle() 
	{
		return _title;
	}

	@Override
	public void Reset(CoreClient player) 
	{
	}

	@Override
	public boolean HasNextPage() 
	{
		return false;
	}

	@Override
	public boolean HasPreviousPage() 
	{
		return false;
	}

	@Override
	public void SetPreviousPage(IShopPage previousPage) 
	{

	}

	@Override
	public void SetNextPage(IShopPage nextPage) 
	{

	}

	@Override
	public IShopPage GetPreviousPage() 
	{
		return null;
	}

	@Override
	public IShopPage GetNextPage() 
	{
		return null;
	}

	@Override
	public void AddItem(ISalesPackage iSalesPackage, int slot) 
	{
	}

	@Override
	public void SetPageNumber(int pageNumber) 
	{
	}

	@Override
	public int GetPageNumber() 
	{
		return 0;
	}

	@Override
	public void UpdateBalance(CoreClient player) 
	{

	}

	@Override
	public void PlayerWants(CoreClient donor, int slot) 
	{
		ItemStack item = getItem(slot);
		if (item != null)
		{
			if (item.getType() == Material.WOOL)
			{
				if (_receivedResult)
				{
					_returnCategory.OpenForPlayer(donor);
					_shop.SetPage(donor, _returnCategory);
					_shop.GetPlugin().getServer().getScheduler().cancelTask(_taskId);
				}
				else
				{
					if (item.getData().getData() == 5)
					{
						ProcessTransaction();
					}
					else if (item.getData().getData() == 14)
					{
						_returnCategory.OpenForPlayer(donor);
						_shop.SetPage(donor, _returnCategory);
						_shop.GetPlugin().getServer().getScheduler().cancelTask(_taskId);
					}
				}
			}
		}
	}

	@Override
	public void PlayerReturning(CoreClient player, int slot) 
	{

	}

	@Override
	public void ResetVisuals() 
	{

	}

	@Override
	public void ChangeCurrency(CoreClient player) 
	{

	}

	@Override
	public void UpdateSlot(CoreClient player, int slot) 
	{

	}
	
	private void ProcessTransaction()
	{
		for (int i=_okSquareSlotStart; i < 54; i++)
		{
			clear(i);
		}
		
		_processing = true;
		
		PurchasePackageEvent event = new PurchasePackageEvent(_player.GetPlayerName(), _salesItem.GetName());
		
		_shop.GetPlugin().getServer().getPluginManager().callEvent(event);
		
		if (!event.isCancelled())
		{
			_shop.GetRepository().PurchaseSalesPackage(new Callback<TransactionResponse> ()
					{
						public void run(TransactionResponse response)
						{
							ShowResultsPage(response);
						}
					}, _player.GetPlayerName(), _currencyHandler instanceof CreditHandler, _salesItem.GetSalesPackageId());
			
			_taskId = _shop.GetPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(_shop.GetPlugin(), this, 2L, 2L);
		}
		else
		{
			BuildErrorPage(event.GetReason());
			_shop.AddPlayerProcessError(_player);
		}
	}

	private void ShowResultsPage(TransactionResponse response)
	{
		_processing = false;
		_receivedResult = true;
		
		switch (response)
		{
			case Failed:
				BuildErrorPage("There was an error processing your request.");
				_shop.AddPlayerProcessError(_player);
				break;
			case InsufficientFunds:
				BuildErrorPage("Your account has insufficient funds.");
				_shop.AddPlayerProcessError(_player);
				break;
			case Success:
				_salesItem.PurchaseBy(_player);
				_currencyHandler.Deduct(_player, _salesItem);
				
				BuildSuccessPage("Your purchase was successful.");
				
				if (_runnable != null)
					_runnable.run();
				
				break;
		}
		
		_progressCount = 0;
	}
	
	private void BuildErrorPage(String message)
	{
		ShopItem item = new ShopItem(Material.WOOL, (byte)14, ChatColor.RED + message, null, 1, false, true);
		for (int i = 0; i < this.getSize(); i++)
		{
			 this.setItem(i, item);
		}
		
		_player.GetPlayer().playSound(_player.GetPlayer().getLocation(), Sound.BLAZE_DEATH, 1, .1f);
	}
	
	private void BuildSuccessPage(String message)
	{
		ShopItem item = new ShopItem(Material.WOOL, (byte)5, ChatColor.GREEN + message, null, 1, false, true);
		for (int i = 0; i < this.getSize(); i++)
		{
			 this.setItem(i, item);
		}
		
		_player.GetPlayer().playSound(_player.GetPlayer().getLocation(), Sound.NOTE_PLING, 1, .9f);
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
				_shop.GetPlugin().getServer().getScheduler().cancelTask(_taskId);
				
				_shop.SetPage(_player, _returnCategory);
				_returnCategory.OpenForPlayer(_player);
			}
		}
		
		_progressCount++;
	}
}
