package me.chiss.Core.Shop;


import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.PvpShop.IPvpShopFactory;
import me.chiss.Core.Shop.actions.PvpReturn;
import me.chiss.Core.Shop.currency.EconomyHandler;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.pagebuilder.game.PvpShopPageBuilder;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.server.IRepository;

public class PvpItemShop extends Shop
{
	IPvpShopFactory ShopFactory;
	
	private String _npcKey;
	
	public PvpItemShop(IPlugin plugin, IRepository repository, CoreClientManager accountManager, IPvpShopFactory shopFactory, String name) 
	{
		super(plugin, repository, accountManager);

		ShopBlockType = null;
		ShopFactory = shopFactory;
		_npcKey = ChatColor.stripColor(name);
		
		ICurrencyHandler economyHandler = new EconomyHandler();

		AddPageBuilder(new PvpShopPageBuilder(this, name, shopFactory, economyHandler));

		CreateShopActions();
	}

    @EventHandler
    public void OnPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
    	if (event.getRightClicked() instanceof LivingEntity && ((LivingEntity)event.getRightClicked()).isCustomNameVisible() && ChatColor.stripColor(((LivingEntity)event.getRightClicked()).getCustomName()).equalsIgnoreCase(_npcKey))
    	{
    		OpenShopForPlayer(event.getPlayer());
    	}
    }
	
	@Override
	protected void CreateShopActions()
	{
		super.CreateShopActions();
		new PvpReturn(GetPlugin(), this, ClientManager, ShopFactory);
	}
	
	@Override
	public void ShowSkillHotBarForPlayer(CoreClient player)
    {
		
    }
}
