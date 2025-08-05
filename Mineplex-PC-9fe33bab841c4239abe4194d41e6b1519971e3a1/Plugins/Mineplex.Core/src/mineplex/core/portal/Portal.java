package mineplex.core.portal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map.Entry;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import mineplex.core.MiniPlugin;
import mineplex.core.arena.Region;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.portal.commands.PortalCommand;

public class Portal extends MiniPlugin
{
	private NautHashMap<Region, String> _portalServerMap = new NautHashMap<Region, String>();	
	private NautHashMap<String, Entry<Location, Location>> _portalSetupMap = new NautHashMap<String, Entry<Location, Location>>();
	private HashSet<String> _connectingPlayers = new HashSet<String>();
	
	public Portal(JavaPlugin plugin)
	{
		super("Portal", plugin);
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(GetPlugin(), "BungeeCord");
		
		LoadPortals();
	}
	
	public void AddCommands()
	{
		AddCommand(new PortalCommand(this));
	}
	
	@EventHandler
	public void OnPlayerMove(PlayerMoveEvent event)
	{
		for (Region region : _portalServerMap.keySet())
		{
			if (region.Contains(event.getTo().toVector()))
			{
				SendPlayerToServer(event.getPlayer(), _portalServerMap.get(region));
				break;
			}
		}
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.isOp() && _portalSetupMap.containsKey(player.getName()))
		{
			if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.BLAZE_ROD)
			{
				if (event.getAction() == Action.LEFT_CLICK_BLOCK)
				{
					_portalSetupMap.put(player.getName(), new AbstractMap.SimpleEntry<Location, Location>(event.getClickedBlock().getLocation(), null));
					player.sendMessage(F.main(GetName(), "Set first point."));
				}
				else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					_portalSetupMap.get(player.getName()).setValue(event.getClickedBlock().getLocation());
					player.sendMessage(F.main(GetName(), "Set second point."));
				}
				
				event.setCancelled(true);
			}
		}
	}
	
	public void SendAllPlayers(String serverName)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			SendPlayerToServer(player, serverName);
		}
	}
	
	public void SendPlayerToServer(final Player player, String serverName)
	{
		if (_connectingPlayers.contains(player.getName()))
			return;
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try 
		{
		    out.writeUTF("Connect");
		    out.writeUTF(serverName);
		}
		catch (IOException e) 
		{
		    // Can never happen
		}
		finally
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
		
		player.sendPluginMessage(GetPlugin(), "BungeeCord", b.toByteArray());
		_connectingPlayers.add(player.getName());
		
		GetScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
		{
			public void run()
			{
				_connectingPlayers.remove(player.getName());
			}
		}, 20L);
	}
	
	public void Help(Player caller, String message)
	{
		UtilPlayer.message(caller, F.main(_moduleName, "Commands List:"));
		UtilPlayer.message(caller, F.help("/portal toggle", "Turn off and on Portal mode.", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/portal create <name>", "Creates portal to name server.", Rank.OWNER));
		
		if (message != null)
			UtilPlayer.message(caller, F.main(_moduleName, ChatColor.RED + message));
	}
	
	public void Help(Player caller)
	{
		Help(caller, null);
	}
	
	public void LoadPortals()
	{
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			File portalsFile = new File("portals.dat");

			if (portalsFile.exists())
			{
				fstream = new FileInputStream(portalsFile);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					Region region = ParseRegion(line);

			    	_portalServerMap.put(region, region.GetName());
					
					line = br.readLine();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(F.main(GetName(), "Error parsing portals file."));
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
	
	public void SavePortals()
	{
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			fstream = new FileWriter("portals.dat");
			out = new BufferedWriter(fstream);
			
			for (Region region : _portalServerMap.keySet())
			{
					out.write(region.GetMinimumPoint().getBlockX() + " " + region.GetMinimumPoint().getBlockY() + " " + region.GetMinimumPoint().getBlockZ() + ", " + region.GetMaximumPoint().getBlockX() + " " + region.GetMaximumPoint().getBlockY() + " " + region.GetMaximumPoint().getBlockZ() + ", " + region.GetName());
					out.newLine();
			}

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("Portals Save Error: " + e.getMessage());
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
	
	private Region ParseRegion(String value)
	{
		String [] parts = value.split(",");
		
		Vector pointOne = ParseVector(parts[0].trim());
		Vector pointTwo = ParseVector(parts[1].trim());
		
		return new Region(parts.length == 3 ? parts[2].trim() : "Null", pointOne, pointTwo);
	}
	
	private Vector ParseVector(String vectorString)
	{
		Vector vector = new Vector();
		
		String [] parts = vectorString.split(" ");
		
		vector.setX(Double.parseDouble(parts[0]));
		vector.setY(Double.parseDouble(parts[1]));
		vector.setZ(Double.parseDouble(parts[2]));
		
		return vector;
	}

	public void ToggleSetupAdmin(Player caller)
	{
		if (_portalSetupMap.containsKey(caller.getName()))
		{
			_portalSetupMap.remove(caller.getName());
			caller.sendMessage(F.main(GetName(), "Disabled Portal Setup."));
		}
		else
		{
			_portalSetupMap.put(caller.getName(), new AbstractMap.SimpleEntry<Location, Location>(null, null));
			caller.sendMessage(F.main(GetName(), "Enabled Portal Setup."));
		}
	}
	
	public boolean IsAdminPortalValid(Player caller)
	{
		return _portalSetupMap.containsKey(caller.getName()) && _portalSetupMap.get(caller.getName()).getKey() != null && _portalSetupMap.get(caller.getName()).getValue() != null; 
	}
	
	public void CreatePortal(Player caller, String name)
	{
		Vector first = _portalSetupMap.get(caller.getName()).getKey().toVector();
		Vector second = _portalSetupMap.get(caller.getName()).getValue().toVector();
		
		_portalServerMap.put(new Region(name, first, second), name);
		caller.sendMessage(F.main(GetName(), "Created '" + name + "' portal at (" + first.toString() + ") and (" + second.toString() + "."));
		SavePortals();
	}
}
