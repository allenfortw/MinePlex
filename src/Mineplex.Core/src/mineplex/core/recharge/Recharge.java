package mineplex.core.recharge;

import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;

public class Recharge extends MiniPlugin
{
	public static Recharge Instance;
	
	public HashSet<String> informSet = new HashSet<String>();
	public NautHashMap<String, NautHashMap<String, Long>> _recharge = new NautHashMap<String, NautHashMap<String, Long>>();
	
	protected Recharge(JavaPlugin plugin)
	{
		super("Recharge", plugin);
	}
	
	public static void Initialize(JavaPlugin plugin)
	{
		Instance = new Recharge(plugin);
	}
	
	@EventHandler
	public void PlayerDeath(PlayerDeathEvent event)
	{
		Get(event.getEntity().getName()).clear();
	}
	
	public NautHashMap<String, Long> Get(String name)
	{
		if (!_recharge.containsKey(name))
			_recharge.put(name, new NautHashMap<String, Long>());
		
		return _recharge.get(name);
	}

	public NautHashMap<String, Long> Get(Player player)
	{
		return Get(player.getName());
	}
	
	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTER)
			return; 
		
		recharge();
	}
	
	public void recharge()
	{
		for (Player cur : UtilServer.getPlayers())
		{
			LinkedList<String> rechargeList = new LinkedList<String>();

			//Check Recharged
			for (String ability : Get(cur).keySet())
			{
				if (System.currentTimeMillis() > Get(cur).get(ability))
					rechargeList.add(ability);
			}

			//Inform Recharge
			for (String ability : rechargeList)
			{
				Get(cur).remove(ability);
				
				if (informSet.contains(ability))
					UtilPlayer.message(cur, F.main("Recharge", "You can use " + F.skill(ability) + "."));
			}
		}
	}
	public boolean use(Player player, String ability, long recharge, boolean inform)
	{
		return use(player, ability, ability, recharge, inform);
	}

	public boolean use(Player player, String ability, String abilityFull, long recharge, boolean inform)
	{
		if (recharge == 0)
			return true;

		//Ensure Expirey
		recharge();
		
		//Lodge Recharge Msg
		if (inform)
			informSet.add(ability);
	
		//Recharging
		if (Get(player).containsKey(ability))
		{
			if (inform)
			{
				UtilPlayer.message(player, F.main("Recharge", "You cannot use " + F.skill(abilityFull) + " for " + 
						F.time(UtilTime.convertString((Get(player).get(ability)-System.currentTimeMillis()), 1, TimeUnit.FIT)) + "."));
			}

			return false;
		}

		//Insert
		UseRecharge(player, ability, recharge);

		return true;
	}
	
	public void useForce(Player player, String ability, long recharge)
	{
		UseRecharge(player, ability, recharge);
	}
	
	public boolean usable(Player player, String ability)
	{
		if (!Get(player).containsKey(ability))
			return true;
		
		return (System.currentTimeMillis() > Get(player).get(ability));
	}
	
	public void UseRecharge(Player player, String ability, long recharge)
	{
		//Event
		RechargeEvent rechargeEvent = new RechargeEvent(player, ability, recharge);
		UtilServer.getServer().getPluginManager().callEvent(rechargeEvent);
		
		Get(player).put(ability, System.currentTimeMillis()+rechargeEvent.GetRecharge());
	}

	@EventHandler
	public void clearPlayer(PlayerQuitEvent event)
	{
		_recharge.remove(event.getPlayer().getName());
	}
	
	public void Reset(Player player) 
	{
		_recharge.put(player.getName(), new NautHashMap<String, Long>());
	}
}
