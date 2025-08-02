package nautilus.game.arcade.managers;

import java.util.Iterator;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class GameChatManager implements Listener
{
	ArcadeManager Manager;
	
	public GameChatManager(ArcadeManager manager)
	{
		Manager = manager; 
		
		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}
	
	@EventHandler
	public void MeCancel(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/me"))
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

		//Dead Prefix
		String dead = "";
		if (Manager.GetGame() != null)
			if (Manager.GetGame().GetTeam(sender) != null)
				if (!Manager.GetGame().IsAlive(sender))
					dead = C.cGray + "Dead ";

		Rank rank = Manager.GetClients().Get(sender).GetRank();
		boolean ownsUltra = false;

		if (Manager.GetGame() != null)
			ownsUltra= Manager.GetDonation().Get(sender.getName()).OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA");

		//Rank Prefix
		String rankStr = "";
		if  (rank != Rank.ALL)
			rankStr = rank.Color + C.Bold + rank.Name.toUpperCase() + " ";

		if (ownsUltra && !rank.Has(Rank.ULTRA))
			rankStr = Rank.ULTRA.Color + C.Bold + Rank.ULTRA.Name.toUpperCase() + " ";

		//Base Format
		event.setFormat(dead + rankStr + Manager.GetColor(sender) + "%1$s " + ChatColor.WHITE + "%2$s");

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
					event.setFormat(C.cWhite + C.Bold + "Team" + " " + dead + rankStr + team.GetColor() + "%1$s " + C.cWhite + "%2$s");
				}
				//All Chat
				else
				{
					globalMessage = true;
					event.setFormat(dead + rankStr + team.GetColor() + "%1$s " + C.cWhite + "%2$s");
				}
			}

			if (globalMessage)
				return;

			//Team Message Remove Recipient
			Iterator<Player> recipientIterator = event.getRecipients().iterator();

			while (recipientIterator.hasNext())
			{
				Player receiver = recipientIterator.next();

				if (Manager.GetClients().Get(receiver).GetRank().Has(Rank.MODERATOR))
					continue;

				if (Manager.GetGame().GetTeam(receiver) != null && Manager.GetGame().GetTeam(sender) != Manager.GetGame().GetTeam(receiver))
					recipientIterator.remove();
			}
		}
	}
}
