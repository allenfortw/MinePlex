package mineplex.core.message;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.chat.Chat;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.message.commands.*;
import mineplex.core.preferences.PreferencesManager;

public class MessageManager extends MiniClientPlugin<ClientMessage>
{
	private CoreClientManager _clientManager;
	private PreferencesManager _preferences;
	private ArrayList<String> _randomMessage;

	public MessageManager(JavaPlugin plugin, CoreClientManager clientManager, PreferencesManager preferences, Chat chat)
	{
		super("Message", plugin);

		_clientManager = clientManager;
		_preferences = preferences;
	}

	public void addCommands()
	{
		addCommand(new MessageCommand(this));
		addCommand(new ResendCommand(this));

		addCommand(new MessageAdminCommand(this));
		addCommand(new ResendAdminCommand(this));

		addCommand(new AdminCommand(this));
	}

	@Override
	protected ClientMessage AddPlayer(String player)
	{
		Set(player, new ClientMessage());
		return Get(player);
	}

	public boolean canMessage(Player from, Player to)
	{
		if (!canSenderMessageThem(from, to.getName()))
		{
			return false;
		}

		String canMessage = canReceiverMessageThem(from.getName(), to);

		if (canMessage != null)
		{
			from.sendMessage(canMessage);

			return false;
		}

		return true;
	}

	public String canReceiverMessageThem(String sender, Player target)
	{
		// If the receiver has turned off private messaging and the sender isn't a mod
		if (!_preferences.Get(target).PrivateMessaging)
		{
			return C.cPurple + target.getName() + " has private messaging disabled.";

		}

		return null;
	}

	public boolean canSenderMessageThem(Player sender, String target)
	{

		return true;
	}

	public void DoMessage(Player from, Player to, String message)
	{
		PrivateMessageEvent pmEvent = new PrivateMessageEvent(from, to, message);
		Bukkit.getServer().getPluginManager().callEvent(pmEvent);
		if (pmEvent.isCancelled())
			return;

		if (!canMessage(from, to))
		{
			return;
		}

		// My attempt at trying to mitigate some of the spam bots - Phinary
		// Triggers if they are whispering a new player
		if (!GetClientManager().Get(from).GetRank().Has(Rank.HELPER) && Get(from).LastTo != null
				&& !Get(from).LastTo.equalsIgnoreCase(to.getName()))
		{
			long delta = System.currentTimeMillis() - Get(from).LastToTime;

			if (Get(from).SpamCounter > 3 && delta < Get(from).SpamCounter * 1000)
			{
				from.sendMessage(F.main("Cooldown", "Try sending that message again in a few seconds"));
				Get(from).LastTo = to.getName();
				return;
			}
			else if (delta < 8000)
			{
				// Silently increment spam counter whenever delta is less than 8 seconds
				Get(from).SpamCounter++;
			}
		}

		// Inform
		UtilPlayer.message(from, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);

		// Save
		Get(from).LastTo = to.getName();
		Get(from).LastToTime = System.currentTimeMillis();

		// Chiss or defek7
		if (to.getName().equals("Chiss") || to.getName().equals("defek7") || to.getName().equals("Phinary") || to.getName().equals("fooify"))
		{
			UtilPlayer.message(from, C.cPurple + to.getName() + " is often AFK or minimized, due to plugin development.");
			UtilPlayer.message(from, C.cPurple + "Please be patient if he does not reply instantly.");
		}

		// Log
		// Logger().logChat("Private Message", from, to.getName(), message);

		// Sound
		from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
		to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2f, 2f);

