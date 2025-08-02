package nautilus.game.pvp.worldevent;

import java.util.HashSet;

import mineplex.core.Rank;
import me.chiss.Core.Module.AModule;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import nautilus.game.pvp.worldevent.events.BossSkeleton;
import nautilus.game.pvp.worldevent.events.BossSlime;
import nautilus.game.pvp.worldevent.events.BaseUndead;
import nautilus.game.pvp.worldevent.events.BossSpider;
import nautilus.game.pvp.worldevent.events.BossSwarmer;
import nautilus.game.pvp.worldevent.events.BossWither;
import nautilus.game.pvp.worldevent.events.EndFlood;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class EventManager extends AModule
{
	private HashSet<EventBase> _active = new HashSet<EventBase>();
	
	private EventTerrainFinder _terrainFinder;
	
	private long _lastStart = 0;
	private long _lastStop = 0;
	
	public EventManager(JavaPlugin plugin) 
	{
		super("Event Manager", plugin);
		
		_terrainFinder = new EventTerrainFinder(this);
		
		_lastStart = System.currentTimeMillis();
		_lastStop = System.currentTimeMillis();
	}

	@Override
	public void enable() 
	{
	
	}

	@Override
	public void disable() 
	{
		for (EventBase cur : _active)
			cur.TriggerStop();
	}

	@Override
	public void config() 
	{
		
	}

	@Override
	public void commands() 
	{
		AddCommand("ev");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;
		
		if (args.length == 0)
		{
			caller.sendMessage("Missing Event Parameter.");
			return;
		}
		
		EventBase event = null;
		
		if (args[0].equals("dead"))		event = new BaseUndead(this);
		if (args[0].equals("dead4"))	event = new BaseUndead(this, 4);
		if (args[0].equals("slime"))	event = new BossSlime(this);
		if (args[0].equals("skel"))		event = new BossSkeleton(this);	
		if (args[0].equals("swarm"))	event = new BossSwarmer(this);
		if (args[0].equals("wither"))	event = new BossWither(this);
		if (args[0].equals("brood"))	event = new BossSpider(this);
		
		if (args[0].equals("flood"))	event = new EndFlood(this, caller.getLocation());

		if (event != null)
		{
			event.TriggerStart();
			_active.add(event);
			
			UtilServer.getServer().getPluginManager().registerEvents(event, Plugin());
		}
	}
	
	@EventHandler
	public void StartEvent(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;
		
		if (!_active.isEmpty())
			return;
		
		if (System.currentTimeMillis() - _lastStart < 7200000)
			return;
		
		if (System.currentTimeMillis() - _lastStop < 7200000)
			return;
		
		StartEvent();
	}
	
	public void StartEvent()
	{
		double rand = Math.random();
		
		if (rand > 0.90)			(new BaseUndead(this)).TriggerStart();
		else if (rand > 0.72)		(new BossSlime(this)).TriggerStart();
		else if (rand > 0.54)		(new BossSkeleton(this)).TriggerStart();	
		else if (rand > 0.36)		(new BossSwarmer(this)).TriggerStart();	
		else if (rand > 0.18)		(new BossWither(this)).TriggerStart();	
		else						(new BossSpider(this)).TriggerStart();

	}

	public EventTerrainFinder TerrainFinder() 
	{
		return _terrainFinder;
	}

	public void RecordStart(EventBase event)
	{
		_active.add(event);
		
		//Register Events
		UtilServer.getServer().getPluginManager().registerEvents(event, Plugin());
		
		_lastStart = System.currentTimeMillis();
	}

	public void RecordStop(EventBase event) 
	{
		_active.remove(event);
		
		//Deregister Events
		HandlerList.unregisterAll(event);
		
		_lastStop = System.currentTimeMillis();
	}
}
