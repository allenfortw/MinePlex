package mineplex.bungee.PlayerCount;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadPlayerCountListCommand extends Command
{
	private PlayerCount _plugin;
	
	public ReloadPlayerCountListCommand(PlayerCount plugin)
	{
		super( "reloadplayercount", "bungeecord.command.reloadserverlist" );
		
		_plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] arg1)
	{
		_plugin.LoadBungeeServers();
		sender.sendMessage(ChatColor.BLUE + "PlayerCount>" + ChatColor.GRAY + " Reloaded player count server list.");
	}
}
