package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Reset extends ShopActionBase
{
    public Reset(JavaPlugin plugin, Shop shop, CoreClientManager clientManager)
    {
        super(plugin, shop, clientManager);
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent event)
    {
        if (Shop.ClickedCurrentPage(event) && event.isRightClick() && event.getRawSlot() == 4)
        {
            CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
            
            Shop.GetPage(player).Reset(player);
            Shop.GetPage(player).UpdateBalance(player);
            
            event.setCancelled(true);
        }
    }
}
