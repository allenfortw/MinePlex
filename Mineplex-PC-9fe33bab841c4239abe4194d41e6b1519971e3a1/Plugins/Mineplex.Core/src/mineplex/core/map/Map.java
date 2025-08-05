package mineplex.core.map;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.map.commands.MapImage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class Map extends MiniPlugin
{
	JavaPlugin Plugin;

	//Private
	private Player _caller = null;
	private String _url = "http://chivebox.com/img/mc/news.png";
	private String _defaultUrl = null;

	public Map(JavaPlugin plugin) 
	{
		super("Map Manager", plugin);

		Plugin = plugin;
	}

	@Override
	public void AddCommands() 
	{ 
		AddCommand(new MapImage(this));
	}
	
	public ItemStack GetMap()
	{
		return ItemStackFactory.Instance.CreateStack(Material.MAP, (byte)127, 1, C.cAqua + C.Bold + "iMap 3.0");
	}

	public void SpawnMap(Player caller, String[] args)
	{
		if (args == null || args.length == 0)
		{
			UtilPlayer.message(_caller, F.main("Map Image", "Missing Image URL!"));
			return;
		}

		_caller = caller;
		_url = args[0];
		caller.getInventory().addItem(GetMap());
		
		if (args.length > 1)
		{
			if (args[1].equals("all"))
			{
				for (Player player : UtilServer.getPlayers())
				{				
					if (player.equals(caller))
						continue;
					
					player.getInventory().remove(Material.MAP);

					player.getInventory().addItem(GetMap());
				}
			}
			else
			{
				Player target = UtilPlayer.searchOnline(caller, args[1], true);
				if (target != null)
				{
					target.getInventory().remove(Material.MAP);
					target.getInventory().addItem(GetMap());
				}
			}
		}
	}

	@EventHandler
	public void MapInit(MapInitializeEvent event) 
	{
		//Map Setup
		final MapView map = event.getMap();

		for (MapRenderer rend : map.getRenderers())
			map.removeRenderer(rend);

		if (_defaultUrl != null)
		{
			Plugin.getServer().getScheduler().runTaskAsynchronously(Plugin, new Runnable()
			{
				public void run()
				{
					try 
					{
						map.addRenderer(new ImageRenderer(_defaultUrl));
					} 
					catch (Exception e) 
					{
						System.out.println("Invalid Default Image: " + _defaultUrl);
					}

					_defaultUrl = null;
				} 
			});

		}
		else if (_url != null)
		{
			Plugin.getServer().getScheduler().runTaskAsynchronously(Plugin, new Runnable()
			{
				public void run()
				{
					try 
					{
						map.addRenderer(new ImageRenderer(_url));
						UtilPlayer.message(_caller, F.main("Map Image", "Loaded Image: " + _url));
					} 
					catch (Exception e) 
					{
						UtilPlayer.message(_caller, F.main("Map Image", "Invalid Image URL: " + _url));
					}
				} 
			});
		}
	}

	public void SetDefaultUrl(String string)
	{
		_defaultUrl = string;
	}	
}
