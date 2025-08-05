package me.chiss.Core.Modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import me.chiss.Core.Clans.ClansClan;
import me.chiss.Core.Clans.ClansUtility.ClanRelation;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Module.AModule;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Quit extends AModule
{
	//Config
	private int _logTime = 10;
	private long _rejoinTime = 60000;

	//Track Offline Players
	private HashMap<Player, QuitDataLog> _logMap = new HashMap<Player, QuitDataLog>();
	private HashMap<String, QuitDataQuit> _quitMap = new HashMap<String, QuitDataQuit>();
	private HashSet<String> _clearSet = new HashSet<String>();
	
	public Quit(JavaPlugin plugin) 
	{
		super("Quit", plugin);
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
		AddCommand("log");
		AddCommand("quit");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!checkItems(caller))
			return;

		if (!checkLand(caller))
			return;
		
		if (!checkWar(caller))
			return;

		//Set
		_logMap.put(caller, new QuitDataLog(_logTime, caller.getLocation()));

		//Inform
		UtilPlayer.message(caller, F.main(GetName(), "Logging out in " + 
				F.time(_logTime + " Seconds") + 
				"."));
	}

	@EventHandler
	public void Join(PlayerJoinEvent event) 
	{
		if (_clearSet.remove(event.getPlayer().getName()))
			UtilInv.Clear(event.getPlayer());
		
		QuitDataQuit quit = _quitMap.get(event.getPlayer().getName());

		//Set
		if (quit != null)
		{
			quit.SetOffline(false);
			Condition().Factory().Silence("Unsafe Log", event.getPlayer(), event.getPlayer(), 10, false, true);
			Condition().Factory().Weakness("Unsafe Log", event.getPlayer(), event.getPlayer(), 10, 3, false, true);
			Condition().Factory().Slow("Unsafe Log", event.getPlayer(), event.getPlayer(), 10, 3, false, true, false);
		}
			
		//Message
		if (quit != null)	event.setJoinMessage(F.sys("Join", event.getPlayer().getName() + C.sysHead + " (" + C.cGreen + "Recover" + C.sysHead + ")"));
		else				event.setJoinMessage(F.sys("Join", event.getPlayer().getName()));

		//Log
		System.out.println("Client Join [" + event.getPlayer().getName() + "] with Recovery [" + (quit != null) + "].");
	}

	public void AddQuit(Player player)
	{
		if (!_quitMap.containsKey(player.getName()))
		{
			_quitMap.put(player.getName(), new QuitDataQuit(player));
		}

		else 
		{
			if (_quitMap.get(player.getName()).SetOffline(true))
			{
				final Player punish = player;
				_plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
				{
					public void run()
					{
						Punish(punish.getName());
					}
				}, 0);
			}	 
		}	
	}

	@EventHandler
	public void Kick(PlayerKickEvent event) 
	{
		QuitDataLog data = _logMap.get(event.getPlayer());

		boolean safe = false;
		if (data != null)
			safe = data.GetLogTime() == 0;

		if (event.getPlayer().isDead())
			safe = true;
		
		if (!safe)
		{
			boolean clear = false;
			for (Player other : event.getPlayer().getWorld().getPlayers())
				if (UtilMath.offset(event.getPlayer(), other) < 32)
					if (Clans().CanHurt(event.getPlayer(), other))
						clear = true;
			
			if (!clear)
				safe = true;
		}
		
		int number = 1;
		if (_quitMap.containsKey(event.getPlayer().getName()))
			number = _quitMap.get(event.getPlayer().getName()).GetCount() + 1;

		//String
		String type = C.sysHead + " (" + C.cRed + "Unsafe " + number + "/3" + C.sysHead + ")";
		if (safe) type = C.sysHead + " (" + C.cGreen + "Safe" + C.sysHead + ")";
		
		//DONT DO STUFF
		
		//Message
		event.setLeaveMessage(F.sys("Quit", event.getPlayer().getName() + type));

		//Log
		System.out.println("Client Kick [" + event.getPlayer().getName() + "] with Safe [" + safe + "].");
	}


	@EventHandler
	public void Quits(PlayerQuitEvent event) 
	{ 
		Player player = event.getPlayer();
		
		QuitDataLog data = _logMap.remove(player);

		boolean safe = false;
		if (data != null)
			safe = data.GetLogTime() == 0;
		
		if (player.isDead())
			safe = true;
		
		if (!safe)
		{
			boolean clear = false;
			for (Player other : player.getWorld().getPlayers())
				if (UtilMath.offset(player, other) < 32)
					if (Clans().CanHurt(player, other))
						clear = true;
			
			if (!clear)
				safe = true;
		}

		//Combat Log
		if (!player.isDead() && 
			(!UtilTime.elapsed(Clients().Get(player).Player().GetLastDamagee(), 15000) || 
			!UtilTime.elapsed(Clients().Get(player).Player().GetLastDamager(), 15000)))
		{
			//String
			String type = C.sysHead + " (" + C.cRed + "Combat Log" + C.sysHead + ")";
			AddQuit(player);

			//Message
			event.setQuitMessage(F.sys("Quit", player.getName() + type));

			//Log
			System.out.println("Client Quit [" + player.getName() + "] with Safe [" + "Combat Log" + "].");
		}
		//Unsafe Log
		else
		{
			int number = 1;
			if (_quitMap.containsKey(player.getName()))
				number = _quitMap.get(player.getName()).GetCount() + 1;

			//String
			String type = C.sysHead + " (" + C.cRed + "Unsafe " + number + "/3" + C.sysHead + ")";
			if (safe) type = C.sysHead + " (" + C.cGreen + "Safe" + C.sysHead + ")";

			if (!safe)		AddQuit(player);
			else			_quitMap.remove(player.getName());

			//Message
			event.setQuitMessage(F.sys("Quit", player.getName() + type));

			//Log
			System.out.println("Client Quit [" + player.getName() + "] with Safe [" + safe + "].");
		}
	}

	@EventHandler
	public void Interact(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;

		//Inform
		if (_logMap.remove(event.getPlayer()) != null)
			UtilPlayer.message(event.getPlayer(), F.main(GetName(), "Log cancelled due to action."));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		//Take Damage
		if (!(event.GetDamageeEntity() instanceof Player))
			return;

		Player damagee = (Player)event.GetDamageeEntity();

		//Inform
		if (_logMap.remove(damagee) != null)
			UtilPlayer.message(damagee, F.main(GetName(), "Log cancelled due to damage."));
	}

	@EventHandler
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;

		if (_logMap.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() == UpdateType.FAST)
			updateQuit();

		if (event.getType() == UpdateType.TICK)
			updateLog();
	}

	public void updateLog()
	{
		for (Iterator<Player> i = _logMap.keySet().iterator(); i.hasNext();) 
		{
			Player cur = i.next();
			
			QuitDataLog log = _logMap.get(cur);

			if (!UtilTime.elapsed(log.GetLogLast(), 1000))
				continue;

			//Set
			log.SetLogTime(log.GetLogTime() - 1);
			log.SetLogLast(System.currentTimeMillis());

			//Safely Log
			if (log.GetLogTime() == 0)
			{
				UtilPlayer.kick(cur, GetName(), "Safely Logged Out", false);
				i.remove();
				continue;
			}

			//Other
			if (UtilMath.offset(log.GetLogLoc(), cur.getLocation()) > 0.5)
			{
				//Inform
				UtilPlayer.message(cur, F.main(GetName(), "Log cancelled due to movement."));
				i.remove();
			}
			else
			{
				//Inform
				UtilPlayer.message(cur, F.main(GetName(), "Logging out in " + 
						F.time(log.GetLogTime() + " Seconds") + 
						"."));
			}
		}
	}

	public void updateQuit()
	{
		HashSet<String> expired = new HashSet<String>();
		HashSet<String> punish = new HashSet<String>();

		for (String cur : _quitMap.keySet())
		{
			QuitDataQuit quit = _quitMap.get(cur);

			if (!UtilTime.elapsed(quit.GetQuitTime(), _rejoinTime))
				continue;

			//Online and Expired
			if (!quit.IsOffline())
			{
				expired.add(cur);
				continue;
			}

			//Done
			punish.add(cur);
		}

		for (String cur : punish)	
			Punish(cur);

		for (String cur : expired)	
		{
			_quitMap.remove(cur);
			System.out.println("Expired [" + cur + "] for staying online.");
		}		
	}

	public void Punish(String cur)
	{
		Player player = _quitMap.remove(cur).GetPlayer();
		
		//Inform
		for (Player other : UtilServer.getPlayers())
			UtilPlayer.message(other, F.main("Log", 
					F.name(player.getName()) + 
					C.cGray + " dropped inventory for unsafe logging."));

		//Log
		System.out.println("Punished [" + cur + "] for unsafe logging.");

		//Drop Inventory
		UtilInv.drop(player, true);
		
		//
		_clearSet.add(player.getName());
	}

	public boolean checkLand(Player caller)
	{
		if (Clans().CUtil().getAccess(caller, caller.getLocation()) == ClanRelation.NEUTRAL && 
				!Clans().CUtil().isAdmin(caller.getLocation()))
		{
			UtilPlayer.message(caller, F.main(GetName(), "You cannot log in enemy territory."));
			return false;
		}

		return true;
	}
	
	public boolean checkWar(Player caller)
	{
		
		ClansClan clan = Clans().CUtil().getClanByPlayer(caller);
		
		if (clan == null)
			return true;
		
		if (clan.GetEnemyEvent().isEmpty())
			return true;
		
		UtilPlayer.message(caller, F.main(GetName(), "You cannot log during invasion event."));
		return false;
	}

	public boolean checkItems(Player player)
	{
		return (new QuitInventory(this)).Check(player);
	}
}
