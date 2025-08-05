package mineplex.bungee.dynamicServer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadServerListCommand extends Command
{
	private DynamicServer _plugin;
	
	public ReloadServerListCommand(DynamicServer plugin)
	{
		super( "reloaddynamicserver", "bungeecord.command.reloadserverlist" );
		
		_plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] arg1)
	{
		_plugin.LoadServers();
		sender.sendMessage(ChatColor.BLUE + "DynamicServer>" + ChatColor.GRAY + " Reloaded server list.");
	}
}
