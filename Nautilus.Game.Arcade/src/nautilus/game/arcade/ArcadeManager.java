package nautilus.game.arcade;

import java.io.File;
import java.util.ArrayList;

import nautilus.game.arcade.addons.*;
import nautilus.game.arcade.command.*;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.managers.*;
import nautilus.game.arcade.shop.ArcadeShop;
import nautilus.game.arcade.world.FireworkHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.core.MiniPlugin;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Modules.Blood;
import mineplex.core.account.CoreClientManager;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.chat.Chat;
import mineplex.core.common.Rank;
import mineplex.core.common.util.*;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.explosion.Explosion;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;

public class ArcadeManager extends MiniPlugin implements IRelation
{
	//Modules
	private BlockRestore _blockRestore;
	private Blood _blood;
	private Chat _chat;
	private CoreClientManager _clientManager;
	private DisguiseManager _disguiseManager;
	private DonationManager _donationManager;
	private ConditionManager _conditionManager;
	private Creature _creature;
	private DamageManager _damageManager;
	private Explosion _explosionManager;
	private Fire _fire;
	private FireworkHandler _firework;
	private ProjectileManager _projectileManager;

	private Portal _portal; 
	private ArcadeShop _arcadeShop;

	//Managers
	private GameFactory _gameFactory;
	private GameCreationManager _gameCreationManager;
	private GameGemManager _gameGemManager;
	private GameManager _gameManager;
	private GameLobbyManager _gameLobbyManager;
	private GameWorldManager _gameWorldManager;

	//Server Games
	private GameServerConfig _serverConfig;

	//Games
	private Game _game;

	public ArcadeManager(JavaPlugin plugin, GameServerConfig serverConfig, CoreClientManager clientManager, DonationManager donationManager, ConditionManager conditionManager, DamageManager damageManager, DisguiseManager disguiseManager, Creature creature, Blood blood, PacketHandler packetHandler)
	{
		super("Game Manager", plugin);

		_serverConfig = serverConfig;

		//Modules
		_blockRestore = new BlockRestore(plugin);

		_blood = blood;

		_explosionManager = new Explosion(plugin, _blockRestore);
		_explosionManager.SetDebris(false);

		_conditionManager = conditionManager;

		_clientManager = clientManager;

		_chat = new Chat(plugin, _clientManager);

		_creature = creature;

		_damageManager = damageManager;
		_damageManager.UseSimpleWeaponDamage = true;

		_disguiseManager = disguiseManager;

		_donationManager = donationManager;

		_firework = new FireworkHandler();

		_projectileManager = new ProjectileManager(plugin);

		_fire = new Fire(plugin, conditionManager, damageManager);

		_portal = new Portal(plugin);

		//Shop
		_arcadeShop = new ArcadeShop(this, clientManager, donationManager);

		//Game Factory
		_gameFactory = new GameFactory(this);

		//Managers
		new GameChatManager(this);
		_gameCreationManager = new GameCreationManager(this);
		_gameGemManager = new GameGemManager(this);
		_gameManager = new GameManager(this);
		_gameLobbyManager = new GameLobbyManager(this, packetHandler);
		new GameFlagManager(this);
		new GamePlayerManager(this);
		_gameWorldManager = new GameWorldManager(this);
		new MiscManager(this);
		new IdleManager(this);

		//Game Addons
		new CompassAddon(plugin, this);
		new SoupAddon(plugin, this);
	}

	@Override
	public void AddCommands() 
	{
		AddCommand(new GameCommand(this));
		AddCommand(new ParseCommand(this));
		AddCommand(new GemCommand(this));
		AddCommand(new WriteCommand(this));
	}

	public GameServerConfig GetServerConfig()
	{
		return _serverConfig;
	}

	public ArrayList<GameType> GetGameList()
	{
		return GetServerConfig().GameList;
	}

	public Blood GetBlood()
	{
		return _blood;
	}

	public Chat GetChat()
	{
		return _chat;
	}

	public BlockRestore GetBlockRestore()
	{
		return _blockRestore;
	}

	public CoreClientManager GetClients()
	{
		return _clientManager;
	}

	public ConditionManager GetCondition()
	{
		return _conditionManager;
	}

	public Creature GetCreature()
	{
		return _creature;
	}

	public DisguiseManager GetDisguise()
	{
		return _disguiseManager;
	}

	public DamageManager GetDamage()
	{
		return _damageManager;
	}

	public DonationManager GetDonation()
	{
		return _donationManager;
	}
	
	public Explosion GetExplosion()
	{
		return _explosionManager;
	}

	public Fire GetFire()
	{
		return _fire;
	}

	public FireworkHandler GetFirework()
	{
		return _firework;
	}

	public ProjectileManager GetProjectile()
	{
		return _projectileManager;
	}

	public Portal GetPortal()
	{
		return _portal;
	}

	public GameLobbyManager GetLobby()
	{
		return _gameLobbyManager;
	}

	public ArcadeShop GetShop()
	{
		return _arcadeShop;
	}

	public GameCreationManager GetGameCreationManager()
	{
		return _gameCreationManager; 
	}

	public GameFactory GetGameFactory()
	{
		return _gameFactory;
	}

	public GameManager GetGameManager()
	{
		return _gameManager;
	}

