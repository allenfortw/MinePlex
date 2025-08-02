package mineplex.hub.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Party 
{
	private PartyManager Manager;
	
	private String _creator = null;
	
	private ArrayList<String> _players = new ArrayList<String>();
	private NautHashMap<String, Long> _invitee = new NautHashMap<String, Long>();

	private Scoreboard _scoreboard;
	private Objective _scoreboardObj;
	private ArrayList<String> _scoreboardLast = new ArrayList<String>();
	
	private long _partyOfflineTimer = -1;
	
	public Party(PartyManager manager)
	{
		Manager = manager;
		
		//Scoreboard
		_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		_scoreboardObj = _scoreboard.registerNewObjective("Party", "dummy");
		_scoreboardObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		_scoreboard.registerNewTeam(ChatColor.GREEN + "Members");
		
		//Scoreboard Ranks
		for (Rank rank : Rank.values())
		{
			if (rank != Rank.ALL)
				_scoreboard.registerNewTeam(rank.Name).setPrefix(rank.Color + C.Bold + rank.Name + ChatColor.RESET + " ");
			else
				_scoreboard.registerNewTeam(rank.Name).setPrefix("");
		}
		
		_scoreboard.registerNewTeam("Party").setPrefix(ChatColor.LIGHT_PURPLE + C.Bold + "Party" + ChatColor.RESET + " ");
		
		//Add Players
		for (Player player : Bukkit.getOnlinePlayers())
		{
			_scoreboard.getTeam(Manager.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
		}
		
		//Owners
		_scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Chiss"));
		_scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("defek7"));
		_scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Spu_"));
		_scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("sterling_"));
	}
	
	public void JoinParty(Player player)
	{
		//Add Leader
		if (_players.isEmpty())
		{
			_players.add(player.getName());

			UtilPlayer.message(player, F.main("Party", "You created a new Party."));
			
			_creator = player.getName();
		}
		else
		{
			_players.add(player.getName());
			_invitee.remove(player.getName());

			Announce(F.elem(player.getName()) + " has joined the party!");
		}
		
		_scoreboard.getTeam("Party").addPlayer(player);
	}

	public void InviteParty(Player player, boolean inviteeInParty)
	{
		_invitee.put(player.getName(), System.currentTimeMillis());

		//Decline
		if (_players.contains(player.getName()))
		{
			UtilPlayer.message(player, F.main("Party", F.name(player.getName()) + " is already in the Party."));
			player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1.5f);
		}

		//Announce
		Announce(F.name(player.getName()) + " has been invited to your Party.");

		//Inform
		UtilPlayer.message(player, F.main("Party", F.name(GetLeader()) + " invited you to their Party."));
		
		//Instruct
		if (inviteeInParty)
		{
			UtilPlayer.message(player, F.main("Party", "Type " + F.link("/party leave") + " then " + F.link("/party " + GetLeader()) + " to join."));
		}
		else
		{
			UtilPlayer.message(player, F.main("Party", "Type " + F.link("/party " + GetLeader()) + " to join."));
		}
			

		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
	}

	public void LeaveParty(Player player)
	{
		//Announce
		Announce(F.name(player.getName()) + " has left the Party.");

		boolean leader = player.equals(GetLeader());

		_players.remove(player.getName());
		
		//Set Scoreboard
		_scoreboard.getTeam(Manager.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
		
		if (leader && _players.size() > 0)
		{
			Announce("Party Leadership passed on to " + F.name(GetLeader()) + ".");
		}
	}
	
	public void KickParty(Player player)
	{
		//Announce
		Announce(F.name(player.getName()) + " was kicked from the Party.");
		
		_players.remove(player.getName());
	}

	public void PlayerJoin(Player player)
	{
		//Scoreboard
		if (_players.contains(player.getName()))
			_scoreboard.getTeam("Party").addPlayer(player);
		else
			_scoreboard.getTeam(Manager.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
			
		if (_creator.equals(player.getName()))
		{
			_players.remove(player.getName());
			_players.add(0, player.getName());
			
			Announce("Party Leadership returned to " + F.name(GetLeader()) + ".");
		}
	}

	//Shuffle Leader
	public void PlayerQuit(Player player)
	{
		if (player.getName().equals(GetLeader()))
		{
			_players.remove(player.getName());
			_players.add(1, player.getName());

			Announce("Party Leadership passed on to " + F.name(GetLeader()) + ".");
		}
	}

	public void Announce(String message)
	{
		for (String name : _players)
		{
			Player player = UtilPlayer.searchExact(name);
			UtilPlayer.message(player, F.main("Party", message));
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
		}
	}

	public void ExpireInvitees()
	{
		Iterator<String> inviteeIterator = _invitee.keySet().iterator();

		while (inviteeIterator.hasNext())
		{
			String name = inviteeIterator.next();
			Player invitee = UtilPlayer.searchExact(name);
			
			if (UtilTime.elapsed(_invitee.get(invitee.getName()), 60000))
			{
				Announce(F.name(invitee.getName()) + " did not respond to the Party ivnite.");
				inviteeIterator.remove();
			}
		}
	}

	public String GetLeader()
	{
		if (_players.isEmpty())
			return _creator;
		
		return _players.get(0);
	}

	public Collection<String> GetPlayers()
	{
		return _players;
	}
	
	public Collection<Player> GetPlayersOnline()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (String name : _players)
		{
			Player player = UtilPlayer.searchExact(name);
			if (player != null)
				players.add(player);
		}

		return players;
	}

	public Collection<String> GetInvitees()
	{
		return _invitee.keySet();
	}

	public void UpdateScoreboard()
	{
		_scoreboardObj.setDisplayName(GetLeader() + "'s Party");

		//Clear Past
		for (String pastLine : _scoreboardLast)
			_scoreboard.resetScores(Bukkit.getOfflinePlayer(pastLine));
		_scoreboardLast.clear();

		int i=16;

		//Add New
		for (int j=0 ; j<_players.size() ; j++)
		{
			String name = _players.get(j);
			Player player = UtilPlayer.searchExact(name);
			
			ChatColor col = ChatColor.GREEN;
			if (player == null)
				col = ChatColor.RED;
			
			String line = col + name;
			
			if (line.length() > 16)
				line = line.substring(0, 16);

			_scoreboardObj.getScore(Bukkit.getOfflinePlayer(line)).setScore(i);

			_scoreboardLast.add(line);

			i--;
		}

		//Add New
		for (String name : _invitee.keySet())
		{
			int time = 1 + (int) ((60000 - (System.currentTimeMillis() - _invitee.get(name)))/1000);
			
			String line = time + " " + ChatColor.GRAY + name;
			
			if (line.length() > 16)
				line = line.substring(0, 16);

			_scoreboardObj.getScore(Bukkit.getOfflinePlayer(line)).setScore(i);

			_scoreboardLast.add(line);

			i--;
		}
		
		//Set Scoreboard
		for (String name : _players)
		{
			Player player = UtilPlayer.searchExact(name);
			
			if (player != null)
			{
				if (!player.getScoreboard().equals(_scoreboard))
				{
					player.setScoreboard(_scoreboard);
				}		
			}
		}
	}

	public boolean IsDead() 
	{
		if (_players.size() == 0)
			return true;
		
		if (_players.size() == 1 && _invitee.size() == 0)
			return true;
		
		int online = 0;
		for (String name : _players)
		{
			Player player = UtilPlayer.searchExact(name);
			if (player != null)
				online++;
		}
			

		//One or Less Members Online - Expirey Countdown
		if (online <= 1)
		{
			if (_partyOfflineTimer == -1)
			{
				_partyOfflineTimer = System.currentTimeMillis();
			}
			else
			{
				if (UtilTime.elapsed(_partyOfflineTimer, 3600000))	// 1 hour
				{
					return true;
				}
			}
		}
			
		return false;
	}
}
