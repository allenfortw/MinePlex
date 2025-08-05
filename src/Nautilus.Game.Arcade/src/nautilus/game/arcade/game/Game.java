package nautilus.game.arcade.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.managers.GameLobbyManager;
import nautilus.game.arcade.world.WorldData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public abstract class Game implements Listener
{
	public enum GameState
	{
		Loading,
		Recruit,
		Prepare,
		Live,
		End,
		Dead
	}

	public ArcadeManager Manager;

	//Game
	private GameType _gameType;
	private String[] _gameDesc;

	//Map
	private ArrayList<String> _files;

	//State
	private GameState _gameState = GameState.Loading;
	private long _gameStateTime = System.currentTimeMillis();

	private int _countdown = -1;
	private boolean _countdownForce = false;

	private int _playerCount = 0;

	private String _customWinLine = "";

	//Kits
	private Kit[] _kits;

	//Teams	
	private ArrayList<GameTeam> _teamList = new ArrayList<GameTeam>();

	//Player Preferences
	protected NautHashMap<Player, Kit> _playerKit	= new NautHashMap<Player, Kit>();
	private NautHashMap<GameTeam, ArrayList<Player>> _teamPreference = new NautHashMap<GameTeam, ArrayList<Player>>();
	private NautHashMap<Player, HashMap<String,GemData>> _gemCount	= new NautHashMap<Player, HashMap<String,GemData>>(); 

	//Player Location Store
	private NautHashMap<String, Location> _playerLocationStore = new NautHashMap<String, Location>();

	//Scoreboard
	private Scoreboard _scoreboard;
	private Objective _sideObjective;

	//Loaded from Map Config
	public WorldData WorldData = null;

	//Gameplay Flags
	public boolean Damage = true;
	public boolean DamagePvP = true;
	public boolean DamagePvE = true;
	public boolean DamageEvP = true;
	public boolean DamageSelf = true;
	public boolean DamageTeamSelf = false;
	public boolean DamageTeamOther = true;	

	public boolean BlockBreak = false;
	public HashSet<Integer> BlockBreakAllow = new HashSet<Integer>();
	public HashSet<Integer> BlockBreakDeny = new HashSet<Integer>();

	public boolean BlockPlace = false;
	public HashSet<Integer> BlockPlaceAllow = new HashSet<Integer>();
	public HashSet<Integer> BlockPlaceDeny = new HashSet<Integer>();

	public boolean ItemPickup = false;
	public HashSet<Integer> ItemPickupAllow = new HashSet<Integer>();
	public HashSet<Integer> ItemPickupDeny = new HashSet<Integer>();

	public boolean ItemDrop = false;
	public HashSet<Integer> ItemDropAllow = new HashSet<Integer>();
	public HashSet<Integer> ItemDropDeny = new HashSet<Integer>();

	public boolean InventoryOpen = false;

	public boolean PrivateBlocks = false;

	public boolean DeathOut = true;
	public boolean DeathDropItems = true;
	public boolean DeathMessages = true;

	public boolean QuitOut = true;

	public boolean IdleKick = true;

	public boolean CreatureAllow = false;
	public boolean CreatureAllowOverride = false;

	public int WorldTimeSet = -1;
	public boolean WorldWeatherEnabled = false;
	public int WorldHeightLimit = 0;
	public int WorldWaterDamage = 0;

	public int HungerSet = -1;
	public int HealthSet = -1;

	public int SpawnDistanceRequirement = 1;

	public boolean PrepareFreeze = true;

	public boolean RepairWeapons = true;

	public boolean AnnounceStay = true;
	public boolean AnnounceJoinQuit = true;
	public boolean AnnounceSilence = true;

	public boolean DisplayLobbySide = true;
	
	public boolean AutoStart = true;

	//Addons
	public boolean CompassEnabled = false;
	public boolean SoupEnabled = true;

	//Gameplay Data
	public HashMap<Location, Player> PrivateBlockMap = new HashMap<Location, Player>();
	public HashMap<String, Integer> PrivateBlockCount = new HashMap<String, Integer>();

	public Location SpectatorSpawn = null;

	public boolean FirstKill = true;

	public String Winner = "Nobody";
	public GameTeam WinnerTeam = null;

	public Game(ArcadeManager manager, GameType gameType, Kit[] kits, String[] gameDesc)
	{
		Manager = manager;

		//Game
		_gameType = gameType;
		_gameDesc = gameDesc;

		//Kits
		_kits = kits;

		//Scoreboard
		_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		_sideObjective = _scoreboard.registerNewObjective("Obj"+UtilMath.r(999999999), "dummy");
		_sideObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		_sideObjective.setDisplayName(C.Bold + GetName());

		//Map
		_files = Manager.LoadFiles(GetName());
		WorldData = new WorldData(this);

		System.out.println("Loading " + GetName() + "...");
	}

	public ArrayList<String> GetFiles()
	{
		return _files;
	}

	public String GetName()
	{
		return _gameType.GetName();
	}

	public GameType GetType() 
	{
		return _gameType;
	}

	public String[] GetDesc()
	{
		return _gameDesc;
	}

	public void SetCustomWinLine(String line)
	{
		_customWinLine = line;
	}

	public Scoreboard GetScoreboard()
	{
		return _scoreboard;
	}

	public Objective GetObjectiveSide()
	{
		return _sideObjective;
	}

	public ArrayList<GameTeam> GetTeamList()
	{
		return _teamList;
	}

	public int GetCountdown()
	{
		return _countdown;
	}

	public void SetCountdown(int time)
	{
		_countdown = time;
	}

	public boolean GetCountdownForce()
	{
		return _countdownForce;
	}

	public void SetCountdownForce(boolean value)
	{
		_countdownForce = value;
	}

	public int GetPlayerCountAtStart()
	{
		return _playerCount;
	}

	public void SetPlayerCountAtStart(int count)
	{
		_playerCount = count;
	}

	public NautHashMap<GameTeam, ArrayList<Player>> GetTeamPreferences()
	{
		return _teamPreference;
	}

	public NautHashMap<Player, Kit> GetPlayerKits()
	{
		return _playerKit;
	}

	public NautHashMap<Player, HashMap<String,GemData>> GetPlayerGems()
	{
		return _gemCount;
	}

	public NautHashMap<String, Location> GetLocationStore()
	{
		return _playerLocationStore;
	}

	public GameState GetState()
	{
		return _gameState;
	}

	public void SetState(GameState state) 
	{
		_gameState = state;
		_gameStateTime = System.currentTimeMillis();

		for (Player player : UtilServer.getPlayers())
			player.leaveVehicle();

		//Event
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(this, state);
		UtilServer.getServer().getPluginManager().callEvent(stateEvent);

		System.out.println(GetName() + " state set to " + state.toString());
	}

	public long GetStateTime()
	{
		return _gameStateTime;
	}

	public boolean InProgress()
	{
		return GetState() == GameState.Prepare || GetState() == GameState.Live;
	}

	public boolean IsLive()
	{
		return _gameState == GameState.Live;
	}

	public void AddTeam(GameTeam team)
	{
		//Add
		GetTeamList().add(team);

		//Set Spawn Data
		team.SetSpawnRequirement(this.SpawnDistanceRequirement);



		System.out.println("Created Team: " + team.GetName());
	}

	public boolean HasTeam(GameTeam team) 
	{
		for (GameTeam cur : GetTeamList())
			if (cur.equals(team))
				return true;

		return false;
	}

	public void CreateScoreboardTeams()
	{
		System.out.println("Creating Scoreboard Teams.");

		//Base Groups
		for (Rank rank : Rank.values())
		{
			//Spectator
			if (rank == Rank.ALL)
			{
				_scoreboard.registerNewTeam(rank.Name + "SPEC").setPrefix(ChatColor.GRAY + "");
			}
			else
			{
				_scoreboard.registerNewTeam(rank.Name + "SPEC").setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + ChatColor.GRAY);
			}
		}

		//Team Groups
		for (GameTeam team : GetTeamList())
		{
			for (Rank rank : Rank.values())
			{
				if (rank == Rank.ALL)
				{
					_scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(team.GetColor() + "");
				}
				else
				{
					_scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + team.GetColor());
				}
			}	
		}
	}

	public void RestrictKits() 
	{
		//Null Default
	}

	public void RegisterKits() 
	{
		for (Kit kit : _kits)
		{
			UtilServer.getServer().getPluginManager().registerEvents(kit, Manager.GetPlugin());

			for (Perk perk : kit.GetPerks())
				UtilServer.getServer().getPluginManager().registerEvents(perk, Manager.GetPlugin());
		}

	}

	public void DeregisterKits() 
	{
		for (Kit kit : _kits)
		{
			HandlerList.unregisterAll(kit);

			for (Perk perk : kit.GetPerks())
				HandlerList.unregisterAll(perk);
		}	
	}

	public void ParseData()
	{
		//Nothing by default,
		//Use this to parse in extra location data from maps
	}	



	public void SetPlayerTeam(Player player, GameTeam team)
	{
		//Clean Old Team
		GameTeam pastTeam = this.GetTeam(player);
		if (pastTeam != null)
		{
			pastTeam.RemovePlayer(player);
		}

		team.AddPlayer(player);

		//Ensure Valid Kit
		ValidateKit(player, team);

		//Game Scoreboard
		SetPlayerScoreboardTeam(player, team.GetName().toUpperCase());

		//Lobby Scoreboard
		Manager.GetLobby().AddPlayerToScoreboards(player, team.GetName().toUpperCase());
	}

	public void SetPlayerScoreboardTeam(Player player, String teamName) 
	{
		for (Team team : GetScoreboard().getTeams())
			team.removePlayer(player);

		if (teamName == null)
			teamName = "";

		GetScoreboard().getTeam(Manager.GetClients().Get(player).GetRank().Name + teamName).addPlayer(player);
	}

	public GameTeam ChooseTeam(Player player) 
	{
		GameTeam team = null;

		//Random Team
		for (int i=0 ; i<_teamList.size() ; i++)
		{
			if (team == null || _teamList.get(i).GetSize() < team.GetSize())
			{
				team = _teamList.get(i);
			}
		}

		return team;
	}

	public double GetKillsGems(Player killer, Player killed, boolean assist)
	{
		if (!DeathOut)
		{
			return 0.5;
		}

		if (!assist)
		{
			return 4;
		}
		else
		{
			return 1;
		}
	}

	public HashMap<String, GemData> GetGems(Player player)
	{
		if (!_gemCount.containsKey(player))
			_gemCount.put(player, new HashMap<String, GemData>());

		return _gemCount.get(player);
	}

	public void AddGems(Player player, double gems, String reason, boolean countAmount)
	{
		if (!countAmount && gems < 1)
			gems = 1;

		if (GetGems(player).containsKey(reason))	
		{
			GetGems(player).get(reason).AddGems(gems);
		}
		else										
		{
			GetGems(player).put(reason, new GemData(gems, countAmount));
		}
	}

	public void ValidateKit(Player player, GameTeam team)
	{
		//Kit
		if (GetKit(player) == null || !team.KitAllowed(GetKit(player)))
		{
			for (Kit kit : _kits)
			{
				if (kit.GetAvailability() == KitAvailability.Hide ||
						kit.GetAvailability() == KitAvailability.Null)
					continue;

				if (team.KitAllowed(kit))
				{
					SetKit(player, kit, false);
					break;
				}
			}
		}
	}

	public void SetKit(Player player, Kit kit, boolean announce) 
	{
		GameTeam team = GetTeam(player);
		if (team != null)
		{
			if (!team.KitAllowed(kit))
			{
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 0.5f);
				UtilPlayer.message(player, F.main("Kit", F.elem(team.GetFormattedName()) + " cannot use " + F.elem(kit.GetFormattedName() + " Kit") + "."));
				return;
			}
		}

		_playerKit.put(player, kit);

		if (announce)
		{
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2f, 1f);
			UtilPlayer.message(player, F.main("Kit", "You equipped " + F.elem(kit.GetFormattedName() + " Kit") + "."));
		}
	}

	public Kit GetKit(Player player) 
	{
		return _playerKit.get(player);
	}

	public Kit[] GetKits() 
	{
		return _kits;
	}

	public boolean HasKit(Kit kit) 
	{
		for (Kit cur : GetKits())
			if (cur.equals(kit))
				return true;

		return false;
	}

	public boolean HasKit(Player player, Kit kit)
	{
		if (GetKit(player) == null)
			return false;

		return GetKit(player).equals(kit);
	}

	public boolean SetPlayerState(Player player, PlayerState state)
	{
		GetScoreboard().resetScores(player);

		GameTeam team = GetTeam(player);

		if (team == null)
			return false;

		team.SetPlayerState(player, state);

		//Event
		PlayerStateChangeEvent playerStateEvent = new PlayerStateChangeEvent(this, player, PlayerState.OUT);
		UtilServer.getServer().getPluginManager().callEvent(playerStateEvent);			

		return true;
	}

	public abstract void EndCheck();

	public void RespawnPlayer(final Player player)
	{
		player.eject();
		player.teleport(GetTeam(player).GetSpawn());

		Manager.Clear(player);

		//Re-Give Kit
		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				GetKit(player).ApplyKit(player);
			} 
		}, 0);
	}

	public boolean IsPlaying(Player player)
	{
		return GetTeam(player) != null;
	}

	public boolean IsAlive(Player player)
	{
		GameTeam team = GetTeam(player);

		if (team == null)
			return false;

		return team.IsAlive(player);
	}

	public ArrayList<Player> GetPlayers(boolean aliveOnly) 
	{
		ArrayList<Player> players = new ArrayList<Player>();

		for (GameTeam team : _teamList)
			players.addAll(team.GetPlayers(aliveOnly));

		return players;
	}

	public GameTeam GetTeam(Player player) 
	{
		for (GameTeam team : _teamList)
			if (team.HasPlayer(player))
				return team;

		return null;
	}

	public GameTeam GetTeam(ChatColor color) 
	{
		for (GameTeam team : _teamList)
			if (team.GetColor() == color)
				return team;

		return null;
	}

	public Location GetSpectatorLocation()
	{
		if (SpectatorSpawn != null)
			return SpectatorSpawn;

		Vector vec = new Vector(0,0,0);
		double count = 0;

		for (GameTeam team : this.GetTeamList())
		{
			for (Location spawn : team.GetSpawns())
			{				
				count++;
				vec.add(spawn.toVector());
			}
		}

		SpectatorSpawn = new Location(this.WorldData.World, 0,0,0);

		vec.multiply(1d/count);

		SpectatorSpawn.setX(vec.getX());
		SpectatorSpawn.setY(vec.getY());
		SpectatorSpawn.setZ(vec.getZ());

		//Move Up - Out Of Blocks
		while (!UtilBlock.airFoliage(SpectatorSpawn.getBlock()) || !UtilBlock.airFoliage(SpectatorSpawn.getBlock().getRelative(BlockFace.UP)))
		{
			SpectatorSpawn.add(0, 1, 0);
		}

		int Up = 0;

		//Move Up - Through Air
		for (int i=0 ; i<15 ; i++)
		{
			if (UtilBlock.airFoliage(SpectatorSpawn.getBlock().getRelative(BlockFace.UP)))
			{
				SpectatorSpawn.add(0, 1, 0);
				Up++;
			}
			else
			{
				break;
			}
		}

		//Move Down - Out Of Blocks
		while (Up > 0 && !UtilBlock.airFoliage(SpectatorSpawn.getBlock()) || !UtilBlock.airFoliage(SpectatorSpawn.getBlock().getRelative(BlockFace.UP)))
		{
			SpectatorSpawn.subtract(0, 1, 0);
			Up--;
		}

		SpectatorSpawn = SpectatorSpawn.getBlock().getLocation().add(0.5, 0.1, 0.5);

		while (SpectatorSpawn.getBlock().getTypeId() != 0 || SpectatorSpawn.getBlock().getRelative(BlockFace.UP).getTypeId() != 0)
			SpectatorSpawn.add(0, 1, 0);

		return SpectatorSpawn;
	}

	public void SetSpectator(Player player) 
	{
		Manager.Clear(player);

		player.teleport(GetSpectatorLocation());
		player.setGameMode(GameMode.CREATIVE);
		player.setFlying(true);
		player.setFlySpeed(0.1f);
		((CraftPlayer)player).getHandle().spectating = true;
		((CraftPlayer)player).getHandle().m = false;

		Manager.GetCondition().Factory().Cloak("Spectator", player, player, 7777, true, true);

		if (GetTeam(player) != null && _scoreboard.getTeam(GetTeam(player).GetName().toUpperCase()) != null)
		{
			_scoreboard.getTeam(GetTeam(player).GetName().toUpperCase()).removePlayer(player);
		}

		SetPlayerScoreboardTeam(player, "SPEC");
	}

	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (GameTeam team : this.GetTeamList())
		{			
			String name = team.GetColor() + team.GetName();
			if (name.length() > 16)
				name = name.substring(0, 16);

			Score score = GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(name));
			score.setScore(team.GetPlayers(true).size());
		}
	}

	public DeathMessageType GetDeathMessageType()
	{
		if (!DeathMessages)
			return DeathMessageType.None;

		if (this.DeathOut)
			return DeathMessageType.Detailed;

		return DeathMessageType.Simple;
	}

	public boolean CanJoinTeam(GameTeam team)
	{
		return team.GetSize() < Math.max(1, UtilServer.getPlayers().length/GetTeamList().size());
	}

	public GameTeam GetTeamPreference(Player player)
	{
		for (GameTeam team : _teamPreference.keySet())
		{
			if (_teamPreference.get(team).contains(player))
				return team;
		}

		return null;
	}

	public void RemoveTeamPreference(Player player)
	{
		for (ArrayList<Player> queue : _teamPreference.values())
			queue.remove(player);
	}

	public String GetTeamQueuePosition(Player player)
	{
		for (ArrayList<Player> queue : _teamPreference.values())
		{
			for (int i=0 ; i<queue.size() ; i++)
			{
				if (queue.get(i).equals(player))
					return (i+1) + "/" + queue.size();
			}
		}

		return "Unknown";
	}

	public void InformQueuePositions()
	{
		for (GameTeam team : _teamPreference.keySet())
		{
			for (Player player : _teamPreference.get(team))
			{
				UtilPlayer.message(player, F.main("Team", "You are " + F.elem(GetTeamQueuePosition(player)) + " in queue for " + F.elem(team.GetFormattedName() + " Team") + "."));
			}
		}
	}

	public void AnnounceGame()
	{
		for (Player player : UtilServer.getPlayers())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);

			for (int i=0 ; i<6-GetDesc().length ; i++)
				UtilPlayer.message(player, "");

			UtilPlayer.message(player, ArcadeFormat.Line);

			UtilPlayer.message(player, C.cGreen + "Game - " + C.cYellow+ C.Bold + this.GetName());
			UtilPlayer.message(player, "");

			for (String line : this.GetDesc())
			{
				UtilPlayer.message(player, C.cWhite + "- " + line);
			}

			UtilPlayer.message(player, "");
			UtilPlayer.message(player, C.cGreen + "Map - " + C.cYellow + C.Bold + WorldData.MapName + ChatColor.RESET + C.cGray + " created by " + C.cYellow+ C.Bold + WorldData.MapAuthor);

			UtilPlayer.message(player, ArcadeFormat.Line);
		}

		if (AnnounceSilence)
			Manager.GetChat().Silence(9000, false);
	}

	public void AnnounceEnd(GameTeam team)
	{
		for (Player player : UtilServer.getPlayers())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);

			UtilPlayer.message(player, "");
			UtilPlayer.message(player, ArcadeFormat.Line);

			UtilPlayer.message(player, "§aGame - §f§l" + this.GetName());
			UtilPlayer.message(player, "");
			UtilPlayer.message(player, "");

			if (team != null)
			{
				WinnerTeam = team;
				Winner = team.GetName() + " Team";
				UtilPlayer.message(player, team.GetColor() + C.Bold + team.GetName() + " won the game!");	
			}		
			else
			{
				UtilPlayer.message(player, ChatColor.WHITE + "§lNobody won the game...");
			}


			UtilPlayer.message(player, _customWinLine);
			UtilPlayer.message(player, "");
			UtilPlayer.message(player, "§aMap - §f§l" + WorldData.MapName + C.cGray + " created by " + "§f§l" + WorldData.MapAuthor);

			UtilPlayer.message(player, ArcadeFormat.Line);
		}

		if (AnnounceSilence)
			Manager.GetChat().Silence(5000, false);
	}

	public void AnnounceEnd(ArrayList<Player> places)
	{
		for (Player player : UtilServer.getPlayers())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);

			UtilPlayer.message(player, "");
			UtilPlayer.message(player, ArcadeFormat.Line);

			UtilPlayer.message(player, "§aGame - §f§l" + this.GetName());
			UtilPlayer.message(player, "");

			if (places == null || places.isEmpty())
			{
				UtilPlayer.message(player, "");
				UtilPlayer.message(player, ChatColor.WHITE + "§lNobody won the game...");
				UtilPlayer.message(player, "");
			}		
			else
			{
				if (places.size() >= 1)
				{
					Winner = places.get(0).getName();
					UtilPlayer.message(player, C.cRed + C.Bold + "1st Place" + C.cWhite + " - " + places.get(0).getName());
				}


				if (places.size() >= 2)
					UtilPlayer.message(player, C.cGold + C.Bold + "2nd Place" + C.cWhite + " - " + places.get(1).getName());

				if (places.size() >= 3)
					UtilPlayer.message(player, C.cYellow + C.Bold + "3rd Place" + C.cWhite + " - " + places.get(2).getName());
			}

			UtilPlayer.message(player, "");
			UtilPlayer.message(player, "§aMap - §f§l" + WorldData.MapName + C.cGray + " created by " + "§f§l" + WorldData.MapAuthor);

			UtilPlayer.message(player, ArcadeFormat.Line);
		}

		if (AnnounceSilence)
			Manager.GetChat().Silence(5000, false);
	}

	public void Announce(String message)
	{
		for (Player player : UtilServer.getPlayers())
		{
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);

			UtilPlayer.message(player, message);
		}
	}

	public boolean AdvertiseText(GameLobbyManager gameLobbyManager, int _advertiseStage) 
	{
		return false;
	}
}
