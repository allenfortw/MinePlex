package nautilus.game.arcade.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.common.util.UtilText.TextAlign;
import mineplex.core.donation.Donor;
import mineplex.core.packethandler.IPacketRunnable;
import mineplex.core.packethandler.PacketArrayList;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.AsymTeamGame;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import net.minecraft.server.v1_6_R2.DataWatcher;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_6_R2.WatchableObject;

public class GameLobbyManager implements IPacketRunnable, Listener
{
	public ArcadeManager Manager;

	private Location _gameText;
	private Location _advText;
	private Location _kitText;
	private Location _teamText;


	private Location _kitDisplay;
	private Location _teamDisplay;

	private Location spawn;

	private NautHashMap<Entity, LobbyEnt> _kits = new NautHashMap<Entity, LobbyEnt>();
	private NautHashMap<Block, Material> _kitBlocks = new NautHashMap<Block, Material>();

	private NautHashMap<Entity, LobbyEnt> _teams = new NautHashMap<Entity, LobbyEnt>();
	private NautHashMap<Block, Material> _teamBlocks = new NautHashMap<Block, Material>();

	private long _fireworkStart;
	private Color _fireworkColor;

	private int _advertiseStage = 0;

	//Scoreboard
	private NautHashMap<Player, Scoreboard> _scoreboardMap = new NautHashMap<Player, Scoreboard>();
	private NautHashMap<Player, Integer> _gemMap = new NautHashMap<Player, Integer>();
	private NautHashMap<Player, String> _kitMap = new NautHashMap<Player, String>();

	private Field _packet40Metadata;
	private Field _packet24MobSpawn;

	private int _oldPlayerCount = 0;

	public GameLobbyManager(ArcadeManager manager, PacketHandler packetHandler)
	{
		Manager = manager;
		packetHandler.AddPacketRunnable(this);

		World world = UtilWorld.getWorld("world");

		spawn = new Location(world, 0, 104, 0);

		_gameText = new Location(world, 0, 130, 50);
		_kitText = new Location(world, -40, 120, 0);
		_teamText = new Location(world, 40, 120, 0);
		_advText = new Location(world, 0, 140, -60);

		_kitDisplay = new Location(world, -17, 101, 0);
		_teamDisplay = new Location(world, 18, 101, 0);

		try 
		{
			_packet40Metadata = Packet40EntityMetadata.class.getDeclaredField("b");
			_packet24MobSpawn = Packet24MobSpawn.class.getDeclaredField("t");
		} 
		catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			e.printStackTrace();
		}

