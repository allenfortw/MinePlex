package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Purchase extends ShopActionBase
{
    public Purchase(JavaPlugin plugin, Shop shop, CoreClientManager clientManager)
    {
        super(plugin, shop, clientManager);
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent event)
    {
        if (Shop.ClickedCurrentPage(event) && event.isLeftClick() && !event.isShiftClick() && event.getRawSlot() > 0)
        {
            if (event.getCurrentItem().getType() != Material.AIR)
            {
                if (event.getRawSlot() > 8 && event.getRawSlot() < 81)
                {
                    CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
                    int slot = event.getRawSlot();

                    Shop.GetPage(player).PlayerWants(player, slot);
                    
                    if (Shop.GetPage(player) != null)
                    	Shop.GetPage(player).UpdateBalance(player);
                    
                    event.setCancelled(true);
                }
            }
        }
    }
}
