package nautilus.game.arcade.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GameTeam 
{
	public enum PlayerState
	{
		IN("In", ChatColor.GREEN),
		OUT("Out", ChatColor.RED);

		private String name;
		private ChatColor color;

		private PlayerState(String name, ChatColor color) 
		{
			this.name = name;
			this.color = color;
		}

		public String GetName()
		{
			return name;
		}

		public ChatColor GetColor()
		{
			return color;
		}
	}

	private String _name;
	private ChatColor _color;

	private HashMap<Player, PlayerState> _players = new HashMap<Player, PlayerState>();

	private ArrayList<Location> _spawns;

	private Creature _teamEntity = null;

	private HashSet<Kit> _kitRestrict = new HashSet<Kit>();
	
	private int _spawnDistance = 0;

	public GameTeam(String name, ChatColor color, ArrayList<Location> spawns)
	{
		_name = name;
		_color = color;
		_spawns = spawns;
	}

	public String GetName()
	{
		return _name;
	}

	public ChatColor GetColor()
	{
		return _color;
	}

	public ArrayList<Location> GetSpawns()
	{
		return _spawns;
	}

	public Location GetSpawn() 
	{	
		ArrayList<Location> valid = new ArrayList<Location>();
		
		Location best = null;
		double bestDist = 0;
		
		for (Location loc : _spawns)
		{
			double closestPlayer = -1;
			
			for (Player player : GetPlayers(true))
			{
				double playerDist = UtilMath.offset(player.getLocation(), loc);
				
				if (closestPlayer == -1 || playerDist < closestPlayer)
					closestPlayer = playerDist;
			}
			
			if (best == null || closestPlayer > bestDist)
			{
				best = loc;
				bestDist = closestPlayer;
			}
			
			if (closestPlayer > _spawnDistance)
			{
				valid.add(loc);
			}
		}
		
		if (valid.size() > 0)
			valid.get(UtilMath.r(valid.size()));
		
		if (best != null)
			return best;
		
		return _spawns.get(UtilMath.r(_spawns.size()));
	}

	public void AddPlayer(Player player)
	{
		_players.put(player, PlayerState.IN);

		UtilPlayer.message(player, F.main("Team",  _color + C.Bold + "You joined " + _name + " Team") + ".");

		for (Player other : UtilServer.getPlayers())
		{
			if (other.equals(player))
				continue;

			other.hidePlayer(player);
			other.showPlayer(player);
		}
	}

	public void RemovePlayer(Player player)
	{
		_players.remove(player);
	}

	public boolean HasPlayer(Player player)
	{
		return _players.containsKey(player);
	}
	
	public boolean HasPlayer(String name, boolean alive)
	{
		for (Player player : _players.keySet())
			if (player.getName().equals(name))
				if (!alive || (alive && _players.get(player) == PlayerState.IN))
					return true;
		
		return false;
	}

	public int GetSize()
	{
		return _players.size();
	}

	public void SetPlayerState(Player player, PlayerState state) 
	{
		_players.put(player, state);
	}

	public boolean IsTeamAlive()
	{
		for (PlayerState state : _players.values())
			if (state == PlayerState.IN)
				return true;

		return false;
	}

	public ArrayList<Player> GetPlayers(boolean playerIn)
	{
		ArrayList<Player> alive = new ArrayList<Player>();

		for (Player player : _players.keySet())
			if (!playerIn || _players.get(player) == PlayerState.IN )
				alive.add(player);

		return alive;
	}

	public String GetFormattedName() 
	{
		return GetColor() + "§l" + GetName();
	}

	public void SpawnTeleport() 
	{
		for (Player player : GetPlayers(true))
		{
			player.leaveVehicle();
			player.eject();
			player.teleport(GetSpawn());
		}
	}

	public HashSet<Kit> GetRestrictedKits()
	{
		return _kitRestrict;
	}

	public boolean KitAllowed(Kit kit)
	{
		if (kit.GetAvailability() == KitAvailability.Null)
			return false;
		
		return !_kitRestrict.contains(kit);
	}

	public boolean IsAlive(Player player)
	{
		if (!_players.containsKey(player))
			return false;

		return _players.get(player) == PlayerState.IN;
	}

	public void SetColor(ChatColor color) 
	{
		_color = color;
	}

	public void SetName(String name)
	{
		_name = name;
	}

	public byte GetColorData()
	{
		if (GetColor() == ChatColor.WHITE)			return (byte)0;
		if (GetColor() == ChatColor.GOLD)			return (byte)1;
		if (GetColor() == ChatColor.LIGHT_PURPLE)	return (byte)2;
		if (GetColor() == ChatColor.AQUA)			return (byte)3;
		if (GetColor() == ChatColor.YELLOW)			return (byte)4;
		if (GetColor() == ChatColor.GREEN)			return (byte)5;
		//if (GetColor() == ChatColor.PINK)			return (byte)6;
		if (GetColor() == ChatColor.DARK_GRAY)		return (byte)7;
		if (GetColor() == ChatColor.GRAY)			return (byte)8;
		if (GetColor() == ChatColor.DARK_AQUA)		return (byte)9;
		if (GetColor() == ChatColor.DARK_PURPLE)	return (byte)10;
		if (GetColor() == ChatColor.BLUE)			return (byte)11;
		if (GetColor() == ChatColor.DARK_BLUE)		return (byte)11;
		//if (GetColor() == ChatColor.BROWN)		return (byte)12;
		if (GetColor() == ChatColor.DARK_GREEN)		return (byte)13;
		if (GetColor() == ChatColor.RED)			return (byte)14;
		else										return (byte)15;
	}

	public void SetTeamEntity(Creature ent) 
	{
		_teamEntity = ent;
	}

	public LivingEntity GetTeamEntity()
	{
		return _teamEntity;
	}

	public void SetSpawns(ArrayList<Location> spawns) 
	{
		_spawns = spawns;
	}
	
	public void SetSpawnRequirement(int value)
	{
		_spawnDistance = value;
	}

	public void ReplaceReference(Player player) 
	{
		Iterator<Player> playerIterator = _players.keySet().iterator();
		PlayerState state = null;
		
		while (playerIterator.hasNext())
		{
			Player cur = playerIterator.next();
			
			if (!cur.getName().equals(player.getName()))
				continue;
			
			state = _players.get(cur);
			playerIterator.remove();
		}
		
		if (state != null)
		{
			_players.put(player, state);
		}
	}
}
