package mineplex.core;

import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.common.util.NautHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MiniClientPlugin<DataType extends Object> extends MiniPlugin
{
	private NautHashMap<String, DataType> _clientData = new NautHashMap<String, DataType>();
	
	public MiniClientPlugin(String moduleName, JavaPlugin plugin) 
	{
		super(moduleName, plugin);
	}
	
	@EventHandler
	public void UnloadPlayer(ClientUnloadEvent event)
	{
		_clientData.remove(event.GetName());
	}
	
	public DataType Get(String name)
	{
		if (!_clientData.containsKey(name))
			_clientData.put(name, AddPlayer(name));
		
		return _clientData.get(name);
	}
	
	public DataType Get(Player player)
	{
		return Get(player.getName());
	}
	
	protected void Set(Player player, DataType data)
	{
		Set(player.getName(), data);
	}
	
	protected void Set(String name, DataType data)
	{
		_clientData.put(name, data);
	}
	
	protected abstract DataType AddPlayer(String player);
}
