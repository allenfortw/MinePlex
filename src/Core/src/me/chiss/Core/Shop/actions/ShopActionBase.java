package me.chiss.Core.Shop.actions;

import me.chiss.Core.Shop.Shop;
import mineplex.core.account.CoreClientManager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ShopActionBase implements Listener
{
    protected JavaPlugin Plugin;
    protected Shop Shop;
    protected CoreClientManager ClientManager;
    
    public ShopActionBase(JavaPlugin plugin, Shop shop, CoreClientManager clientManager)
    {
        Plugin = plugin;
        Shop = shop;
        ClientManager = clientManager;
        
        Plugin.getServer().getPluginManager().registerEvents(this,  Plugin);
    }
}
