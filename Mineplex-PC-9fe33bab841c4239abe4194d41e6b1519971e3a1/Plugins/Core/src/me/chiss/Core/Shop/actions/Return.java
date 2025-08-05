package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Utility.InventoryUtil;
import me.chiss.Core.Weapon.IWeapon;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.minecraft.game.core.classcombat.item.IItem;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Return extends ShopActionBase
{
    private IWeaponFactory _weaponFactory;
    private IItemFactory _itemFactory;
    
    public Return(JavaPlugin plugin, Shop shop, CoreClientManager clientManager, IWeaponFactory weaponFactory, IItemFactory itemFactory)
    {
        super(plugin, shop, clientManager);
        _weaponFactory = weaponFactory;
        _itemFactory = itemFactory;
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent event)
    {
        if (Shop.ClickedCurrentPage(event) && event.isRightClick() && !event.isShiftClick() && event.getRawSlot() > 0)
        {
            if (event.getCurrentItem().getType() != Material.AIR)
            {
                if (event.getRawSlot() > 8 && event.getRawSlot() < 81)
                {
                    CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
                    int slot = event.getRawSlot();

                    Shop.GetPage(player).PlayerReturning(player, slot);
                    Shop.GetPage(player).UpdateBalance(player);
                    
                    event.setCancelled(true);
                }
                else if (event.getRawSlot() > 80 && event.getRawSlot() < 90)
                {
                    CoreClient player = ClientManager.Get((Player)event.getWhoClicked());
                    ItemStack playerItem = event.getCurrentItem();
                    boolean foundItem = false;
                    
                    for(IWeapon weapon : _weaponFactory.GetWeapons())
                    {
                        if (weapon.GetType() == playerItem.getType() && playerItem.getAmount() >= weapon.GetAmount() && playerItem.getDurability() == 0)
                        {
                        	ItemStack itemStack = new ShopItem(weapon.GetType(), weapon.GetName(), weapon.GetAmount(), false);
                        	int returnAmount = InventoryUtil.GetCountOfObjectsRemovedInSlot((CraftInventory)player.Class().GetInventory(), event.getSlot(), itemStack);
                        	int cost = weapon.GetTokenCost();
                        	
                        	player.Donor().ReturnItem(returnAmount * cost);
                            foundItem = true;
                            break;
                        }
                    }
                    
                    if (!foundItem)
                    {
                        for(IItem item : _itemFactory.GetItems())
                        {
                            if (item.GetType() == playerItem.getType() && playerItem.getAmount() >= item.GetAmount() && playerItem.getDurability() == 0)
                            {
                            	ItemStack itemStack = new ShopItem(item.GetType(), item.GetName(), item.GetAmount(), false);
                            	int returnAmount = InventoryUtil.GetCountOfObjectsRemovedInSlot((CraftInventory)player.Class().GetInventory(), event.getSlot(), itemStack);
                            	int cost = item.GetTokenCost();
                            	
                            	player.Donor().ReturnItem(returnAmount * cost);
                                foundItem = true;
                                break;
                            }
                        }
                    }
                    
                    if (foundItem)
                    {
                        Shop.GetPage(player).UpdateBalance(player);
                        
                        player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.SPIDER_WALK, 1, .6f);
                        
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
