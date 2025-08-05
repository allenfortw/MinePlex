package mineplex.core.message;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.message.Commands.*;

public class MessageManager extends MiniClientPlugin<ClientMessage>
{
	private LinkedList<String> _randomMessage;
	
	private CoreClientManager _clientManager;

	public MessageManager(JavaPlugin plugin, CoreClientManager clientManager) 
	{
		super("Message", plugin);
		
		_clientManager = clientManager;
	}

	//Module Functions
	@Override
	public void Enable() 
	{
		_randomMessage = new LinkedList<String>();
		_randomMessage.clear();
		_randomMessage.add("Hello, do you have any wild boars for purchase?");
		_randomMessage.add("There's a snake in my boot!");
		_randomMessage.add("Somebody's poisoned the waterhole!");
		_randomMessage.add("MORE ORBZ MORE ORBZ MORE ORBZ MORE ORBZ!");
		_randomMessage.add("Senwom is bad. Kill it with Wood Pickaxes!");
		_randomMessage.add("grrgo_ does the vote rolls");
		_randomMessage.add("Chiss is a chiss and chiss chiss.");
		_randomMessage.add("*_*");
		_randomMessage.add("#swag");
		_randomMessage.add("Not on my hand.");
		_randomMessage.add("Everything went better then I thought.");
		_randomMessage.add("HAVE A CHICKEN!");
		_randomMessage.add("follow me, i have xrays");
		_randomMessage.add("I'm making a java");
		_randomMessage.add("close your eyes to sleep");
		_randomMessage.add("I crashed because my internet ran out.");
		_randomMessage.add("I saw morgan freeman on a breaking bad ad on a bus.");
		_randomMessage.add("Where is the volume control?");
		_randomMessage.add("You're worms must be worse than useless.");
		_randomMessage.add("meow");
		_randomMessage.add("7");
		_randomMessage.add("7 cats meow meow meow meow meow meow meow");
		_randomMessage.add("I'm half bi, half straight");
	}

	
	public void AddCommands() 
	{ 
		AddCommand(new MessageCommand(this));
		AddCommand(new ResendCommand(this));
		
		AddCommand(new MessageAdminCommand(this));
		AddCommand(new ResendAdminCommand(this));
		
		
		AddCommand(new AdminCommand(this));
	}
	
	public void Help(Player caller, String message)
	{
		UtilPlayer.message(caller, F.main(_moduleName, "Commands List:"));
		UtilPlayer.message(caller, F.help("/npc add <radius> <name>", "Right click mob to attach npc.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/npc del ", "Right click npc to delete", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/npc clear", "Removes all npcs", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/npc home", " Teleport npcs to home locations.", Rank.OWNER));
		UtilPlayer.message(caller, F.help("/npc reattach", "Attempt to reattach npcs to entities.", Rank.OWNER));
		
		if (message != null)
			UtilPlayer.message(caller, F.main(_moduleName, ChatColor.RED + message));
	}
	
	public void Help(Player caller)
	{
		Help(caller, null);
	}
	/*
	@Override
	public void commands() 
	{
		AddCommand("m");
		AddCommand("msg");
		AddCommand("message");
		AddCommand("tell");
		AddCommand("t");
		AddCommand("r");
		AddCommand("a");
		AddCommand("ma");
		AddCommand("ra");
	}

/* XXX Incorporate this
		PunishChatEvent event = new PunishChatEvent(caller);
		
		GetPlugin().getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return;
*/

	public void DoMessage(Player from, Player to, String message)
	{
		//Inform
		UtilPlayer.message(from, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);

		//Save
		Get(from).LastTo = to.getName();

		//Chiss
		if (to.getName().equals("Chiss"))
		{
			UtilPlayer.message(from, C.cPurple + "Chiss is often AFK or minimized, due to plugin development.");
			UtilPlayer.message(from, C.cPurple + "Please be patient if he does not reply instantly.");
		}

		//Defek
		if (to.getName().equals("defek7"))
		{
			UtilPlayer.message(from, C.cPurple + "defek7 is often AFK or minimized, due to plugin development.");
			UtilPlayer.message(from, C.cPurple + "Please be patient if he does not reply instantly.");
		}

		//Log
		//Logger().logChat("Private Message", from, to.getName(), message);

		//Ignored XXX
		//if (Get(to).Ignore().IsIgnored(from.getName()))
		//	return;
		
		//Sound
		from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
		to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2f, 2f);

		//Send
		UtilPlayer.message(to, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);
	}

	public void DoMessageAdmin(Player from, Player to, String message)
	{
		//Inform
		UtilPlayer.message(from, C.cPurple + "-> " + F.rank(_clientManager.Get(to).GetRank()) + " " +  to.getName() + " " + C.cPurple + message);

		//Inform Admins
		for (Player staff : UtilServer.getPlayers())
			if (!to.equals(staff))
				if (!from.equals(staff))
					if (_clientManager.Get(staff).GetRank().Has(Rank.HELPER))
						UtilPlayer.message(staff, F.rank(_clientManager.Get(from).GetRank()) + " " + from.getName() + 
								C.cPurple + " -> " + F.rank(_clientManager.Get(to).GetRank()) + " " + to.getName() + " " + C.cPurple + message);

		//Save
		Get(from).LastAdminTo = to.getName();

		//Send
		UtilPlayer.message(to, C.cPurple + "<- " + F.rank(_clientManager.Get(from).GetRank()) + " " + from.getName() + " " + C.cPurple + message);

		//Sound
		from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
		to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2f, 2f);
		
		//Log XXX
		//Logger().logChat("Staff Message", from, to.getName(), message);
	}

	@Override
	protected ClientMessage AddPlayer(String player)
	{
		Set(player, new ClientMessage());
		return Get(player);
	}

	public LinkedList<String> GetRandomMessages() 
	{
		return _randomMessage;
	}

	public String GetRandomMessage() 
	{
		if (_randomMessage.isEmpty())
			return "meow";
		
		return _randomMessage.get(UtilMath.r(_randomMessage.size()));
	}

	public CoreClientManager GetClientManager()
	{
		return _clientManager;
	}
}
