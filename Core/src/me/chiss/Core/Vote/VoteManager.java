package me.chiss.Core.Vote;


import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.event.GetClientEvent;
import mineplex.core.server.event.PlayerVoteEvent;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;

public class VoteManager extends MiniPlugin
{
	public VoteManager(JavaPlugin plugin)
	{
		super("Vote", plugin);
	}
	
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVote(PlayerVoteEvent event)
    {
    	GetClientEvent clientEvent = new GetClientEvent(event.GetPlayerName());
    	GetPluginManager().callEvent(clientEvent);
    	
    	CoreClient client = clientEvent.GetClient();
    	
		if (client != null)
		{
			client.Donor().AddPoints(event.GetPointsReceived());
		
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().sendMessage(C.cDGreen + "           Thanks for voting!");
    		client.GetPlayer().sendMessage(C.cDGreen + "       You received " + ChatColor.YELLOW + event.GetPointsReceived() + C.cDGreen + " points! ");
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().playSound(client.GetPlayer().getLocation(), Sound.LEVEL_UP, .3f, 1f);
    		    		
    		for (Player player : GetPlugin().getServer().getOnlinePlayers())
    		{
    			if (player ==  client.GetPlayer())
    				continue;
    			
    			player.sendMessage(F.main(GetName(), ChatColor.YELLOW + event.GetPlayerName() + ChatColor.GRAY + " voted at bettermc.com/Vote for " + ChatColor.YELLOW + event.GetPointsReceived() + C.cGray + " points! "));
    		}

    		client.GetPlayer().updateInventory();
		}
    }
}
