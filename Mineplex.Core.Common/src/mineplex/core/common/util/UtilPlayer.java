package mineplex.core.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UtilPlayer
{
	public static void message(Entity client, LinkedList<String> messageList)
	{
		message(client, messageList, false);
	}

	public static void message(Entity client, String message)
	{
		message(client, message, false);
	}

	public static void message(Entity client, LinkedList<String> messageList, boolean wiki)
	{
		for (String curMessage : messageList)
		{
			message(client, curMessage, wiki);
		}
	}

	public static void message(Entity client, String message, boolean wiki)
	{
		if (client == null)
			return;

		if (!(client instanceof Player))
			return;

		/*
		if (wiki)
			message = UtilWiki.link(message);
			*/

		((Player)client).sendMessage(message);
	}

	public static Player searchExact(String name)
	{
		for (Player cur : UtilServer.getPlayers())
			if (cur.getName().equalsIgnoreCase(name))
				return cur;

		return null;
	}

	public static String searchCollection(Player caller, String player, Collection<String> coll, String collName, boolean inform) 
	{
		LinkedList<String> matchList = new LinkedList<String>();

		for (String cur : coll)
		{
			if (cur.equalsIgnoreCase(player))
				return cur;

			if (cur.toLowerCase().contains(player.toLowerCase()))
				matchList.add(cur);
		}

		//No / Non-Unique
		if (matchList.size() != 1)
		{
			if (!inform)
				return null;

			//Inform
			message(caller, F.main(collName + " Search", "" +
					C.mCount + matchList.size() +
					C.mBody + " matches for [" +
					C.mElem + player +
					C.mBody + "]."));

			if (matchList.size() > 0)
			{
				String matchString = "";
				for (String cur : matchList)
					matchString += cur + " ";

				message(caller, F.main(collName + " Search", "" +
						C.mBody + " Matches [" +
						C.mElem + matchString +
						C.mBody + "]."));
			}

			return null;
		}

		return matchList.get(0);
	}

	public static Player searchOnline(Player caller, String player, boolean inform) 
	{
		LinkedList<Player> matchList = new LinkedList<Player>();

		for (Player cur : UtilServer.getPlayers())
		{
			if (cur.getName().equalsIgnoreCase(player))
				return cur;

			if (cur.getName().toLowerCase().contains(player.toLowerCase()))
				matchList.add(cur);
		}

		//No / Non-Unique
		if (matchList.size() != 1)
		{
			if (!inform)
				return null;

			//Inform
			message(caller, F.main("Online Player Search", "" +
					C.mCount + matchList.size() +
					C.mBody + " matches for [" +
					C.mElem + player +
					C.mBody + "]."));

			if (matchList.size() > 0)
			{
				String matchString = "";
				for (Player cur : matchList)
					matchString += F.elem(cur.getName()) + ", ";
				if (matchString.length() > 1)
					matchString = matchString.substring(0 , matchString.length() - 2);

				message(caller, F.main("Online Player Search", "" +
						C.mBody + "Matches [" +
						C.mElem + matchString +
						C.mBody + "]."));
			}

			return null;
		}

		return matchList.get(0);
	}

	public static void searchOffline(List<String> matches, final Callback<String> callback, final Player caller, final String player, final boolean inform)
	{
		//No / Non-Unique
		if (matches.size() != 1)
		{
			if (!inform || !caller.isOnline())
			{
				callback.run(null);
				return;
			}

			//Inform
			message(caller, F.main("Offline Player Search", "" +
					C.mCount + matches.size() +
					C.mBody + " matches for [" +
					C.mElem + player +
					C.mBody + "]."));

			if (matches.size() > 0)
			{
				String matchString = "";
				for (String cur : matches)
					matchString += cur + " ";
				if (matchString.length() > 1)
					matchString = matchString.substring(0 , matchString.length() - 2);

				message(caller, F.main("Offline Player Search", "" +
						C.mBody + "Matches [" +
						C.mElem + matchString +
						C.mBody + "]."));
			}

			callback.run(null);
			return;
		}

		callback.run(matches.get(0));
	}

	public static LinkedList<Player> matchOnline(Player caller, String players, boolean inform)
	{
		LinkedList<Player> matchList = new LinkedList<Player>();

		String failList = "";

		for (String cur : players.split(","))
		{
			Player match = searchOnline(caller, cur, inform);

			if (match != null)
				matchList.add(match);

			else
				failList += cur + " " ;
		}

		if (inform && failList.length() > 0)
		{
			failList = failList.substring(0, failList.length() - 1);
			message(caller, F.main("Online Player(s) Search", "" +
					C.mBody + "Invalid [" +
					C.mElem + failList +
					C.mBody + "]."));
		}

		return matchList;
	}

	public static LinkedList<Player> getNearby(Location loc, double maxDist)
	{
		LinkedList<Player> nearbyMap = new LinkedList<Player>();

		for (Player cur : loc.getWorld().getPlayers())
		{
			if (cur.getGameMode() == GameMode.CREATIVE)
				continue;
			
			if (cur.isDead())
				continue;

			double dist = loc.toVector().subtract(cur.getLocation().toVector()).length();

			if (dist > maxDist)
				continue;

			for (int i=0 ; i<nearbyMap.size() ; i++)
			{
				if (dist < loc.toVector().subtract(nearbyMap.get(i).getLocation().toVector()).length())
				{
					nearbyMap.add(i, cur);
					break;
				}
			}

			if (!nearbyMap.contains(cur))
				nearbyMap.addLast(cur);
		}

		return nearbyMap;
	}
	
	public static Player getClosest(Location loc, Collection<Player> ignore) 
	{ 
		Player best = null;
		double bestDist = 0;
		
		for (Player cur : loc.getWorld().getPlayers())
		{
			if (cur.getGameMode() == GameMode.CREATIVE)
				continue;
			
			if (cur.isDead())
				continue;
			
			if (ignore != null && ignore.contains(cur))
				continue;

			double dist = UtilMath.offset(cur.getLocation(), loc);
			
			if (best == null || dist < bestDist)
			{
				best = cur;
				bestDist = dist;
			}
		}

		return best;
	}	

	public static void kick(Player player, String module, String message)
	{
		kick(player,module,message, true);
	}

	public static void kick(Player player, String module, String message, boolean log)
	{
		if (player == null)
			return;

		String out = ChatColor.RED + module + 
				ChatColor.WHITE + " - " + 
				ChatColor.YELLOW + message;
		player.kickPlayer(out);

		//Log
		if (log)
			System.out.println("Kicked Client [" + player.getName() + "] for [" + module + " - " + message + "]");
	}

	public static HashMap<Player, Double> getInRadius(Location loc, double dR) 
	{
		HashMap<Player, Double> players = new HashMap<Player, Double>();

		for (Player cur : loc.getWorld().getPlayers())
		{
			if (cur.getGameMode() == GameMode.CREATIVE)
				continue;

			double offset = UtilMath.offset(loc, cur.getLocation());

			if (offset < dR)
				players.put(cur, 1 - (offset/dR));
		}

		return players;
	}

	public static void health(Player player, double mod) 
	{
		if (player.isDead())
			return;

		double health = player.getHealth() + mod;

		if (health < 0)
			health = 0;

		if (health > 20)
			health = 20;

		player.setHealth(health);
	}

	public static void hunger(Player player, int mod) 
	{
		if (player.isDead())
			return;

		int hunger = player.getFoodLevel() + mod;

		if (hunger < 0)
			hunger = 0;

		if (hunger > 20)
			hunger = 20;

		player.setFoodLevel(hunger);
	}

	public static boolean isOnline(String name) 
	{
		return (searchExact(name) != null);
	}
	
	public static String safeNameLength(String name)
	{
		if (name.length() > 16)
			name = name.substring(0, 16);
		
		return name;
	}

	
	/*
	public void setListName(Player player, CoreClient client) 
	{
		StringBuilder playerNameBuilder = new StringBuilder();

    	String prefixChar = "*";
    	
    	if (client.NAC().IsUsing())							playerNameBuilder.append(ChatColor.GREEN + prefixChar);
    	else												playerNameBuilder.append(ChatColor.DARK_GRAY + prefixChar);
    		
    	if (client.Rank().Has(Rank.OWNER, false))			playerNameBuilder.append(ChatColor.AQUA + prefixChar + ChatColor.RED);
    	else if (client.Rank().Has(Rank.MODERATOR, false))	playerNameBuilder.append(ChatColor.AQUA + prefixChar + ChatColor.GOLD);
    	else if (client.Rank().Has(Rank.DIAMOND, false))	playerNameBuilder.append(ChatColor.AQUA + prefixChar + ChatColor.WHITE);
    	else if (client.Rank().Has(Rank.EMERALD, false))	playerNameBuilder.append(ChatColor.GREEN + prefixChar + ChatColor.WHITE);
    	else if (client.Donor().HasDonated())				playerNameBuilder.append(ChatColor.YELLOW + prefixChar + ChatColor.WHITE);
    	else												playerNameBuilder.append(ChatColor.DARK_GRAY + prefixChar + ChatColor.WHITE);

    	playerNameBuilder.append(player.getName());

    	String playerName = playerNameBuilder.toString();
    	
    	if (playerNameBuilder.length() > 16)
    	{
    		playerName = playerNameBuilder.substring(0, 16);
    	}
    	
    	player.setPlayerListName(playerName);
	}
	*/
}
