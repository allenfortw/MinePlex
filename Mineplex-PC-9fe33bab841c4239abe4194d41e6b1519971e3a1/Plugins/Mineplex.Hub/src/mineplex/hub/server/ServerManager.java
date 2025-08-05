package mineplex.hub.server;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.donation.DonationManager;
import mineplex.core.portal.Portal;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.server.command.ServerNpcCommand;
import mineplex.hub.server.ui.ServerNpcShop;

public class ServerManager extends MiniPlugin implements PluginMessageListener
{
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private Portal _portal;
	
	private NautHashMap<String, List<ServerInfo>> _serverNpcMap = new NautHashMap<String, List<ServerInfo>>();
	private NautHashMap<String, ServerNpcShop> _serverNpcShopMap = new NautHashMap<String, ServerNpcShop>();
	private NautHashMap<String, ServerInfo> _serverInfoMap = new NautHashMap<String, ServerInfo>();
	private NautHashMap<String, Long> _serverUpdate = new NautHashMap<String, Long>();

	private boolean _update = true;
	private boolean _loading = false;
	
	public ServerManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, Portal portal)
	{
		super("Server Manager", plugin);
		
		_clientManager = clientManager;
		_donationManager = donationManager;
		_portal = portal;
		
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeSigns");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeSigns", this);
		
		LoadServers();
	}

	public void AddCommands()
	{
		AddCommand(new ServerNpcCommand(this));
	}
	
	public void AddServer(String serverNpcName, String serverName)
	{
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.Name = serverName;
		
		_serverNpcMap.get(serverNpcName).add(serverInfo);
		
		if (!_serverInfoMap.containsKey(serverName))
		{
			_serverInfoMap.put(serverName, serverInfo);
			_serverUpdate.put(serverName, System.currentTimeMillis());
		}
		
		SaveServers();
	}
	
	public void RemoveServer(String serverName)
	{
		for (String key : _serverNpcMap.keySet())
		{
			_serverNpcMap.get(key).remove(serverName);
		}
		
		_serverInfoMap.remove(serverName);
	}
	
	public void AddServerNpc(String serverNpcName)
	{
		_serverNpcMap.put(serverNpcName, new ArrayList<ServerInfo>());
		_serverNpcShopMap.put(serverNpcName, new ServerNpcShop(this, _clientManager, _donationManager, serverNpcName));
	}
	
	public void RemoveServerNpc(String serverNpcName)
	{
		List<ServerInfo> mappedServers = _serverNpcMap.remove(serverNpcName);
		_serverNpcShopMap.remove(serverNpcName);
		
		if (mappedServers != null)
		{
			for (ServerInfo mappedServer : mappedServers)
			{
				boolean isMappedElseWhere = false;
				
				for (String key : _serverNpcMap.keySet())
				{
					for (ServerInfo value : _serverNpcMap.get(key))
					{
						if (value.Name.equalsIgnoreCase(mappedServer.Name))
						{
							isMappedElseWhere = true;
							break;
						}
					}
					
					if (isMappedElseWhere)
						break;
				}
				
				if (!isMappedElseWhere)
					_serverInfoMap.remove(mappedServer.Name);
			}
		}
	}
	
	public List<ServerInfo> GetServerList(String serverNpcName)
	{
		return _serverNpcMap.get(serverNpcName);
	}
	
	public Set<String> GetAllServers()
	{
		return _serverInfoMap.keySet();
	}
	
	public ServerInfo GetServerInfo(String serverName)
	{
		return _serverInfoMap.get(serverName);
	}
	
	public boolean HasServerNpc(String serverNpcName) 
	{
		return _serverNpcMap.containsKey(serverNpcName);
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

			if (_serverInfoMap.containsKey(serverName))
			{
				ServerInfo serverInfo = _serverInfoMap.get(serverName);
				serverInfo.MOTD = motd;
				serverInfo.CurrentPlayers = players;
				serverInfo.MaxPlayers = maxPlayers;
				
				_serverUpdate.put(serverName, System.currentTimeMillis());
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
	
	@EventHandler
	public void UpdateServers(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		_update = !_update;
		
		if (!_update)
		{
			for (ServerNpcShop shop : _serverNpcShopMap.values())
			{
				shop.UpdatePages();
			}
			
			return;
		}
		
		if (Bukkit.getServer().getOnlinePlayers().length > 0)
		{
			for (String serverName : _serverInfoMap.keySet())
			{				
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
				
				Bukkit.getServer().getOnlinePlayers()[0].sendPluginMessage(GetPlugin(), "BungeeSigns", b.toByteArray());
				
				if (_serverUpdate.get(serverName) != -1L && System.currentTimeMillis() - _serverUpdate.get(serverName) > 5000)
				{
					ServerInfo serverInfo = _serverInfoMap.get(serverName);
					serverInfo.MOTD = ChatColor.DARK_RED + "OFFLINE";
					serverInfo.CurrentPlayers = 0;
					serverInfo.MaxPlayers = 0;
					
					_serverUpdate.put(serverName, -1L);
				}
			}
		}
	}

	public void Help(Player caller, String message)
	{
		UtilPlayer.message(caller, F.main(_moduleName, "Commands List:"));
		UtilPlayer.message(caller, F.help("/servernpc create <name>", "<name> is name of npc.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc delete <name>", "<name> is name of npc.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc addserver <servernpc> | <name>", "Adds server.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc removeserver <name>", "Removes server.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc listnpcs", "Lists all server npcs.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc listservers <servernpc>", "Lists all servers.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/servernpc listoffline", "Shows all servers offline.", Rank.OWNER));
		
		if (message != null)
			UtilPlayer.message(caller, F.main(_moduleName, ChatColor.RED + message));
	}
	
	public void Help(Player caller)
	{
		Help(caller, null);
	}

	public void SelectServer(org.bukkit.entity.Player player, String serverName)
	{
		player.leaveVehicle();
		player.eject();
		
		_portal.SendPlayerToServer(player, serverName);
	}

	public void ListServerNpcs(Player caller)
	{
		UtilPlayer.message(caller, F.main(GetName(), "Listing Server Npcs:"));
		
		for (String serverNpc : _serverNpcMap.keySet())
		{
			UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc));
		}
	}
	
	public void ListServers(Player caller, String serverNpcName)
	{
		UtilPlayer.message(caller, F.main(GetName(), "Listing Servers for '" + serverNpcName + "':"));
		
		for (ServerInfo serverNpc : _serverNpcMap.get(serverNpcName))
		{
			UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc.Name +  C.cWhite + " - " + serverNpc.MOTD + " " + serverNpc.CurrentPlayers + "/" + serverNpc.MaxPlayers));
		}
	}
	
	public void ListOfflineServers(Player caller)
	{
		UtilPlayer.message(caller, F.main(GetName(), "Listing Offline Servers:"));
		
		for (ServerInfo serverNpc : _serverInfoMap.values())
		{
			if (serverNpc.MOTD.equalsIgnoreCase(ChatColor.DARK_RED + "OFFLINE"))
			{
				UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc.Name +  C.cWhite + " - " + F.time(UtilTime.convertString(System.currentTimeMillis() - _serverUpdate.get(serverNpc.Name), 0, TimeUnit.FIT))));
			}
		}
	}
	
	public void SaveServers()
	{
		if (_loading)
			return;
		
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			fstream = new FileWriter("ServerManager.dat");
			out = new BufferedWriter(fstream);
			
			for (String key : _serverNpcMap.keySet())
			{
				System.out.println("Saving serverinfos for " + key);
				for (ServerInfo serverInfo : _serverNpcMap.get(key))
				{
					out.write(key + " | " + serverInfo.Name);
					out.newLine();
				}
			}

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("ServerManager Save Error: " + e.getMessage());
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
	
	public void LoadServers()
	{
		_loading = true;
		
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			File npcFile = new File("ServerManager.dat");

			if (npcFile.exists())
			{
				fstream = new FileInputStream(npcFile);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					String serverNpcName = line.substring(0, line.indexOf('|')).trim();
					String server = line.substring(line.indexOf('|') + 1).trim();
					
					if (!HasServerNpc(serverNpcName))
					{
						AddServerNpc(serverNpcName);
					}
					
					AddServer(serverNpcName, server);
					
					line = br.readLine();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ServerManager - Error parsing servers file : " + e.getMessage());
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
			
			_loading = false;
		}
	}
}
