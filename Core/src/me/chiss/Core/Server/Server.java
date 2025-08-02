package me.chiss.Core.Server;

import me.chiss.Core.Events.ServerSaveEvent;
import me.chiss.Core.Server.command.ListCommand;
import me.chiss.Core.Server.command.SpawnSetCommand;
import me.chiss.Core.Server.command.WaterSpreadCommand;
import mineplex.core.MiniPlugin;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.account.CoreClientManager;
import mineplex.core.chat.command.BroadcastCommand;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Server extends MiniPlugin
{
	private CoreClientManager _clientManager;
	public boolean stopWeather = true;
	public boolean liquidSpread = true;

	public Server(JavaPlugin plugin, CoreClientManager clientManager) 
	{
		super("Server", plugin);
		
		_clientManager = clientManager;
	}

	@Override
	public void AddCommands() 
	{
		AddCommand(new BroadcastCommand(this));
		AddCommand(new ListCommand(this));
		AddCommand(new SpawnSetCommand(this));
		AddCommand(new WaterSpreadCommand(this));
	}

	@EventHandler
	public void WorldTimeWeather(UpdateEvent event)
	{
		if (event.getType() == UpdateType.TICK)
		{
			for (World cur : UtilServer.getServer().getWorlds())
			{
				if (cur.getTime() > 12000 && cur.getTime() < 24000)
					cur.setTime(cur.getTime() + 5);

				if (cur.getTime() > 14000 && cur.getTime() < 22000)
					cur.setTime(22000); 

				if (stopWeather)
				{
					cur.setStorm(false);
					cur.setThundering(false);
				}		
			}
		}

		if (event.getType() == UpdateType.MIN_04)
		{
			ServerSaveEvent saveEvent = new ServerSaveEvent();
			_plugin.getServer().getPluginManager().callEvent(saveEvent);

			if (!saveEvent.isCancelled())
			{
				saveClients();
				saveWorlds();
			}
		}
	}

	@EventHandler
	public void WaterSpread(BlockFromToEvent event)
	{
		if (!liquidSpread)
			event.setCancelled(true);
	}
	
	public void saveClients()
	{
		long epoch = System.currentTimeMillis();

		//Save Clients
		for (Player cur : UtilServer.getPlayers())
			cur.saveData();

		Log("Saved Clients to Disk. Took " + (System.currentTimeMillis()-epoch) + " milliseconds.");
	}

	public void saveWorlds()
	{
		long epoch = System.currentTimeMillis();

		//Save World
		for (World cur : UtilServer.getServer().getWorlds())
			cur.save();

		UtilServer.broadcast(C.cGray + "Saved Worlds [" + 
				F.time(UtilTime.convertString((System.currentTimeMillis()-epoch), 1, TimeUnit.FIT)) +
				"].");

		Log("Saved Worlds to Disk. Took " + (System.currentTimeMillis()-epoch) + " milliseconds.");
	}

	public void reload()
	{
		UtilServer.broadcast(F.main(_moduleName, "Reloading Plugins..."));
		Log("Reloading Plugins...");
		UtilServer.getServer().dispatchCommand(UtilServer.getServer().getConsoleSender(), "reload");
	}

	public void restart()
	{
		UtilServer.broadcast(F.main(_moduleName, "Restarting Server..."));
		Log("Restarting Server...");

		for (Player cur : UtilServer.getPlayers())
			UtilPlayer.kick(cur, _moduleName, "Server Restarting");

		UtilServer.getServer().dispatchCommand(UtilServer.getServer().getConsoleSender(), "stop");
	}

	@EventHandler
	public void handleCommand(PlayerCommandPreprocessEvent event) 
	{
		String cmdName = event.getMessage().split("\\s+")[0].substring(1);

		if (cmdName.equalsIgnoreCase("reload") || cmdName.equalsIgnoreCase("rl"))
		{
			event.setCancelled(true);

			if (_clientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), Rank.ADMIN, true))
				reload();
		}
		else if (cmdName.equalsIgnoreCase("stop"))
		{
			event.setCancelled(true);

			if (_clientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), Rank.ADMIN, true))
				restart();
		}	
		else if (cmdName.equalsIgnoreCase("reload"))
		{
			if (event.getPlayer().getName().equals("Chiss"))
				return;

			event.getPlayer().sendMessage("Plugins cannot be reloaded.");

			event.setCancelled(true);
		}
		else if (cmdName.equalsIgnoreCase("me"))
		{
			event.setCancelled(true);
		}
	}

	public CoreClientManager GetClientManager()
	{
		return _clientManager;
	}

	public void ToggleLiquidSpread()
	{
		liquidSpread = !liquidSpread;
	}
	
	public boolean GetLiquidSpread()
	{
		return liquidSpread;
	}
}