		_packet40Metadata.setAccessible(true);
		_packet24MobSpawn.setAccessible(true);

		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}

	private boolean HasScoreboard(Player player)
	{
		return _scoreboardMap.containsKey(player);
	}

	public void CreateScoreboards()
	{
		for (Player player : UtilServer.getPlayers())
			CreateScoreboard(player);
	}

	private void CreateScoreboard(Player player) 
	{
		_scoreboardMap.put(player, Bukkit.getScoreboardManager().getNewScoreboard());

		Scoreboard scoreboard = _scoreboardMap.get(player);
		Objective objective = scoreboard.registerNewObjective("§l" + "Lobby", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Rank rank : Rank.values())
		{
			if (rank == Rank.ALL)
			{
				scoreboard.registerNewTeam(rank.Name).setPrefix("");
			}
			else
			{
				scoreboard.registerNewTeam(rank.Name).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + ChatColor.WHITE);
			}

			if (Manager.GetGame() != null && !Manager.GetGame().GetTeamList().isEmpty())
			{
				for (GameTeam team : Manager.GetGame().GetTeamList())
				{
					if (rank == Rank.ALL)
					{
						scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(team.GetColor() + "");
					}
					else
					{
						scoreboard.registerNewTeam(rank.Name + team.GetName().toUpperCase()).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + team.GetColor());
					}
				}
			}
		}

		for (Player otherPlayer : UtilServer.getPlayers())
		{
			AddPlayerToScoreboards(otherPlayer, null);
		}
	}

	public Collection<Scoreboard> GetScoreboards()
	{
		return _scoreboardMap.values();
	}

	public void WriteLine(Player player, int x, int y, int z, BlockFace face, int line, String text)
	{
		Location loc = player.getLocation();
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);

		int id = 159;
		byte data = 15;

		if (player.getItemInHand() != null && player.getItemInHand().getType().isBlock() && player.getItemInHand().getType() != Material.AIR)
		{
			id = player.getItemInHand().getTypeId();
			data = UtilInv.GetData(player.getItemInHand());
		}

		if (line > 0)
			loc.add(0, line*-6, 0);

		UtilText.MakeText(text, loc, face, id, data, TextAlign.CENTER);

		player.sendMessage("Writing: " + text + " @ " + UtilWorld.locToStrClean(loc));
	}

	public void WriteGameLine(String text, int line, int id, byte data)
	{
		Location loc = _gameText.clone();

		if (line > 0)
			loc.add(0, line*-6, 0);

		BlockFace face = BlockFace.WEST;

		UtilText.MakeText(text, loc, face, id, data, TextAlign.CENTER);
	}

	public void WriteAdvertiseLine(String text, int line, int id, byte data)
	{
		Location loc = _advText.clone();

		if (line > 0)
			loc.add(0, line*-6, 0);

		BlockFace face = BlockFace.EAST;

		UtilText.MakeText(text, loc, face, id, data, TextAlign.CENTER);
	}

	public void WriteKitLine(String text, int line, int id, byte data)
	{
		Location loc = _kitText.clone();

		if (line > 0)
			loc.add(0, line*-6, 0);

		BlockFace face = BlockFace.NORTH;

		UtilText.MakeText(text, loc, face, id, data, TextAlign.CENTER);
	}

	public void WriteTeamLine(String text, int line, int id, byte data)
	{
		Location loc = _teamText.clone();

		if (line > 0)
			loc.add(0, line*-6, 0);

		BlockFace face = BlockFace.SOUTH;

		UtilText.MakeText(text, loc, face, id, data, TextAlign.CENTER);
	}

	public Location GetSpawn() 
	{	
		return spawn.clone().add(4 - Math.random()*8, 0, 4 - Math.random()*8);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void TeamGeneration(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Recruit)
			return;

		CreateTeams(event.GetGame());
	}

	public void CreateTeams(Game game)
	{
		//Text
		WriteTeamLine("Select", 0, 159, (byte)15);
		WriteTeamLine("Team", 1, 159, (byte)4);

		//Remove Old Ents
		for (Entity ent : _teams.keySet())
			ent.remove();
		_teams.clear();

		//Remove Blocks
		for (Block block : _teamBlocks.keySet())
			block.setType(_teamBlocks.get(block));
		_teamBlocks.clear();
		
		
		//Standard
		if (game.GetKits().length > 1)
		{
			//Display
			ArrayList<GameTeam> teams = game.GetTeamList();

			//Positions
			double space = 6;		
			double offset = (teams.size()-1)*space/2d;

			for (int i=0 ; i<teams.size() ; i++)
			{
				Location entLoc = _teamDisplay.clone().subtract(0, 0, i*space - offset);

				SetKitTeamBlocks(entLoc.clone(), 35, teams.get(i).GetColorData(), _teamBlocks);

				entLoc.add(0, 1.5, 0);

				entLoc.getChunk().load();

				Sheep ent = (Sheep)Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
				ent.setRemoveWhenFarAway(false);
				ent.setCustomNameVisible(true);

				ent.setColor(DyeColor.getByWoolData(teams.get(i).GetColorData()));

				UtilEnt.Vegetate(ent);

				teams.get(i).SetTeamEntity(ent);

				_teams.put(ent, new LobbyEnt(ent, entLoc, teams.get(i)));
			}
		}
		//Double
		else
		{
			//Text
			WriteKitLine("Select", 0, 159, (byte)15);
			WriteKitLine("Team", 1, 159, (byte)4);
			
			//Display
			ArrayList<GameTeam> teamsA = new ArrayList<GameTeam>();
			ArrayList<GameTeam> teamsB = new ArrayList<GameTeam>();
			
			for (int i=0 ; i<game.GetTeamList().size() ; i++)
			{
				if (i < game.GetTeamList().size()/2)
					teamsA.add(game.GetTeamList().get(i));
				else
					teamsB.add(game.GetTeamList().get(i));
			}
			
			//A
			{
				//Positions
				double space = 6;		
				double offset = (teamsA.size()-1)*space/2d;

				for (int i=0 ; i<teamsA.size() ; i++)
				{
					Location entLoc = _teamDisplay.clone().subtract(0, 0, i*space - offset);

					SetKitTeamBlocks(entLoc.clone(), 35, teamsA.get(i).GetColorData(), _teamBlocks);

					entLoc.add(0, 1.5, 0);

					entLoc.getChunk().load();

					Sheep ent = (Sheep)Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
					ent.setRemoveWhenFarAway(false);
					ent.setCustomNameVisible(true);

					ent.setColor(DyeColor.getByWoolData(teamsA.get(i).GetColorData()));

					UtilEnt.Vegetate(ent);

					teamsA.get(i).SetTeamEntity(ent);

					_teams.put(ent, new LobbyEnt(ent, entLoc, teamsA.get(i)));
				}
			}
			//B
			{
				//Positions
				double space = 6;		
				double offset = (teamsB.size()-1)*space/2d;

				for (int i=0 ; i<teamsB.size() ; i++)
				{
					Location entLoc = _kitDisplay.clone().subtract(0, 0, i*space - offset);

					SetKitTeamBlocks(entLoc.clone(), 35, teamsB.get(i).GetColorData(), _teamBlocks);

					entLoc.add(0, 1.5, 0);

					entLoc.getChunk().load();

					Sheep ent = (Sheep)Manager.GetCreature().SpawnEntity(entLoc, EntityType.SHEEP);
					ent.setRemoveWhenFarAway(false);
					ent.setCustomNameVisible(true);

					ent.setColor(DyeColor.getByWoolData(teamsB.get(i).GetColorData()));

					UtilEnt.Vegetate(ent);

					teamsB.get(i).SetTeamEntity(ent);

					_teams.put(ent, new LobbyEnt(ent, entLoc, teamsB.get(i)));
				}
			}
			
			
		}

		

		CreateScoreboards();
	}

	public void CreateKits(Game game)
	{
		//Text
		WriteKitLine("Select", 0, 159, (byte)15);
		WriteKitLine("Kit", 1, 159, (byte)4);

		//Remove Old Ents
		for (Entity ent : _kits.keySet())
			ent.remove();
		_kits.clear();

		//Remove Blocks
		for (Block block : _kitBlocks.keySet())
			block.setType(_kitBlocks.get(block));
		_kitBlocks.clear();
		
		if (game.GetKits().length <= 1)
		{
			WriteKitLine("      ", 0, 159, (byte)15);
			WriteKitLine("      ", 1, 159, (byte)4);
			return;
		}

		//Display
		ArrayList<Kit> kits = new ArrayList<Kit>();
		for (Kit kit : game.GetKits())
		{
			if (kit.GetAvailability() != KitAvailability.Hide)
				kits.add(kit);
		}

		//Positions
		double space = 4;		
		double offset = (kits.size()-1)*space/2d;

		for (int i=0 ; i<kits.size() ; i++)
		{
			Kit kit = kits.get(i);

			if (kit.GetAvailability() == KitAvailability.Null)
				continue;

			Location entLoc = _kitDisplay.clone().subtract(0, 0, i*space - offset);

			byte data = 4;
			if (kit.GetAvailability() == KitAvailability.Green) 		data = 5;
			else if (kit.GetAvailability() == KitAvailability.Blue) 	data = 3;
			SetKitTeamBlocks(entLoc.clone(), 35, data, _kitBlocks);

			entLoc.add(0, 1.5, 0);

			entLoc.getChunk().load();

			Entity ent = kit.SpawnEntity(entLoc);

			if (ent == null)
				continue;

			_kits.put(ent, new LobbyEnt(ent, entLoc, kit));
		}
	}

	public void SetKitTeamBlocks(Location loc, int id, byte data, NautHashMap<Block, Material> blockMap) 
	{
		//Coloring
		Block block = loc.clone().add( 0.5, 0,  0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);

		block = loc.clone().add(-0.5, 0,  0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);

		block = loc.clone().add( 0.5, 0, -0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);

		block = loc.clone().add(-0.5, 0, -0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);

		//Top
		block = loc.clone().add( 0.5, 1,  0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);

		block = loc.clone().add(-0.5, 1,  0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);

		block = loc.clone().add( 0.5, 1, -0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);

		block = loc.clone().add(-0.5, 1, -0.5).getBlock();
		blockMap.put(block, block.getType());
		MapUtil.QuickChangeBlockAt(block.getLocation(), 44, (byte)5);

		//Floor
		for (int x=-2 ; x<2 ; x++)
		{
			for (int z=-2 ; z<2 ; z++)
			{
				block = loc.clone().add(x, -1,  z).getBlock();

				blockMap.put(block, block.getType());
				MapUtil.QuickChangeBlockAt(block.getLocation(), id, data);
			}
		}

		//Outline
		for (int x=-3 ; x<3 ; x++)
		{
			for (int z=-3 ; z<3 ; z++)
			{
				block = loc.clone().add(x, -1,  z).getBlock();

				if (blockMap.containsKey(block))
					continue;

				blockMap.put(block, block.getType());
				MapUtil.QuickChangeBlockAt(block.getLocation(), 35, (byte)15);
			}
		}
	}

	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		_scoreboardMap.remove(event.getPlayer());
		_gemMap.remove(event.getPlayer());
		_kitMap.remove(event.getPlayer());
	}

	@EventHandler
	public void Update(UpdateEvent event) 
	{
		if (event.getType() == UpdateType.FAST)
		{
			spawn.getWorld().setTime(6000);
			spawn.getWorld().setStorm(false);
			spawn.getWorld().setThundering(false);
		}


		if (event.getType() == UpdateType.TICK)
			UpdateEnts();

		if (event.getType() == UpdateType.FASTEST)
			UpdateFirework();

		if (event.getType() == UpdateType.SEC)
			RemoveInvalidEnts();

		if (event.getType() == UpdateType.SLOW)
			UpdateAdvertise();

		ScoreboardDisplay(event);
		ScoreboardSet(event);
	}

	private void RemoveInvalidEnts() 
	{
		for (Entity ent : UtilWorld.getWorld("world").getEntities())
		{
			if (ent instanceof Creature || ent instanceof Slime)
			{
				if (_kits.containsKey(ent))
					continue;

				if (_teams.containsKey(ent))
					continue;

				if (ent.getPassenger() != null)
					continue;
				
				ent.remove();
			}
		}
	}

	private void UpdateAdvertise() 
	{
		if (Manager.GetGame() == null)
			return;
		
		_advertiseStage = (_advertiseStage+1)%2;
		
		if (Manager.GetGame().AdvertiseText(this, _advertiseStage))
		{
			return;
		}

		if (_advertiseStage == 0)
		{
			WriteAdvertiseLine("MINEPLEX ULTRA RANK", 0, 159, (byte)4);
			WriteAdvertiseLine("UNLOCKS EVERYTHING", 1, 159, (byte)15);
			WriteAdvertiseLine("IN EVERY GAME", 2, 159, (byte)15);

			WriteAdvertiseLine("www.mineplex.com", 4, 159, (byte)15);
		}
		else if (_advertiseStage == 1)
		{
			WriteAdvertiseLine("GAME ULTRA RANKS", 0, 159, (byte)4);
			WriteAdvertiseLine("UNLOCK EVERY KIT", 1, 159, (byte)15);
			WriteAdvertiseLine("IN THAT GAME", 2, 159, (byte)15);

			WriteAdvertiseLine("www.mineplex.com", 4, 159, (byte)15);
		}


		
	}

	public void UpdateEnts()
	{
		for (Entity ent : _kits.keySet())
			ent.teleport(_kits.get(ent).GetLocation());

		for (Entity ent : _teams.keySet())
			ent.teleport(_teams.get(ent).GetLocation());
	}

	public Kit GetClickedKit(Entity clicked)
	{
		for (LobbyEnt ent : _kits.values())
			if (clicked.equals(ent.GetEnt()))
				return ent.GetKit();

		return null;
	}

	public GameTeam GetClickedTeam(Entity clicked)
	{
		for (LobbyEnt ent : _teams.values())
			if (clicked.equals(ent.GetEnt()))
				return ent.GetTeam();

		return null;
	}

	public void RegisterFireworks(GameTeam winnerTeam) 
	{
		if (winnerTeam != null)
		{
			_fireworkColor = Color.GREEN;
			if (winnerTeam.GetColor() == ChatColor.RED)			_fireworkColor = Color.RED;
			if (winnerTeam.GetColor() == ChatColor.AQUA)		_fireworkColor = Color.BLUE;
			if (winnerTeam.GetColor() == ChatColor.YELLOW)		_fireworkColor = Color.YELLOW;

			_fireworkStart = System.currentTimeMillis();
		}
	}

	public void UpdateFirework()
	{
		if (UtilTime.elapsed(_fireworkStart, 10000))
			return;

		FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(_fireworkColor).with(Type.BALL_LARGE).trail(false).build();

		try 
		{
			Manager.GetFirework().playFirework(spawn.clone().add(
					Math.random()*160-80, 30 + Math.random()*10, Math.random()*160-80), effect);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@EventHandler
	public void Combust(EntityCombustEvent event) 
	{
		for (LobbyEnt ent : _kits.values())
			if (event.getEntity().equals(ent.GetEnt()))
			{
				event.setCancelled(true);
				return;
			}
	}

	public void DisplayLast(Game game) 
	{
		//Start Fireworks
		RegisterFireworks(game.WinnerTeam);
	}

	public void DisplayNext(Game game, HashMap<String, ChatColor> pastTeams) 
	{
		WriteGameLine("next game", 0, 159, (byte)4);
		WriteGameLine(game.GetName(), 1, 159, (byte)14);
		DisplayWaiting();

		if (game instanceof AsymTeamGame)
		{
			if (pastTeams == null)	WriteGameLine("Round 1 of 2", 2, 159, (byte)14);
			else					WriteGameLine("Round 2 of 2", 2, 159, (byte)14);
		}
		else						WriteGameLine("", 2, 159, (byte)14);

		CreateKits(game);
		CreateTeams(game);
	}

	public void DisplayWaiting()
	{
		WriteGameLine("waiting for players", 3, 159, (byte)13);
	}

	@EventHandler
	public void ScoreboardDisplay(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (Manager.GetGame() != null && 
				(Manager.GetGame().GetState() != GameState.Loading && 
				Manager.GetGame().GetState() != GameState.Recruit))
		{
			for (Player player : UtilServer.getPlayers())
				player.setScoreboard(Manager.GetGame().GetScoreboard());
		}

		else
		{
			for (Player player : UtilServer.getPlayers())
			{
				if (!HasScoreboard(player))
				{
					CreateScoreboard(player);
				}
				else
				{
					player.setScoreboard(_scoreboardMap.get(player));	
				}
			}
		}
	}

	@EventHandler
	public void ScoreboardSet(UpdateEvent event) 
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (Manager.GetGame() != null && !Manager.GetGame().DisplayLobbySide)
		{
			return;
		}
		
		for (Entry<Player, Scoreboard> entry : _scoreboardMap.entrySet())		
		{	
			Objective objective = entry.getValue().getObjective("§l" + "Lobby");

			if (Manager.GetGame() != null && Manager.GetGame().GetCountdown() >= 0)
			{
				if (Manager.GetGame().GetCountdown() > 0)
					objective.setDisplayName(C.Bold + "§lStarting in " + C.cGreen + "§l" + Manager.GetGame().GetCountdown() + " Seconds");
				else if (Manager.GetGame().GetCountdown() == 0)
					objective.setDisplayName(ChatColor.WHITE + "§lIn Progress...");
			}
			else
			{
				objective.setDisplayName(ChatColor.GREEN + "§l" + "Waiting for Players");
			}

			int line = 14;

			objective.getScore(Bukkit.getOfflinePlayer(C.cYellow + "Max Players")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(Manager.GetPlayerFull() + " ")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(" ")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(C.cYellow + "Min Players")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(Manager.GetPlayerMin() + "  ")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer("   ")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(C.cYellow + "Players")).setScore(line--);

			// Remove old
			entry.getValue().resetScores(Bukkit.getOfflinePlayer(_oldPlayerCount + "   "));
			// Set new
			objective.getScore(Bukkit.getOfflinePlayer(UtilServer.getPlayers().length + "   ")).setScore(line--);

			if (Manager.GetGame() != null)
			{
				ChatColor teamColor = ChatColor.GRAY;
				String kitName = "None";

				if (Manager.GetGame().GetTeam(entry.getKey()) != null)
				{
					teamColor = Manager.GetGame().GetTeam(entry.getKey()).GetColor();
				}

				if (Manager.GetGame().GetKit(entry.getKey()) != null)
				{
					kitName = Manager.GetGame().GetKit(entry.getKey()).GetName() + "";
				}

				if (teamColor == null)

					//Shorten Kit Name
					if (kitName.length() > 16)
						kitName = kitName.substring(0, 16);

				// Remove old
				entry.getValue().resetScores(Bukkit.getOfflinePlayer(C.cGray + C.Bold +  "Kit"));
				entry.getValue().resetScores(Bukkit.getOfflinePlayer(_kitMap.get(entry.getKey()) + ""));

				// Set new
				objective.getScore(Bukkit.getOfflinePlayer("    ")).setScore(line--);
				objective.getScore(Bukkit.getOfflinePlayer(teamColor + C.Bold +  "Kit")).setScore(line--);
				objective.getScore(Bukkit.getOfflinePlayer(kitName + "")).setScore(line--);

				_kitMap.put(entry.getKey(), kitName + "");
			}

			objective.getScore(Bukkit.getOfflinePlayer("     ")).setScore(line--);
			objective.getScore(Bukkit.getOfflinePlayer(C.cGreen + C.Bold +  "Gems")).setScore(line--);

			// Remove old
			entry.getValue().resetScores(Bukkit.getOfflinePlayer(_gemMap.get(entry.getKey()) + "     "));
			// Set new
			objective.getScore(Bukkit.getOfflinePlayer(Manager.GetDonation().Get(entry.getKey().getName()).GetGems() + "     ")).setScore(line--);

			_gemMap.put(entry.getKey(), Manager.GetDonation().Get(entry.getKey().getName()).GetGems());
		}

		_oldPlayerCount = UtilServer.getPlayers().length;
	}

	private String GetKitCustomName(Player player, Game game, LobbyEnt ent)
	{
		CoreClient client = Manager.GetClients().Get(player);
		Donor donor = Manager.GetDonation().Get(player.getName());

		String entityName = ent.GetKit().GetName();

		if (client.GetRank().Has(Rank.ULTRA) || donor.OwnsUnknownPackage(game.GetName() + " " + ent.GetKit().GetName()) || donor.OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA") || ent.GetKit().GetAvailability() == KitAvailability.Free)
		{
			entityName = ChatColor.GREEN + entityName;
		}
		else
		{
			entityName = ChatColor.RED + entityName;

			if (ent.GetKit().GetAvailability() != KitAvailability.Blue)
				entityName += ChatColor.WHITE + " (" + ChatColor.GREEN + ent.GetKit().GetCost() + " Gems" + ChatColor.WHITE + ")";
		}

		if (ent.GetKit().GetAvailability() == KitAvailability.Blue)
		{
			entityName += ChatColor.WHITE + " (" + ChatColor.AQUA + "Ultra" + ChatColor.WHITE + ")";
		}

		return entityName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean run(Packet packet, Player owner, PacketArrayList packetList)
	{
		int entityId = -1;

		if (packet instanceof Packet40EntityMetadata)
		{
			entityId = ((Packet40EntityMetadata)packet).a;
		}
		else if (packet instanceof Packet24MobSpawn)
		{
			//entityId = ((Packet24MobSpawn)packet).a;
		}

		if (entityId != -1)
		{
			String customName = null;

			// Order important (_next and _prev overlap if games are same and will throw NPE on _game.GetName())
			for (LobbyEnt ent : _kits.values())
			{
				if (ent.GetEnt().getEntityId() == entityId && Manager.GetGame() != null)
				{
					customName = GetKitCustomName(owner, Manager.GetGame(), ent);
					break;
				}
			}

			if (customName != null)
			{
				try
				{
					if (packet instanceof Packet40EntityMetadata)
					{
						List<WatchableObject> watchables = new ArrayList<WatchableObject>();

						for (WatchableObject watchableObject : (List<WatchableObject>)_packet40Metadata.get(packet))
						{
							WatchableObject newWatch = new WatchableObject(watchableObject.c(), watchableObject.a(), watchableObject.b());

							if (newWatch.a() == 10)
							{
								newWatch.a(customName);
							}

							watchables.add(newWatch);
						}

						Packet40EntityMetadata newPacket = new Packet40EntityMetadata();
						newPacket.a = entityId;
						_packet40Metadata.set(newPacket, watchables);

						packetList.forceAdd(newPacket);

						return false;
					}
					else if (packet instanceof Packet24MobSpawn)
					{
						DataWatcher watcher = (DataWatcher)_packet24MobSpawn.get((Packet24MobSpawn)packet);
						watcher.watch(10, customName);
						watcher.watch(11, Byte.valueOf((byte)1));
					}
				} 
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	public void AddPlayerToScoreboards(Player player, String teamName) 
	{
		for (Scoreboard scoreboard : GetScoreboards())
		{
			for (Team team : scoreboard.getTeams())
				team.removePlayer(player);
		}

		if (teamName == null)
			teamName = "";

		for (Scoreboard scoreboard : GetScoreboards())
		{
			String rankName = Manager.GetClients().Get(player).GetRank().Name;
			
			if (!Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA) && Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA"))
			{
				rankName = Rank.ULTRA.Name;
			}
				
			scoreboard.getTeam(rankName + teamName).addPlayer(player);
		}
	}
}