	public GameGemManager GetGameGemManager()
	{
		return _gameGemManager;
	}

	public GameWorldManager GetGameWorldManager()
	{
		return _gameWorldManager;
	}

	public ChatColor GetColor(Player player)
	{
		if (_game == null)
			return ChatColor.GRAY;

		GameTeam team = _game.GetTeam(player);
		if (team == null)
			return ChatColor.GRAY;

		return team.GetColor();
	}

	@Override
	public boolean CanHurt(String a, String b) 
	{
		return CanHurt(UtilPlayer.searchExact(a), UtilPlayer.searchExact(b));
	}

	public boolean CanHurt(Player pA, Player pB)
	{
		if (pA == null || pB == null)
			return false;

		if (!_game.Damage)
			return false;

		if (!_game.DamagePvP)
			return false;

		//Self Damage
		if (pA.equals(pB))
			return _game.DamageSelf;

		GameTeam tA = _game.GetTeam(pA);
		if (tA == null)
			return false;

		GameTeam tB = _game.GetTeam(pB);
		if (tB == null)
			return false;

		if (tA.equals(tB) && !_game.DamageTeamSelf)
			return false;

		if (!tA.equals(tB) && !_game.DamageTeamOther)
			return false;

		return true;
	}

	@Override
	public boolean IsSafe(Player player) 
	{
		if (_game == null)
			return true;

		if (_game.IsPlaying(player))
			return false;
		
		return true;
	}

	@EventHandler
	public void MessageMOTD(ServerListPingEvent event)
	{
		if (_game == null || _game.GetState() == GameState.Recruit)
		{
			if (_game != null && _game.GetCountdown() != -1)
			{
				event.setMotd(ChatColor.GREEN + "Starting in " + _game.GetCountdown() + " Seconds");
			}
			else
			{
				event.setMotd(ChatColor.GREEN + "Recruiting");
			}

		}
		else
		{
			event.setMotd(ChatColor.YELLOW + "In Progress");
		}
	}

	@EventHandler
	public void MessageJoin(PlayerJoinEvent event)
	{
		if (_game == null || _game.AnnounceJoinQuit)
			event.setJoinMessage(F.sys("Join", event.getPlayer().getName()));
		else
			event.setJoinMessage(null);
	}

	@EventHandler
	public void MessageQuit(PlayerQuitEvent event)
	{
		if (_game == null || _game.AnnounceJoinQuit)
			event.setQuitMessage(F.sys("Quit", GetColor(event.getPlayer()) + event.getPlayer().getName()));
		else
			event.setQuitMessage(null);
	}

	public Game GetGame() 
	{
		return _game;
	}

	public void SetGame(Game game)
	{
		_game = game;
	}

	public int GetPlayerMin()
	{
		return GetServerConfig().MinPlayers;
	}

	public int GetPlayerFull()
	{
		return GetServerConfig().MaxPlayers;
	}



	public void HubClock(Player player)
	{
		player.getInventory().setItem(8, ItemStackFactory.Instance.CreateStack(Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
				new String[] {"", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub."}));
	}
	
	@EventHandler
	public void Login(PlayerLoginEvent event)
	{		
		if (event.getResult() == PlayerLoginEvent.Result.KICK_OTHER)
		{
			return;
		}
		
        // Reserved Slot Check
		if (Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
		{
			if (_clientManager.Get(event.getPlayer().getName()).GetRank().Has(event.getPlayer(), Rank.HELPER, false) || _donationManager.Get(event.getPlayer().getName()).OwnsUnknownPackage(_serverConfig.ServerType + " ULTRA"))
			{
				event.allow();
				event.setResult(PlayerLoginEvent.Result.ALLOWED);
				return;
			}
			
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Full > Donate for Ultra");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void HubClockInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (player.getItemInHand() == null)
			return;

		if (player.getItemInHand().getType() != Material.WATCH)
			return;

		if (_game != null && _game.IsAlive(player))
			return;

		_portal.SendPlayerToServer(player, "Lobby");		
	}

	public boolean IsAlive(Player player) 
	{
		if (_game == null) 
			return false;

		return _game.IsAlive(player);
	}

	public void Clear(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		UtilInv.Clear(player);
		player.setFoodLevel(20);
		player.setHealth(20); 
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setLevel(0);
		player.setExp(0f);
		((CraftPlayer)player).getHandle().spectating = false;
		((CraftPlayer)player).getHandle().m = true;

		GetCondition().EndCondition(player, ConditionType.CLOAK, "Spectator");

		HubClock(player);

		GetDisguise().undisguise(player);
	}

	public ArrayList<String> LoadFiles(String gameName) 
	{
		File folder = new File("maps" + File.separatorChar + gameName);
		if (!folder.exists())		folder.mkdirs();

		ArrayList<String> maps = new ArrayList<String>();

		System.out.println("Searching Maps in: " + folder);

		for (File file : folder.listFiles())
		{
			if (!file.isFile())
				continue;

			String name = file.getName();

			if (name.length() < 5)
				continue;

			name = name.substring(name.length()-4, name.length());

			if (file.getName().equals(".zip"))
				continue;

			maps.add(file.getName().substring(0, file.getName().length()-4));
		}

		for (String map : maps)
			System.out.println("Found Map: " + map);

		return maps;
	}
}
