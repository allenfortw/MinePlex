package mineplex.core.chat;

import mineplex.core.MiniPlugin;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.account.CoreClientManager;
import mineplex.core.chat.command.BroadcastCommand;
import mineplex.core.chat.command.SilenceCommand;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Chat extends MiniPlugin
{
	private CoreClientManager _clientManager;
	
	private long _silenced = 0;
	
	public Chat(JavaPlugin plugin, CoreClientManager clientManager)
	{
		super("Chat", plugin);
		
		_clientManager = clientManager;
	}

	@Override
	public void AddCommands() 
	{
		AddCommand(new SilenceCommand(this));
		AddCommand(new BroadcastCommand(this));
	}

	public void Silence(long duration, boolean inform)
	{
		//Set Silenced
		if (duration > 0)
			_silenced = System.currentTimeMillis() + duration;
		else
			_silenced = duration;
		
		if (!inform)
			return;
		
		//Announce
		if (duration == -1)
			UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time("Permanent") + "."));
		else if (duration == 0)
			UtilServer.broadcast(F.main("Chat", "Chat is no longer silenced."));
		else
			UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time(UtilTime.MakeStr(duration, 1)) + "."));
	}
	
	@EventHandler
	public void preventMe(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/me"))
		{
			event.getPlayer().sendMessage(F.main(GetName(), "Quite full of yourself aren't you?  Nobody cares."));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void SilenceUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		SilenceEnd();
	}
	
	public void SilenceEnd()
	{
		if (_silenced <= 0)
			return;
		
		if (System.currentTimeMillis() > _silenced)
			Silence(0, true);
	}
	
	public boolean SilenceCheck(Player player)
	{
		SilenceEnd();
		
		if (_silenced == 0)
			return false;
		
		if (_clientManager.Get(player).GetRank().Has(player, Rank.MODERATOR, false))
			return false;
		
		if (_silenced == -1)
			UtilPlayer.message(player, F.main(GetName(), "Chat is silenced permanently."));
		else
			UtilPlayer.message(player, F.main(GetName(), "Chat is silenced for " + F.time(UtilTime.MakeStr(_silenced - System.currentTimeMillis(), 1)) + "."));
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void HandleChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;
		
		Player sender = event.getPlayer();
		
		if (SilenceCheck(sender))
		{
			event.setCancelled(true);
			return;
		}
		else if (!Recharge.Instance.use(sender, "Chat Message", 500, false))
		{
			UtilPlayer.message(sender, F.main("Chat", "You are sending messages too fast."));
			event.setCancelled(true);
		}
	}

	public long Silenced()
	{
		return _silenced;
	}
}
