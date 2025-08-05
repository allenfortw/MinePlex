package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PreviousPage extends ShopActionBase
{
    public PreviousPage(JavaPlugin plugin, Shop shop, CoreClientManager clientManager)
    {
        super(plugin, shop, clientManager);
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent event)
    {
        CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
        
        if (Shop.ClickedCurrentPage(event) && event.getRawSlot() == 0 && Shop.HasPreviousPage(player))
        {
            Shop.TurnToPreviousPage(player);
            
            event.setCancelled(true);
        }
    }
}