		// Send
		UtilPlayer.message(to, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);
	}

	public void DoMessageAdmin(Player from, Player to, String message)
	{
		// Inform
		UtilPlayer.message(from, C.cPurple + "-> " + F.rank(_clientManager.Get(to).GetRank()) + " " + to.getName() + " "
				+ C.cPurple + message);

		// Inform Admins
		for (Player staff : UtilServer.getPlayers())
		{
			if (!to.equals(staff) && !from.equals(staff))
			{
				if (_clientManager.Get(staff).GetRank().Has(Rank.HELPER))
				{
					UtilPlayer.message(staff, F.rank(_clientManager.Get(from).GetRank()) + " " + from.getName() + C.cPurple
							+ " -> " + F.rank(_clientManager.Get(to).GetRank()) + " " + to.getName() + " " + C.cPurple + message);
				}
			}
		}

		// Save
		Get(from).LastAdminTo = to.getName();

		// Send
		UtilPlayer.message(to, C.cPurple + "<- " + F.rank(_clientManager.Get(from).GetRank()) + " " + from.getName() + " "
				+ C.cPurple + message);

		// Sound
		from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
		to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2f, 2f);

		// Log XXX
		// Logger().logChat("Staff Message", from, to.getName(), message);
	}

	// Module Functions
	@Override
	public void enable()
	{
		_randomMessage = new ArrayList<String>();
		_randomMessage.clear();
		_randomMessage.add("Hello, do you have any wild boars for purchase?");
		_randomMessage.add("There's a snake in my boot!");
		_randomMessage.add("Monk, I need a Monk!");
		_randomMessage.add("Hi, I'm from planet minecraft, op me plz dooooood!");
		_randomMessage.add("Somebody's poisoned the waterhole!");
		_randomMessage.add("MORE ORBZ MORE ORBZ MORE ORBZ MORE ORBZ!");
		_randomMessage.add("Chiss is a chiss and chiss chiss.");
		_randomMessage.add("*_*");
		_randomMessage.add("#swag");
		_randomMessage.add("Everything went better then I thought.");
		_randomMessage.add("HAVE A CHICKEN!");
		_randomMessage.add("follow me, i have xrays");
		_randomMessage.add("I'm making a java");
		_randomMessage.add("Do you talk to strangers?  I have candy if it helps.");
		_randomMessage.add("Solid 2.9/10");
		_randomMessage.add("close your eyes to sleep");
		_randomMessage.add("I crashed because my internet ran out.");
		_randomMessage.add("I saw morgan freeman on a breaking bad ad on a bus.");
		_randomMessage.add("Where is the volume control?");
		_randomMessage.add("I saw you playing on youtube with that guy and stuff.");
		_randomMessage.add("Your worms must be worse than useless.");
		_randomMessage.add("meow");
		_randomMessage.add("7");
		_randomMessage.add("Don't you wish your girlfriend was hot like me?");
		_randomMessage.add("how do you play mindcrafts?");
		_randomMessage.add("7 cats meow meow meow meow meow meow meow");
		_randomMessage.add("For King Jonalon!!!!!");
		_randomMessage.add("Do you like apples?");
		_randomMessage.add("I'm Happy Happy Happy.");
		_randomMessage.add("kthxbye");
		_randomMessage.add("i like pie.");
		_randomMessage.add("Do you play Clash of Clans?");
		_randomMessage.add("Mmm...Steak!");
		_randomMessage.add("Poop! Poop everywhere!");
		_randomMessage.add("I'm so forgetful. Like I was going to say somethin...wait what were we talking about?");
		_randomMessage.add("Mmm...Steak!");
	}

	public CoreClientManager GetClientManager()
	{
		return _clientManager;
	}

	public String GetRandomMessage()
	{
		if (_randomMessage.isEmpty())
			return "meow";

		return _randomMessage.get(UtilMath.r(_randomMessage.size()));
	}

	public ArrayList<String> GetRandomMessages()
	{
		return _randomMessage;
	}

	public void Help(Player caller)
	{
		Help(caller, null);
	}

	public void Help(Player caller, String message)
	{
		UtilPlayer.message(caller, F.main(_moduleName, ChatColor.RED + "Err...something went wrong?"));
	}

	public void sendMessage(final Player sender, final String target, final String message, final boolean isReply,
			final boolean adminMessage)
	{

		if (!adminMessage)
		{

		new BukkitRunnable()
		{
			final String newMessage = message;

			@Override
			public void run()
			{
				new BukkitRunnable()
				{

					@Override
					public void run()
					{
						sendMessage1(sender, target, newMessage, adminMessage, isReply);
					}

				}.runTask(getPlugin());
			}

		}.runTaskAsynchronously(getPlugin());
		}
	}

	private void sendMessage1(final Player sender, String target, String message, final boolean adminMessage, boolean isReply)
	{
		// We now have the friend object, if its not null. We are sending the message to that player.

		// Only notify player if friend is null and its not a reply
		Player to = UtilPlayer.searchOnline(sender, target, !adminMessage && !isReply);

		// If isn't admin message, friend is null and target is null. Return because location of receiver is unknown.
		if (!adminMessage && to == null)
		{
			// We need to notify them that the player they are replying to is gone
			if (isReply)
			{
				UtilPlayer.message(sender, F.main(getName(), F.name(target) + " is no longer online."));
			}

			return;
		}

		// If this is a message inside the server
			if (adminMessage)
			{
				DoMessageAdmin(sender, to, message);
			}
			else
			{
				DoMessage(sender, to, message);
			}
	}
}
