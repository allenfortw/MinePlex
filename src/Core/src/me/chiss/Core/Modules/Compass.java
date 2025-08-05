package me.chiss.Core.Modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;

public class Compass extends AModule
{
	//Search Map
	private HashMap<Player, Entity> _searchMap = new HashMap<Player, Entity>();

	public Compass(JavaPlugin plugin) 
	{
		super("Compass", plugin);
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
		AddCommand("compass");
		AddCommand("find");
		AddCommand("q");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		//No Compass
		if (!caller.getInventory().contains(Material.COMPASS))
		{
			UtilPlayer.message(caller, F.main(_moduleName, "You do not have a Compass."));
			return;
		}

		//No Args
		if (args.length < 1)
		{
			UtilPlayer.message(caller, F.main(_moduleName, "Missing Entity Parameter."));
			return;
		}

		//Parse Input
		if (UtilEnt.searchName(null, args[0], true) != null)			
			findCreature(caller, UtilEnt.searchName(null, args[0], false));

		else if (args[0].equalsIgnoreCase("random"))
			findRandom(caller);

		else
			findPlayer(caller, args[0]);
	}

	public void findRandom(Player caller)
	{
		//Recharge
		if (!Recharge().use(caller, "Random Compass Search", 12000, true))
			return;

		//Find Players
		ArrayList<Player> findList = new ArrayList<Player>();
		for (Player cur : UtilServer.getPlayers())
		{
			if (caller.getWorld().getEnvironment() != cur.getWorld().getEnvironment())
				continue;

			if (Clans().CUtil().playerAlly(caller.getName(), cur.getName()))
				continue;

			findList.add(cur);
		}

		//
		if (findList.size() == 0)
		{
			UtilPlayer.message(caller, F.main(_moduleName, "You do not have a Compass."));
			return;
		}

		findPlayer(caller, findList.get(UtilMath.r(findList.size())).getName());
	}

	public void findCreature(Player caller, String name)
	{
		//Type
		EntityType type = UtilEnt.searchEntity(null, name, false);

		//Target Creature
		Entity bestTarget = null;
		double bestDist = 999999;

		//Search World
		for (Entity ent : caller.getWorld().getEntities())
			if (ent.getType() == type)
			{
				double newDist = closer(caller, ent, bestDist);
				if (newDist < bestDist)
				{
					bestTarget = ent;
					bestDist = newDist;
				}
			}

		//None Found
		if (bestTarget == null)
		{
			UtilPlayer.message(caller, F.main(_moduleName, "There are no " + F.elem(name) + " nearby."));
			setTarget(caller, caller);
			return;
		}

		//Inform
		UtilPlayer.message(caller, F.main(_moduleName, "The nearest " + F.elem(name) + " is at " + F.elem(UtilWorld.locToStrClean(bestTarget.getLocation())) + "."));

		//Set
		setTarget(caller, bestTarget);
	}


	public double closer(Player caller, Entity newEnt, double oldDist)
	{
		double newDist = caller.getLocation().toVector().subtract(newEnt.getLocation().toVector()).length();

		if (newDist < oldDist)
			return newDist;

		return 999999999;
	}

	public void findPlayer(Player caller, String name)
	{
		Player target = UtilPlayer.searchOnline(caller, name, true);

		if (target == null)
			return;

		if (caller.getLocation().getWorld().equals(target.getLocation().getWorld()))
		{
			//Inform
			UtilPlayer.message(caller, F.main(_moduleName, F.name(target.getName()) + " is in " + 
					F.elem(UtilWorld.envToStr(caller.getLocation().getWorld().getEnvironment())) + 
					" at " + F.elem(UtilWorld.locToStrClean(target.getLocation())) + "."));

			//Lock
			setTarget(caller, target);	
		}
		else
		{
			//Inform
			UtilPlayer.message(caller, F.main(_moduleName, F.name(target.getName()) + " was last seen in " + 
					F.elem(UtilWorld.envToStr(caller.getLocation().getWorld().getEnvironment())) + "."));

			//Lock
			setTarget(caller, caller);	
		}
	}

	public void setTarget(Player caller, Entity target)
	{
		_searchMap.put(caller, target);
	}

	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return; 
		
		for (Player cur : UtilServer.getPlayers())
			updateCompass(cur);

		updateTarget();
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		_searchMap.remove(event.getPlayer());
	}

	public void updateCompass(Player player)
	{	
		//No Compass
		if (!player.getInventory().contains(Material.COMPASS))
		{
			_searchMap.remove(player);
			return;
		}

		//Ensure
		if (!_searchMap.containsKey(player))
			setTarget(player,player);

		//No Target
		if (_searchMap.get(player).equals(player))
		{
			double x = player.getLocation().getX() + (Math.sin((System.currentTimeMillis()/800)%360d)*20);
			double y = player.getLocation().getY();
			double z = player.getLocation().getZ() + (Math.cos((System.currentTimeMillis()/800)%360d)*20);
			player.setCompassTarget(new Location(player.getWorld(), x, y, z));
			return;
		}

		//Lock
		Entity target = _searchMap.get(player);

		//Changed World
		if (!player.getLocation().getWorld().equals(target.getLocation().getWorld()))
		{
			UtilPlayer.message(player, F.main(_moduleName, "Target is no longer in your World."));
			setTarget(player,player);
			return;
		}

		//Lock
		player.setCompassTarget(target.getLocation());
	}	

	public void updateTarget()
	{
		HashSet<Player> toRemove = new HashSet<Player>();

		for (Player cur : _searchMap.keySet())
		{	
			//Target Living
			if (_searchMap.get(cur) instanceof LivingEntity)
			{
				LivingEntity ent = (LivingEntity)_searchMap.get(cur);
				if (ent.isDead())
				{
					toRemove.add(cur);
					UtilPlayer.message(cur, F.main(_moduleName, "Target has been killed."));
					continue;
				}		
			}

			//Target Player
			if (_searchMap.get(cur) instanceof Player)
			{
				Player ent = (Player)_searchMap.get(cur);
				if (!ent.isOnline())
				{
					toRemove.add(cur);
					UtilPlayer.message(cur, F.main(_moduleName, "Target has left the game."));
					continue;
				}
			}
		}

		for (Player cur : toRemove)
			setTarget(cur, cur);
	}
}
