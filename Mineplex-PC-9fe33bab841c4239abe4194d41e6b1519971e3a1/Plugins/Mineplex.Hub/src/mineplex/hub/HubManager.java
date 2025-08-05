package mineplex.hub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePlayer;
import mineplex.core.donation.DonationManager;
import mineplex.core.portal.Portal;
import mineplex.core.task.TaskManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.modules.MapManager;
import mineplex.hub.modules.MountManager;
import mineplex.hub.party.Party;
import mineplex.hub.party.PartyManager;
import mineplex.hub.tutorial.TutorialManager;

public class HubManager extends MiniClientPlugin<HubClient>
{
	private CoreClientManager _clientManager; 
	private DonationManager _donationManager;
	private DisguiseManager _disguiseManager;
	private PartyManager _partyManager;
	private Portal _portal;


	private TutorialManager _tutorialManager;
	private TextCreator _textCreator;

	private Location _spawn;
	private int _scoreboardTick = 0;
	
	private HashMap<Player, Scoreboard> _scoreboards = new HashMap<Player, Scoreboard>();

	private String _pigStacker = "0 - Nobody";

	public String DragonTextA = "Mineplex";
	public String DragonTextB = "";
	
	private boolean _shuttingDown;

	private HashSet<LivingEntity> _mobs = new HashSet<LivingEntity>();

