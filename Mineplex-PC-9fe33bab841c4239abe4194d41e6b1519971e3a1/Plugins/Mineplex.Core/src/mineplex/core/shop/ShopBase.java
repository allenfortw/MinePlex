package mineplex.core.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.ChatColor;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.page.ShopPageBase;

public abstract class ShopBase<PluginType extends MiniPlugin> implements Listener
{
	private NautHashMap<String, Long> _errorThrottling;
	private NautHashMap<String, Long> _purchaseBlock;
	
	private List<CurrencyType> _availableCurrencyTypes;
	
	protected PluginType Plugin;
	protected CoreClientManager ClientManager;
	protected DonationManager DonationManager;
	protected String Name;
	protected NautHashMap<String, ShopPageBase<PluginType, ? extends ShopBase<PluginType>>> PlayerPageMap;
	
	private HashSet<String> _openedShop = new HashSet<String>();
	
	public ShopBase(PluginType plugin, CoreClientManager clientManager, DonationManager donationManager, String name, CurrencyType...currencyTypes) 
	{		
		Plugin = plugin;
		ClientManager = clientManager;
		DonationManager = donationManager;
		Name = name;
		
		PlayerPageMap = new NautHashMap<String, ShopPageBase<PluginType, ? extends ShopBase<PluginType>>>();
		_errorThrottling = new NautHashMap<String, Long>();
		_purchaseBlock = new NautHashMap<String, Long>();
		
		_availableCurrencyTypes = new ArrayList<CurrencyType>();
		_availableCurrencyTypes.addAll(Arrays.asList(currencyTypes));
		
		Plugin.RegisterEvents(this);
	}
	
	public List<CurrencyType> GetAvailableCurrencyTypes()
	{
		return _availableCurrencyTypes;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void OnPlayerDamageEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof LivingEntity)
		{
			if (event.getDamager() instanceof Player)
			{
	    		if (AttemptShopOpen((Player)event.getDamager(), (LivingEntity)event.getEntity()))
	    		{
	    			event.setCancelled(true);
	    		}	    			
			}
		}
	}
	
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
    	if (event.getRightClicked() instanceof LivingEntity)
    	{
    		if (AttemptShopOpen(event.getPlayer(), (LivingEntity)event.getRightClicked()))
    			event.setCancelled(true);
    	}
    }
    
    private boolean AttemptShopOpen(Player player, LivingEntity entity)
    {
		if (!_openedShop.contains(player.getName()) && entity.isCustomNameVisible() && entity.getCustomName() != null && ChatColor.stripColor(entity.getCustomName()).equalsIgnoreCase(ChatColor.stripColor(Name)))
		{

			_openedShop.add(player.getName());
			
    		OpenShopForPlayer(player);
    		if (!PlayerPageMap.containsKey(player.getName()))
    		{
    			PlayerPageMap.put(player.getName(), BuildPagesFor(player));
    		}
    		
    		OpenPageForPlayer(player, GetOpeningPageForPlayer(player));
    		
    		return true;
		}
		
		return false;
    }
	
	protected ShopPageBase<PluginType, ? extends ShopBase<PluginType>> GetOpeningPageForPlayer(Player player)
	{
		return PlayerPageMap.get(player.getName());
	}

	@EventHandler
	public void OnInventoryClick(InventoryClickEvent event)
	{
		if (PlayerPageMap.containsKey(event.getWhoClicked().getName()) && PlayerPageMap.get(event.getWhoClicked().getName()).getName().equalsIgnoreCase(event.getInventory().getName()))
		{
			PlayerPageMap.get(event.getWhoClicked().getName()).PlayerClicked(event);			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnInventoryClose(InventoryCloseEvent event)
	{
		if (PlayerPageMap.containsKey(event.getPlayer().getName()) && PlayerPageMap.get(event.getPlayer().getName()).getTitle().equalsIgnoreCase(event.getInventory().getTitle()))
		{
			PlayerPageMap.get(event.getPlayer().getName()).PlayerClosed();
			PlayerPageMap.get(event.getPlayer().getName()).Dispose();
			
			PlayerPageMap.remove(event.getPlayer().getName());
			
			CloseShopForPlayer((Player)event.getPlayer());
			
			_openedShop.remove(event.getPlayer().getName());
		}
	}
	
	protected void OpenShopForPlayer(Player player) { }
	
	protected void CloseShopForPlayer(Player player) { }

	public void ResetPlayer(Player player)
	{
		PlayerPageMap.remove(player.getName());
	}
	
	@EventHandler
	public void OnPlayerQuit(PlayerQuitEvent event)
	{
		if (PlayerPageMap.containsKey(event.getPlayer().getName()))
		{
			PlayerPageMap.get(event.getPlayer().getName()).PlayerClosed();
			PlayerPageMap.get(event.getPlayer().getName()).Dispose();
			
			event.getPlayer().closeInventory();
			CloseShopForPlayer(event.getPlayer());
			
			PlayerPageMap.remove(event.getPlayer().getName());
			
			_openedShop.remove(event.getPlayer().getName());
		}
	}

	public void OpenPageForPlayer(Player player, ShopPageBase<PluginType, ? extends ShopBase<PluginType>> page)
	{
		if (PlayerPageMap.containsKey(player.getName()))
		{
			PlayerPageMap.get(player.getName()).PlayerClosed();
		}
		
		SetCurrentPageForPlayer(player, page);
		
		player.closeInventory();
		
		player.openInventory(page);
	}
	
	public void SetCurrentPageForPlayer(Player player, ShopPageBase<PluginType, ? extends ShopBase<PluginType>> page)
	{
		PlayerPageMap.put(player.getName(), page);
	}
	
	public void AddPlayerProcessError(Player player) 
	{
		if (_errorThrottling.containsKey(player.getName()) && (System.currentTimeMillis() - _errorThrottling.get(player.getName()) <= 5000))
			_purchaseBlock.put(player.getName(), System.currentTimeMillis());

		_errorThrottling.put(player.getName(), System.currentTimeMillis());
	}

	public boolean CanPlayerAttemptPurchase(Player player)
	{
		return !_purchaseBlock.containsKey(player.getName()) || (System.currentTimeMillis() - _purchaseBlock.get(player.getName()) > 10000);
	}
	
	public NautHashMap<String, ShopPageBase<PluginType, ? extends ShopBase<PluginType>>> GetPageMap()
	{
		return PlayerPageMap;
	}
	
	protected abstract ShopPageBase<PluginType, ? extends ShopBase<PluginType>> BuildPagesFor(Player player);
}
