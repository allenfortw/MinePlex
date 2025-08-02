package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NextPage extends ShopActionBase
{
    public NextPage(JavaPlugin plugin, Shop shop, CoreClientManager clientManager)
    {
        super(plugin, shop, clientManager);
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent event)
    {
        CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
        
        if (Shop.ClickedCurrentPage(event) && event.getRawSlot() == 8 && Shop.HasNextPage(player))
        {
            Shop.TurnToNextPage(player);
            
            event.setCancelled(true);
        }
    }
}
