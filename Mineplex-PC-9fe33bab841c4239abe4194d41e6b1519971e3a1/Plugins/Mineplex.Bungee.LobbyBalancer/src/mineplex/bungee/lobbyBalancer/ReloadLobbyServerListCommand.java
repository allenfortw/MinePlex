package mineplex.bungee.lobbyBalancer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadLobbyServerListCommand extends Command
{
	private LobbyBalancer _plugin;
	
	public ReloadLobbyServerListCommand(LobbyBalancer plugin)
	{
		super( "reloadlobbybalancer", "bungeecord.command.reloadserverlist" );
		
		_plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] arg1)
	{
		_plugin.loadLobbyServers();
		sender.sendMessage(ChatColor.BLUE + "LobbyBalancer>" + ChatColor.GRAY + " Reloaded lobby server list.");
	}
}
