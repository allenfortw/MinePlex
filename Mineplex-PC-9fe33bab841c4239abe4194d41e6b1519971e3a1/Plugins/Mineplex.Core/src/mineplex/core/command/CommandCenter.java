package mineplex.core.command;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandCenter implements Listener
{
	public static CommandCenter Instance;
	
	protected JavaPlugin Plugin;
	protected CoreClientManager ClientManager;
	protected NautHashMap<String, ICommand> Commands;
	
	public static void Initialize(JavaPlugin plugin, CoreClientManager clientManager)
	{
		if (Instance == null)
			Instance = new CommandCenter(plugin, clientManager);
	}
	
	public CoreClientManager GetClientManager()
	{
		return ClientManager;
	}
	
	private CommandCenter(JavaPlugin instance, CoreClientManager manager)
	{
		Plugin = instance;
		ClientManager = manager;
		Commands = new NautHashMap<String, ICommand>();
		Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
	}
	
	@EventHandler
	public void OnPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String commandName = event.getMessage().substring(1);
		String[] args = null;
		
		if (commandName.contains(" "))
		{
			commandName = commandName.split(" ")[0];
			args = event.getMessage().substring(event.getMessage().indexOf(' ') + 1).split(" ");
		}
		
		ICommand command = Commands.get(commandName);
		
		if (command != null && ClientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), command.GetRequiredRank(), true))
		{
			command.SetAliasUsed(commandName);
			command.Execute(event.getPlayer(), args);
			
			event.setCancelled(true);
		}
	}
	
	public void AddCommand(ICommand command)
	{
		for (String commandRoot : command.Aliases())
		{
			Commands.put(commandRoot, command);
			command.SetCommandCenter(this);
		}
	}

	public void RemoveCommand(ICommand command)
	{
		for (String commandRoot : command.Aliases())
		{
			Commands.remove(commandRoot);
			command.SetCommandCenter(null);
		}
	}
}
