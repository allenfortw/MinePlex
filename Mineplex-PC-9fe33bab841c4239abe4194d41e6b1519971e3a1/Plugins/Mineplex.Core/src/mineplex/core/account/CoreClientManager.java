package mineplex.core.account;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import mineplex.core.account.event.AsyncClientLoadEvent;
import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.account.repository.AccountRepository;
import mineplex.core.account.repository.token.ClientToken;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.TimeSpan;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreClientManager implements Listener
{
	private static CoreClientManager _instance;
	
	private JavaPlugin _plugin;
	private AccountRepository _repository;
	private HashSet<String> _allClients;
	private HashMap<String, CoreClient> _clientList;
	private HashSet<String> _dontRemoveList;
	private NautHashMap<String, Entry<CoreClient, Long>> _cacheList;
	
	private Object _clientLock = new Object();
	
	protected CoreClientManager(JavaPlugin plugin, String webServer)
	{
		_instance = this;
		
		_plugin = plugin;
		_repository = new AccountRepository(webServer);
        _allClients = new HashSet<String>();
        _clientList = new HashMap<String, CoreClient>();
        _dontRemoveList = new HashSet<String>();
        _cacheList = new NautHashMap<String, Entry<CoreClient, Long>>();
        
        for (String clientName : _repository.GetAllClientNames())
        {
            _allClients.add(clientName);
        }
        
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
	}
	
	public static CoreClientManager Initialize(JavaPlugin plugin, String webServer)
	{
		if (_instance == null)
		{
			_instance = new CoreClientManager(plugin, webServer);
		}
		
		return _instance;
	}

	public CoreClient Add(String name)
	{
		CoreClient newClient = null;
		
		synchronized (this)
		{
			if (_cacheList.containsKey(name))
			{
				newClient = _cacheList.get(name).getKey();
				_cacheList.remove(name);
			}
		}
	    
		if (newClient == null)
		{
			newClient = new CoreClient(name);
		}
		
		CoreClient oldClient = null;
		
		synchronized(_clientLock)
		{
			oldClient = _clientList.put(name, newClient);
		}
	    
	    if (oldClient != null)
	    {
	    	oldClient.Delete();
	    }

		return newClient;
	}

	public CoreClient Add(Player player)
	{
		CoreClient newClient = new CoreClient(player);
	    CoreClient oldClient = null;
	    
		synchronized(_clientLock)
		{
			oldClient = _clientList.put(player.getName(), newClient);
		}
	    
	    if (oldClient != null)
	    {
	    	oldClient.Delete();
	    }

		return newClient;
	}

	public void Del(String name)
	{
		CoreClient removedClient = null;

		synchronized(_clientLock)
		{
			removedClient = _clientList.remove(name); 
		}
		synchronized (this)
		{
			_cacheList.put(name, new AbstractMap.SimpleEntry<CoreClient, Long>(removedClient, System.currentTimeMillis() + TimeSpan.MINUTE));
		}
	}

	public CoreClient Get(String name)
	{
		synchronized(_clientLock)
		{
			return _clientList.get(name);
		}
	}
	
	public CoreClient Get(Player player)
	{
		synchronized(_clientLock)
		{
			return _clientList.get(player.getName());
		}
	}
	
	public HashSet<String> GetAll() 
	{
		return _allClients;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void AsyncLogin(AsyncPlayerPreLoginEvent event)
	{
		try
		{	
			LoadClient(Add(event.getName()), event.getAddress().getHostAddress());
		}
		catch(Exception exception)
		{
			System.out.println("Error logging '" + event.getName() + "' in.");
			
			System.out.println(exception.getMessage());
			
			for (StackTraceElement element : exception.getStackTrace())
			{
				System.out.println(element);
			}

			event.disallow(Result.KICK_OTHER, "Error retrieving information from web, please retry in a minute.");
		}
	}
	
	private void LoadClient(CoreClient client, String ipAddress)
	{
		ClientToken token = null;
		Gson gson = new Gson();
	    
		String response = _repository.GetClient(client.GetPlayerName(), ipAddress);
        token = gson.fromJson(response, ClientToken.class);
		
		client.SetAccountId(token.AccountId);
		client.SetFilterChat(token.FilterChat);
		client.SetRank(Rank.valueOf(token.Rank));
		
		Bukkit.getServer().getPluginManager().callEvent(new ClientWebResponseEvent(response));
		Bukkit.getServer().getPluginManager().callEvent(new AsyncClientLoadEvent(token, client));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Login(PlayerLoginEvent event)
	{
        CoreClient client = Get(event.getPlayer().getName());
        client.SetPlayer(event.getPlayer());
		
        // Reserved Slot Check
		if (Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
		{
			if (client.GetRank().Has(event.getPlayer(), Rank.HELPER, false))
			{
				event.allow();
				event.setResult(PlayerLoginEvent.Result.ALLOWED);
				return;
			}
			
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Full > Donate for Ultra");
		}
	}
	
	@EventHandler
	public void Kick(PlayerKickEvent event)
	{
		if (event.getReason().equalsIgnoreCase("You logged in from another location"))
		{
			_dontRemoveList.add(event.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void Quit(PlayerQuitEvent event)
	{
		if (!_dontRemoveList.contains(event.getPlayer().getName()))
		{
			Del(event.getPlayer().getName());
		}
		
		_dontRemoveList.remove(event.getPlayer().getName());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void ClearCache(UpdateEvent event)
	{
		if (event.getType() == UpdateType.SLOW)
		{
			synchronized (this)
			{
				Iterator<String> cacheIterator = _cacheList.keySet().iterator();
				
				while (cacheIterator.hasNext())
				{
					String name = cacheIterator.next();
					
					if (System.currentTimeMillis() >= _cacheList.get(name).getValue())
					{
						cacheIterator.remove();
						_plugin.getServer().getPluginManager().callEvent(new ClientUnloadEvent(name));
					}
				}
			}
		}
	}

	public void SaveRank(final String name, Rank rank, boolean perm)
	{
		_repository.SaveRank(new Callback<Rank>()
		{
			public void run(Rank newRank)
			{
				if (_plugin.getServer().getPlayer(name) != null)
				{
					CoreClient client = Get(name);				

					client.SetRank(newRank);
				}
			}
		}, name, rank, perm);
	}
}