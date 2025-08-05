package nautilus.game.arcade.managers;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;

public class GameChatManager implements Listener
{
	ArcadeManager Manager;

	public GameChatManager(ArcadeManager manager)
	{
		Manager = manager; 

		Manager.getPluginManager().registerEvents(this, Manager.getPlugin());
	}  

	@EventHandler
	public void MeCancel(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/me "))
		{
			event.getPlayer().sendMessage(F.main("Mirror", "You can't see /me messages, are you a vampire?"));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void HandleChat(AsyncPlayerChatEvent event) 
	{
		if (event.isCancelled())
			return;

		Player sender = event.getPlayer();
		String senderName = sender.getDisplayName();
		
		//Dead Prefix 
		String dead = "";
		if (Manager.GetGame() != null)
			if (Manager.GetGame().GetTeam(sender) != null)
				if (!Manager.GetGame().IsAlive(sender))
					dead = C.cGray + "Dead ";

		//Base Format
		event.setFormat(dead + Manager.GetClients().Get(sender).GetRank().GetTag(true, true) + " "+ Manager.GetColor(sender) + senderName + " " + ChatColor.WHITE + "%2$s");

		//Public/Private (Not If Player Dead)
		if (Manager.GetGame() != null && Manager.GetGame().GetState() == GameState.Live)
		{
			boolean globalMessage = false;

			//Team
			GameTeam team = Manager.GetGame().GetTeam(sender);

			if (team != null) 
			{
				//Team Chat
				if (event.getMessage().charAt(0) == '@')
				{
					event.setMessage(event.getMessage().substring(1, event.getMessage().length()));
					event.setFormat(C.cWhite + C.Bold + "Team" + " " + dead + team.GetColor() + senderName + " " + C.cWhite + "%2$s");
				}
				//All Chat
				else
				{
					globalMessage = true;
					event.setFormat(dead + team.GetColor() + senderName + " " + C.cWhite + "%2$s");
				}
			}

			if (globalMessage)
				return;

			//Team Message Remove Recipient
			Iterator<Player> recipientIterator = event.getRecipients().iterator();

			while (recipientIterator.hasNext())
			{
				Player receiver = recipientIterator.next();

				if (!Manager.GetServerConfig().Tournament && Manager.GetClients().Get(receiver).GetRank().Has(Rank.MODERATOR))
					continue;
				
				GameTeam recTeam = Manager.GetGame().GetTeam(receiver);
				GameTeam sendTeam = Manager.GetGame().GetTeam(sender);
				
				if (recTeam == null || sendTeam == null)
				{
					continue;
				}

				if (!recTeam.equals(sendTeam))
					recipientIterator.remove();
			}
		}
	}
}
