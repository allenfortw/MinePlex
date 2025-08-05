package me.chiss.Core.Modules;

import me.chiss.Core.ClientData.ClientPlayer;
import me.chiss.Core.Module.AModule;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfo extends AModule
{
	public PlayerInfo(JavaPlugin plugin) 
	{
		super("Player Update", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{

	}
	
	@Override
	public void commands() 
	{
		
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		
	}
	
	@EventHandler
	public void handleInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.PHYSICAL)
			return;
		
		Clients().Get(event.getPlayer()).Player().SetLastAction(System.currentTimeMillis());
	}
	
	@EventHandler
	public void handleJoin(PlayerJoinEvent event)
	{
		Clients().Get(event.getPlayer()).Acc().SetLoginLast(System.currentTimeMillis());
	}
}
