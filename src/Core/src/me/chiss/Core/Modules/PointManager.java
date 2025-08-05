package me.chiss.Core.Modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import me.chiss.Core.Clans.ClansClan;
import me.chiss.Core.Clans.ClansUtility.ClanRelation;
import me.chiss.Core.ClientData.ClientGame;
import me.chiss.Core.Module.AModule;
import me.chiss.Core.Scheduler.IScheduleListener;
import me.chiss.Core.Scheduler.Scheduler;
import mineplex.core.server.IRepository;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.donation.repository.token.PlayerUpdateToken;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PointManager extends AModule implements IScheduleListener
{
	private IRepository Repository;
	
	private long _interval = 900000;

	private String _folder = "data/pph/";
	
	private HashMap<String, Long> _quitMap = new HashMap<String, Long>();
	private HashMap<String, Integer> _pointTotals = new HashMap<String, Integer>();
	
	
	private int _maxPPH;
	
	private int _pphOnline;

	private int _pphOnlineClan;
	private int _pphOfflineClan;

	private int _pphOnlinePet;
	private int _pphOfflinePet;

	private int _pphNAC; 

	public PointManager(JavaPlugin plugin, Scheduler scheduler, IRepository repository, int maxPPH, int pphOnline, int pphOnlineClanAge, int pphOfflineClanAge, int pphOnlinePetLevel, int pphOfflinePetLevel, int pphNAC) 
	{
		super("Point Manager", plugin);
		
		scheduler.ScheduleDailyRecurring(this, 0);
		
		Repository = repository;
		
		//Dir
		File file = new File(_folder);
		file.mkdirs();
		

		_maxPPH = maxPPH;
		_pphOnline = pphOnline;

		_pphOnlineClan = pphOnlineClanAge;
		_pphOfflineClan = pphOfflineClanAge;

		_pphOnlinePet = pphOnlinePetLevel;
		_pphOfflinePet = pphOfflinePetLevel;

		_pphNAC = pphNAC;
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void Drop(PlayerDropItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getItemDrop().getItemStack().getType() != Material.EMERALD)
			return;
		
		UtilPlayer.message(event.getPlayer(), F.main("Loot", "You cannot drop " + F.item("Emerald") + "."));
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getItem().getItemStack().getType() != Material.EMERALD)
			return;
		
		//Effect
		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.5f, 1f);
	}
	
	@EventHandler
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getItem().getItemStack().getType() != Material.EMERALD)
			return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void Inventory(InventoryOpenEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!event.getPlayer().getInventory().contains(Material.EMERALD))
			return;
		
		UtilPlayer.message(event.getPlayer(), F.main("Loot", "You cannot use this while holding " + F.item("Emerald") + "."));
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void Deposit(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.getItemInHand() == null)
			return;
		
		if (player.getItemInHand().getType() != Material.EMERALD)
			return;
		
		if (!Clans().CUtil().isClaimed(player.getLocation()) || 
				Clans().CUtil().getAccess(player, player.getLocation()) != ClanRelation.SELF)
		{
			UtilPlayer.message(event.getPlayer(), F.main("Loot", "You can only claim " + F.item("Emerald") + 
					" in your Territory."));
			return;
		}
		
		int amount = UtilInv.removeAll(player, Material.EMERALD, (byte)0);
		int per = 1000;
		
		UtilPlayer.message(event.getPlayer(), F.main("Loot", "You claimed " + F.item(amount + " Emeralds") + 
				" for " + F.count((amount * per) + " Coins") + "."));
		
		//Economy
		Clients().Get(player).Game().ModifyEconomyBalance(amount * per);
		
		//Effect
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 2f);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void AsyncLogin(AsyncPlayerPreLoginEvent event)
	{
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		
		long quit = ReadQuit(event.getName());
		
		Clients().Get(event.getName()).Game().SetLastPPH(System.currentTimeMillis());
		
		if (!UtilTime.elapsed(quit, 3600000))
			return;

		_quitMap.put(event.getName(), quit);
		WriteQuit(event.getName());
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		_quitMap.remove(event.getPlayer().getName());
		WriteQuit(event.getPlayer().getName());
	}
	
	@EventHandler
	public void UpdateOffline(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;

		for (String name : _quitMap.keySet())
		{
			Player cur = UtilPlayer.searchExact(name);
			if (cur == null)		continue;
			
			boolean capped = false;

			if (!_pointTotals.containsKey(cur.getName()))
				_pointTotals.put(cur.getName(), 0);
			if ( _pointTotals.get(cur.getName()) >= _maxPPH)
				capped = true;
			
			if (capped)
			{
				UtilPlayer.message(cur, "§c§lYou have already earned the max of " + _maxPPH + " Points today.");
				UtilPlayer.message(cur, F.value("You will earn more in:", UtilTime.convertString(Scheduler.Instance.GetTimeTilNextAppt(this), 1, TimeUnit.FIT)));
				continue;
			}
			
			double hours = (System.currentTimeMillis() - _quitMap.get(name)) / 3600000d;
			if (hours > 12)
				hours = 12;

			double petLevel = 0;
			double clanAge = 0;
			
			ClansClan clan = Clans().CUtil().getClanByPlayer(cur);
			if (clan != null)	clanAge = (System.currentTimeMillis() - clan.GetDateCreated()) / 86400000d;
			if (clanAge > 7)
				clanAge = 7;
			
			int clanPoints = (int) (_pphOfflineClan * clanAge * hours);
			int petPoints = (int) (_pphOfflinePet * petLevel * hours);
			int totalPoints = clanPoints + petPoints;
			boolean hitCap = false;
			
			if (totalPoints == 0)
				continue;
			else if (totalPoints + _pointTotals.get(cur.getName()) > _maxPPH)
			{
				hitCap = true;
				totalPoints = _maxPPH - _pointTotals.get(cur.getName());
			}
			
			UtilPlayer.message(cur, "§c§lOffline Point Reward - " + UtilTime.convertString(System.currentTimeMillis() - _quitMap.get(name), 1, TimeUnit.FIT));
			UtilPlayer.message(cur, F.value("Clan Age", clanPoints + " Points"));
			UtilPlayer.message(cur, F.value("Battle Pet", petPoints + " Points"));
			UtilPlayer.message(cur, "You received " + F.count(totalPoints + " Points") + ".");
			
			if (hitCap)
			{
				UtilPlayer.message(cur, "§c§lYou have already earned the max of " + _maxPPH + " Points today.");
				UtilPlayer.message(cur, F.value("You will earn more in:", UtilTime.convertString(Scheduler.Instance.GetTimeTilNextAppt(this), 1, TimeUnit.FIT)));
			}
			
			//Effect
			cur.playSound(cur.getLocation(), Sound.LEVEL_UP, 1f, 2f);
			
			//Give Points
			PlayerUpdateToken token = new PlayerUpdateToken();
			token.Name = cur.getName();
			token.Points = totalPoints;
			Repository.PlayerUpdate(null, token);
			
			_pointTotals.put(cur.getName(), _pointTotals.get(cur.getName()) + totalPoints);
		}
		
		_quitMap.clear();
	}

	@EventHandler
	public void UpdateOnline(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;
		
		for (Player cur : UtilServer.getPlayers())
		{
			boolean capped = false;

			if (!_pointTotals.containsKey(cur.getName()))
				_pointTotals.put(cur.getName(), 0);
			if ( _pointTotals.get(cur.getName()) >= _maxPPH)
				capped = true;
			
			ClientGame client = Clients().Get(cur).Game();
			
			if (!UtilTime.elapsed(client.GetLastPPH(), _interval)) //15 Minutes
				continue;
			
			client.SetLastPPH(System.currentTimeMillis());
			
			if (capped)
			{
				UtilPlayer.message(cur, "§c§lYou have already earned the max of " + _maxPPH + " Points today.");
				UtilPlayer.message(cur, F.value("You will earn more in:", UtilTime.convertString(Scheduler.Instance.GetTimeTilNextAppt(this), 1, TimeUnit.FIT)));
				continue;
			}
			
			double mult = _interval / 3600000d;
			
			double petLevel = 0;
			double clanAge = 0;
			
			ClansClan clan = Clans().CUtil().getClanByPlayer(cur);
			if (clan != null)	clanAge = (System.currentTimeMillis() - clan.GetDateCreated()) / 86400000d;
			if (clanAge > 7)
				clanAge = 7;
			
			int NACPoints = 0;
			
			if (Clients().Get(cur).NAC().IsUsing())
				NACPoints = (int) (_pphNAC * mult);
			
			int clanPoints = (int) (mult * _pphOnlineClan * clanAge);
			int petPoints = (int) (mult * _pphOnlinePet * petLevel);
			int onlinePoints = (int) (mult * _pphOnline);
			
			int totalPoints = clanPoints + petPoints + NACPoints + onlinePoints;
			boolean hitCap = false;
			
			if (totalPoints == 0)
				continue;
			else if (totalPoints + _pointTotals.get(cur.getName()) > _maxPPH)
			{
				hitCap = true;
				totalPoints = _maxPPH - _pointTotals.get(cur.getName());
			}
			
			UtilPlayer.message(cur, "§a§lOnline Point Reward - " + UtilTime.convertString(_interval, 1, TimeUnit.FIT));
			UtilPlayer.message(cur, F.value("Online", onlinePoints + " Points"));
			UtilPlayer.message(cur, F.value("NAC User", NACPoints + " Points"));
			UtilPlayer.message(cur, F.value("Clan Age", clanPoints + " Points"));
			UtilPlayer.message(cur, F.value("Battle Pet", petPoints + " Points"));
			UtilPlayer.message(cur, "You received " + F.count(totalPoints + " Points") + ".");
			
			if (hitCap)
			{
				UtilPlayer.message(cur, "§c§lYou have already earned the max of " + _maxPPH + " Points today.");
				UtilPlayer.message(cur, F.value("You will earn more in:", UtilTime.convertString(Scheduler.Instance.GetTimeTilNextAppt(this), 1, TimeUnit.FIT)));
			}
			
			//Effect
			cur.playSound(cur.getLocation(), Sound.LEVEL_UP, 1f, 2f);
			
			//Give Points
			PlayerUpdateToken token = new PlayerUpdateToken();
			token.Name = cur.getName();
			token.Points = totalPoints;
			Repository.PlayerUpdate(null, token);
			
			_pointTotals.put(cur.getName(), _pointTotals.get(cur.getName()) + totalPoints);
		}
	}
	
	private long ReadQuit(String name) 
	{
		long quitTime = System.currentTimeMillis();
		
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			File f = new File(_folder + name + ".dat");

			if (!f.exists())
				return quitTime;
			
			fstream = new FileInputStream(f);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			
			try
			{
				quitTime = Long.parseLong(strLine);
			}
			catch (Exception e)
			{
				
			}
		}
		catch (Exception e)
		{
			System.err.println("Build Read Error: " + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (in != null)
			{
				try
				{
					in.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return quitTime;
	}

	private void WriteQuit(String name) 
	{
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			fstream = new FileWriter(_folder + name + ".dat");
			out = new BufferedWriter(fstream);

			out.write("" + System.currentTimeMillis());

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("PPH Write Error: " + e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void AppointmentFire()
	{
		_pointTotals.clear();
	}
}