	public HubManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, DisguiseManager disguiseManager, TaskManager taskManager, Portal portal) 
	{
		super("Hub Manager", plugin);

		_clientManager = clientManager;
		_donationManager = donationManager;
		_disguiseManager = disguiseManager;
		_portal = portal;

		_spawn = new Location(UtilWorld.getWorld("world"), 0.5, 74, 0.5);

		_textCreator = new TextCreator(this);

		new Dragon(this);
		new MountManager(this);
		new MapManager(this);

		_partyManager = new PartyManager(this);
		_tutorialManager = new TutorialManager(this, donationManager, taskManager, _textCreator);

		DragonTextB = GetDragonText();
	}
	
	public String GetDragonText()
	{
		File file = new File("DragonText.dat");

		//Write If Blank
		if (!file.exists())
		{
			try
			{
				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("Home of Premium Game Modes");

				out.close();
			}
			catch (Exception e)
			{
				System.out.println("Error: Game World GetId Write Exception");
			}
		}

		String line = "Home of Premium Game Modes";

		//Read
		try
		{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			line = br.readLine();

			in.close();
		}
		catch (Exception e)
		{
			System.out.println("Error: Game World GetId Read Exception");
		}

		return line;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void reflectMotd(ServerListPingEvent event)
	{
		if (_shuttingDown)
		{
			event.setMotd("Restarting soon");
		}
	}

	@EventHandler
	public void redirectStopCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.getPlayer().isOp() && event.getMessage().equalsIgnoreCase("/stop"))
		{
			_shuttingDown = true;
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
			{
				public void run()
				{
					_portal.SendAllPlayers("Lobby");
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
					{
						public void run()
						{
							Bukkit.shutdown();
						}
					}, 40L);
				}
			}, 60L);
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventEggSpawn(ItemSpawnEvent event)
	{
		if (event.getEntity() instanceof Egg)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent event)
	{
		event.setRespawnLocation(GetSpawn());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void OnChunkLoad(ChunkLoadEvent event)
	{
		for (Entity entity : event.getChunk().getEntities())
		{
			if (entity instanceof LivingEntity)
			{
				if (((LivingEntity)entity).isCustomNameVisible() && ((LivingEntity)entity).getCustomName() != null)
				{
					if (((LivingEntity)entity).getCustomName().equalsIgnoreCase("play minekart plz"))
					{
						_disguiseManager.disguise(new DisguisePlayer(entity, "Play " + ChatColor.YELLOW + "MineKart"));
					}
					else if (((LivingEntity)entity).getCustomName().equalsIgnoreCase("defek7"))
						_disguiseManager.disguise(new DisguisePlayer(entity, "defek7"));
					else if (((LivingEntity)entity).getCustomName().equalsIgnoreCase("chiss"))
						_disguiseManager.disguise(new DisguisePlayer(entity, "Chiss"));
					else if (((LivingEntity)entity).getCustomName().equalsIgnoreCase("Sterling_"))
						_disguiseManager.disguise(new DisguisePlayer(entity, "sterling_"));
					else if (((LivingEntity)entity).getCustomName().equalsIgnoreCase("Spu_"))
						_disguiseManager.disguise(new DisguisePlayer(entity, "Spu_"));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();

		//Survival
		player.setGameMode(GameMode.SURVIVAL);

		//Public Message
		event.setJoinMessage(null);

		//Teleport
		player.teleport(GetSpawn());

		//Allow Double Jump
		player.setAllowFlight(true);

		UtilInv.Clear(player);

		//Scoreboard
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		player.setScoreboard(board);
		_scoreboards.put(player, board);

		//Objective
		Objective obj = board.registerNewObjective(C.Bold + "Player Data", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Rank rank : Rank.values())
		{
			if (rank != Rank.ALL)
				board.registerNewTeam(rank.Name).setPrefix(rank.Color + C.Bold + rank.Name + ChatColor.RESET + " ");
			else
				board.registerNewTeam(rank.Name).setPrefix("");
		}

		for (Player otherPlayer : Bukkit.getOnlinePlayers())
		{
			//Add Other to Self
			board.getTeam(_clientManager.Get(otherPlayer).GetRank().Name).addPlayer(otherPlayer);

			//Add Self to Other
			otherPlayer.getScoreboard().getTeam(_clientManager.Get(player).GetRank().Name).addPlayer(player);
		}

		board.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Chiss"));
		board.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("defek7"));
		board.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Spu_"));
		board.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("sterling_"));
	}

	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);

		event.getPlayer().leaveVehicle();
		event.getPlayer().eject();

		for (Player player : UtilServer.getPlayers())
			player.getScoreboard().resetScores(event.getPlayer());
		
		_scoreboards.remove(event.getPlayer());
	}

	@EventHandler
	public void PlayerChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();

		Rank rank = GetClients().Get(player).GetRank();

		String rankStr = "";
		if  (rank != Rank.ALL)
			rankStr = rank.Color + C.Bold + GetClients().Get(player).GetRank().Name.toUpperCase() + " ";

		//Party Chat
		if (event.getMessage().charAt(0) == '@')
		{
			Party party = _partyManager.GetParty(player);
			if (party != null)
			{
				for (String name : party.GetPlayers())
				{
					Player other = UtilPlayer.searchExact(name);
					if (other != null)
						UtilPlayer.message(other, C.cDPurple + C.Bold + "Party " + C.cWhite + C.Bold + player.getName() + ChatColor.RESET + " " + C.cPurple + event.getMessage().substring(1, event.getMessage().length()));
				}
			}
			else
			{
				UtilPlayer.message(player, F.main("Party", "You are not in a Party."));
			}
			
			return;
		}

		for (Player other : UtilServer.getPlayers())
		{
			if (_tutorialManager.InTutorial(other))
				continue;

			UtilPlayer.message(other, rankStr + C.cYellow + player.getName() + " " + C.cWhite + event.getMessage());
		}
	}

	@EventHandler
	public void Damage(EntityDamageEvent event)
	{
		if (event.getCause() == DamageCause.VOID)
			if (event.getEntity() instanceof Player)
				event.getEntity().teleport(GetSpawn());
			else
				event.getEntity().remove();

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void ItemPickup(PlayerPickupItemEvent event)
	{
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void ItemDrop(PlayerDropItemEvent event)
	{
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void BlockBreak(BlockBreakEvent event)
	{
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return; 

		event.setCancelled(true);
	}

	@EventHandler
	public void LeaveDecay(LeavesDecayEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void BlockPlace(BlockPlaceEvent event)
	{
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void FlightHop(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);
		player.setFlying(false);

		//Disable Flight
		player.setAllowFlight(false);

		//Velocity
		UtilAction.velocity(player, 1.4, 0.2, 1, true);

		//Sound
		player.playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
	}

	@EventHandler
	public void FlightUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (player.getGameMode() == GameMode.CREATIVE)
				continue;

			if (UtilEnt.isGrounded(player) || UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
			{
				player.setAllowFlight(true);
				player.setFlying(false);
			}
		}
	}

	@EventHandler
	public void BorderUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (UtilMath.offset(player.getLocation(), GetSpawn()) > 200)
			{
				player.eject();
				player.leaveVehicle();
				player.teleport(GetSpawn());
			}
		}
	}

	@EventHandler
	public void FoodHealthUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			player.setHealth(20);
			player.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void UpdateWeather(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		World world = UtilWorld.getWorld("world");
		world.setTime(6000);
		world.setStorm(false);
	}

	@EventHandler
	public void UpdateScoreboard(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		_scoreboardTick = (_scoreboardTick + 1)%3;

		if (_scoreboardTick != 0)
			return;

		int bestPig = 0;
		for (Player player : UtilServer.getPlayers())
		{
			if (player.getVehicle() != null)
				continue;

			int count = 0;

			Entity ent = player;
			while (ent.getPassenger() != null)
			{
				ent = ent.getPassenger();
				count++;
			}

			if (count > bestPig)
			{
				_pigStacker = player.getName();
				bestPig = count;
			}
		}
		if (bestPig == 0)
		{
			_pigStacker = "0 - Nobody";
		}
		else
		{
			_pigStacker = bestPig + " - " + _pigStacker;

			if (_pigStacker.length() > 16)
				_pigStacker = _pigStacker.substring(0, 16);
		}

		for (Player player : UtilServer.getPlayers())
		{
			//Dont Waste Time
			if (_partyManager.GetParty(player) != null)
				continue;
			
			//Return to Main Scoreboard
			if (!player.getScoreboard().equals(_scoreboards.get(player)))
				player.setScoreboard(_scoreboards.get(player));
			
			//Objective
			Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

			//Title
			obj.setDisplayName(C.cWhite + C.Bold + Get(player).GetScoreboardText());

			int line = 15;

			obj.getScore(Bukkit.getOfflinePlayer(C.cGreen + C.Bold +  "Gems")).setScore(line--);

			// Remove Old
			player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player.getName()).GetLastGemCount() + ""));
			// Add New			
			obj.getScore(Bukkit.getOfflinePlayer(GetDonation().Get(player.getName()).GetGems() + "")).setScore(line--);

			Get(player.getName()).SetLastGemCount(GetDonation().Get(player.getName()).GetGems());

			//Space
			obj.getScore(Bukkit.getOfflinePlayer(" ")).setScore(line--);

			/*
			//News
			obj.getScore(Bukkit.getOfflinePlayer(C.cGray + C.Bold + "Latest News")).setScore(line--);	
			player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player).GetNewsText(false)));
			obj.getScore(Bukkit.getOfflinePlayer(Get(player).GetNewsText(true))).setScore(line--);
			 */

			//Stacker
			obj.getScore(Bukkit.getOfflinePlayer(C.cGray + C.Bold + "Stacker")).setScore(line--);
			player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player).BestPig));
			Get(player).BestPig = _pigStacker;
			obj.getScore(Bukkit.getOfflinePlayer(Get(player).BestPig)).setScore(line--);

			//Space
			obj.getScore(Bukkit.getOfflinePlayer("  ")).setScore(line--);

			//Display Rank
			if (GetClients().Get(player).GetRank().Has(Rank.ULTRA))
			{
				obj.getScore(Bukkit.getOfflinePlayer(C.cPurple + C.Bold + "Ultra Rank")).setScore(line--);

				player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player).GetUltraText(false)));
				obj.getScore(Bukkit.getOfflinePlayer(Get(player).GetUltraText(true))).setScore(line--);
			}
			else
			{
				obj.getScore(Bukkit.getOfflinePlayer(C.cRed + C.Bold + "No Rank")).setScore(line--);

				player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player).GetPurchaseText(false)));
				obj.getScore(Bukkit.getOfflinePlayer(Get(player).GetPurchaseText(true))).setScore(line--);
			}

			//Space
			obj.getScore(Bukkit.getOfflinePlayer("   ")).setScore(line--);

			//Display Staff
			obj.getScore(Bukkit.getOfflinePlayer(C.cGold + C.Bold + "Online Staff")).setScore(line--);
			String staff = "";
			for (Player other : UtilServer.getPlayers())
			{
				Rank rank = GetClients().Get(other).GetRank();

				if (!rank.Has(Rank.HELPER))
					continue;

				staff += other.getName() + "   ";
			}
			if (staff.length() == 0)
				staff = "None";

			player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(Get(player).GetStaffText(false)));
			Get(player).StaffString = staff;
			obj.getScore(Bukkit.getOfflinePlayer(Get(player).GetStaffText(true))).setScore(line--);

			//Space
			obj.getScore(Bukkit.getOfflinePlayer("    ")).setScore(line--);

			//Website
			obj.getScore(Bukkit.getOfflinePlayer(C.cYellow + C.Bold + "Website")).setScore(line--);
			obj.getScore(Bukkit.getOfflinePlayer("www.mineplex.com")).setScore(line--);
			obj.getScore(Bukkit.getOfflinePlayer("----------------")).setScore(line--);

		}
	}

	@Override
	protected HubClient AddPlayer(String player) 
	{ 
		return new HubClient(player);
	}

	public CoreClientManager GetClients()
	{
		return _clientManager;
	}

	public DonationManager GetDonation()
	{
		return _donationManager;
	}

	public Location GetSpawn()
	{
		return _spawn.clone();
	}

	@EventHandler
	public void UpdateVisibility(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			for (Player other : UtilServer.getPlayers())
			{
				if (player.equals(other))
					continue;

				if (UtilMath.offset(player.getLocation(), GetSpawn()) < 4 || _tutorialManager.InTutorial(other) || _tutorialManager.InTutorial(player))
				{
					((CraftPlayer)other).hidePlayer(player, true, false);
				}
				else
				{
					other.showPlayer(player);
				}
			}
		}
	}

	@EventHandler
	public void SpawnAnimals(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;

		Iterator<LivingEntity> entIterator = _mobs.iterator();

		while (entIterator.hasNext())
		{
			LivingEntity ent = entIterator.next();

			if (!ent.isValid())
			{
				ent.remove();
				entIterator.remove();
			}
		}

		if (_mobs.size() > 24)
			return;

		//Loc
		double r = Math.random();

		Location loc = GetSpawn();

		if (r > 0.75)		loc.add(32, 0.5, 0);
		else if (r > 0.5)	loc.add(0, 0.5, 32);
		else if (r > 0.25)	loc.add(-32, 0.5, 0);
		else 				loc.add(0, 0.5, -32);

		//Spawn
		r = Math.random();

		if (r > 0.75)		_mobs.add(loc.getWorld().spawn(loc, Cow.class));
		else if (r > 0.5)	_mobs.add(loc.getWorld().spawn(loc, Pig.class));
		else if (r > 0.25)	_mobs.add(loc.getWorld().spawn(loc, Sheep.class));
		else				_mobs.add(loc.getWorld().spawn(loc, Chicken.class));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Explosion(EntityExplodeEvent event)
	{
		event.blockList().clear();
	}

	@EventHandler
	public void VineGrow(BlockSpreadEvent event)
	{
		event.setCancelled(true);
	}
}
