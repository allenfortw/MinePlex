package mineplex.core.command;

import mineplex.core.MiniPlugin;
import mineplex.core.common.Rank;
import mineplex.core.common.util.NautHashMap;

import org.bukkit.entity.Player;

public abstract class MultiCommandBase<PluginType extends MiniPlugin> extends CommandBase<PluginType>
{
	protected NautHashMap<String, ICommand> Commands;
	
	public MultiCommandBase(PluginType plugin, Rank rank, String...aliases)
	{
		super(plugin, rank, aliases);
		
		Commands = new NautHashMap<String, ICommand>();
	}
	
	public void AddCommand(ICommand command)
	{
		for (String commandRoot : command.Aliases())
		{
			Commands.put(commandRoot, command);
			command.SetCommandCenter(CommandCenter);
		}
	}
	@Override
	public void Execute(Player caller, String[] args)
	{
		String commandName = null;
		String[] newArgs = null;
		
		if (args != null && args.length > 0)
		{
			commandName = args[0];
			
			if (args.length > 1)
			{
				newArgs = new String[args.length - 1];
				
				for (int i = 0 ; i < newArgs.length; i++)
				{
					newArgs[i] = args[i+1];
				}
			}
		}
		
		ICommand command = Commands.get(commandName);
		
		if (command != null && CommandCenter.ClientManager.Get(caller).GetRank().Has(caller, command.GetRequiredRank(), true))
		{
			command.SetAliasUsed(commandName);

			command.Execute(caller, newArgs);
		}
		else
		{
			Help(caller, args);
		}
	}
	
	protected abstract void Help(Player caller, String[] args);
}
