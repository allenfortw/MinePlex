package mineplex.minecraft.BungeeSigns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeSigns extends JavaPlugin implements Listener, PluginMessageListener, Runnable
{
	private HashMap<String, Location> _signs = new HashMap<String, Location>();
	private HashMap<String, Long> _signUpdate = new HashMap<String, Long>();
	
	@Override
	public void onEnable()
	{
		LoadSigns();
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeSigns");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeSigns", this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, this, 0L, 40L);
	}
	
	public void onDisable()
	{
		_signs.clear();
	}
	
	@EventHandler
	public void PlayerBreakSign(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return;
		
		if ((event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST))
		{
			String serverName = ChatColor.stripColor(((Sign)event.getBlock().getState()).getLine(1));
			
			if (_signs.containsKey(serverName))
			{
				_signs.remove(serverName);
				SaveSigns();
			}
		}
	}
	
	@EventHandler
	public void PlayerClickSign(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && _signs.containsKey(ChatColor.stripColor(((Sign)event.getClickedBlock().getState()).getLine(1))))
		{
			String serverName = ChatColor.stripColor(((Sign)event.getClickedBlock().getState()).getLine(1));
			
			if (_signUpdate.containsKey(serverName) && _signUpdate.get(serverName) == -1L)
			{
				event.getPlayer().sendMessage(ChatColor.BLUE + "BungeeSigns" + ChatColor.GRAY + "> " + ChatColor.RED + "That server is offline.");
				return;
			}
			
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
			
			event.getPlayer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
		}
	}
	
	@EventHandler
	public void PlayerPlaceSign(SignChangeEvent event)
	{
		if (event.isCancelled() || !event.getPlayer().isOp())
			return;
		
		if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST)
		{
			if (event.getLine(0).equalsIgnoreCase("[BungeeSigns]"))
			{
				if (_signs.containsKey(event.getLine(1)) && _signs.get(event.getLine(1)) != event.getBlock().getLocation())
				{
					_signs.get(event.getLine(1)).getBlock().setType(Material.AIR);
				}
				
				_signs.put(event.getLine(1), event.getBlock().getLocation());
				SaveSigns();
			}
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
        if (!channel.equalsIgnoreCase("BungeeSigns"))
            return;
        
        DataInputStream in = null;
        String serverName = null;
        String motd = null;
        int players = 0;
        int maxPlayers = 0;
        
		try
		{
			in = new DataInputStream(new ByteArrayInputStream(message));
			serverName = in.readUTF();
			motd = in.readUTF();
			players = in.readInt();
			maxPlayers = in.readInt();

			if (_signs.containsKey(serverName))
			{
				Block block = _signs.get(serverName).getBlock();
				
				if (block != null && block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				{
					Sign sign = (Sign)block.getState();
					
					sign.setLine(0, ChatColor.UNDERLINE + "Click to Join!");
					sign.setLine(1, ChatColor.BOLD + serverName);
					sign.setLine(2, motd);
					sign.setLine(3, players + "/" + maxPlayers);
					sign.update(true);
				}
				
				_signUpdate.put(serverName, System.currentTimeMillis());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
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
	}
	
	public void run()
	{
		if (getServer().getOnlinePlayers().length > 0)
		{
			for (String serverName : _signs.keySet())
			{
				if (!_signUpdate.containsKey(serverName))
				{
					_signUpdate.put(serverName, System.currentTimeMillis());
				}
				
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				
				try
				{
					out.writeUTF(serverName);
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				getServer().getOnlinePlayers()[0].sendPluginMessage(this, "BungeeSigns", b.toByteArray());
				
				if (_signUpdate.get(serverName) != -1L && System.currentTimeMillis() - _signUpdate.get(serverName) > 5000)
				{
					Block block = _signs.get(serverName).getBlock();
					
					if (block != null && block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
					{
						Sign sign = (Sign)block.getState();
						
						sign.setLine(0, ChatColor.UNDERLINE + "" + ChatColor.DARK_RED + "Don't touch me!");
						sign.setLine(1, ChatColor.BOLD + serverName);
						sign.setLine(2, ChatColor.DARK_RED + "OFFLINE");
						sign.setLine(3, "?/?");
						sign.update(true);
					}
					
					_signUpdate.put(serverName, -1L);
				}
			}
		}
	}
	
	public void SaveSigns()
	{
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			fstream = new FileWriter("BungeeSigns.dat");
			out = new BufferedWriter(fstream);
			
			for (String key : _signs.keySet())
			{
					out.write(key + " " + locToStr(_signs.get(key)));
					out.newLine();
			}

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("BungeeSigns Save Error: " + e.getMessage());
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
	
	public void LoadSigns()
	{
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			File npcFile = new File("BungeeSigns.dat");

			if (npcFile.exists())
			{
				fstream = new FileInputStream(npcFile);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					String name = line.split(" ")[0];
					String location = line.split(" ")[1];
					
			    	_signs.put(name, strToLoc(location));
					
					line = br.readLine();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("BungeeSigns Error parsing npc file : " + e.getMessage());
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
	
	public String locToStr(Location loc)
	{
		if (loc == null)
			return "";
		
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}
	
	public Location strToLoc(String string)
	{
		if (string.length() == 0)
			return null;
		
		String[] tokens = string.split(",");
		
		try
		{
			for (World world : getServer().getWorlds())
			{
				if (world.getName().equalsIgnoreCase(tokens[0]))
				{
					return new Location(world, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
				}
			}
		}
		catch (Exception e)
		{
			return null;
		}
		
		return null;
	}
}
