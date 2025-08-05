package mineplex.core.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mineplex.core.MiniPlugin;
import mineplex.core.common.Rank;

public abstract class CommandBase<PluginType extends MiniPlugin> implements ICommand
{	
	private Rank _requiredRank;
	private List<String> _aliases;
	
	protected PluginType Plugin;
	protected String AliasUsed;
	protected CommandCenter CommandCenter;
	
	public CommandBase(PluginType plugin, Rank requiredRank, String...aliases)
	{
		Plugin = plugin;
		_requiredRank = requiredRank;
		_aliases = Arrays.asList(aliases);
	}
	
	public Collection<String> Aliases()
	{
		return _aliases;
	}

	public void SetAliasUsed(String alias)
	{
		AliasUsed = alias;
	}
	
	public Rank GetRequiredRank()
	{
		return _requiredRank;
	}
	
	public void SetCommandCenter(CommandCenter commandCenter)
	{
		CommandCenter = commandCenter;
	}
}
