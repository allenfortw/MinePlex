package nautilus.game.arcade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import me.chiss.Core.Config.Config;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.command.CommandCenter;
import mineplex.core.common.util.FileUtil;
import mineplex.core.common.util.UtilServer;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Scheduler.Scheduler;
import mineplex.core.message.MessageManager;
import mineplex.core.monitor.LagMeter;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.pet.PetManager;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.punish.Punish;
import mineplex.core.recharge.Recharge;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.FileUpdater;
import mineplex.core.updater.Updater;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;

import nautilus.game.arcade.game.GameServerConfig;
import nautilus.minecraft.core.INautilusPlugin;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Arcade extends JavaPlugin implements INautilusPlugin, IPlugin
{
	private String WEB_CONFIG = "webServer";

	//Modules
	private ModuleManager _moduleManager;
	private Config _config;
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private DamageManager _damageManager;
	private Utility _utility;
	private BlockRegenerate _blockRegenerate;
	private BlockRestore _blockRestore;
	private Blood _blood;
	private ConditionManager _condition;
	private Creature _creature;
	private Fire _fire;
	private Logger _logger; 
	private LootFactory _lootFactory;
	private Observer _observer;
	private PetManager _petManager; 
	private me.chiss.Core.Server.Server _serverModule;
	private Spawn _spawn;
	private Teleport _teleport;
	private ProjectileManager _throw;

	private ArcadeManager _gameManager;

	@Override 
	public void onEnable()
	{
		//Delete Old Games Folders
		DeleteFolders();

		//Configs
		getConfig().addDefault(WEB_CONFIG, "http://api.mineplex.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig(); 

		//Init Modules
		GetModules();

		_clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
		_donationManager = new DonationManager(this, GetWebServerAddress());
		CommandCenter.Initialize(this, _clientManager);
		ItemStackFactory.Initialize(this, false);
		Recharge.Initialize(this);

		ConditionManager conditionManager = new ConditionManager(this);
 
		new MessageManager(this, _clientManager);

		new AntiStack(this);
		
		GetCreature();
		GetSpawn();
		GetTeleport();
		new FileUpdater(this, new Portal(this));
		new LagMeter(this, _clientManager);
		
		PacketHandler packetHandler = new PacketHandler(this);
		DisguiseManager disguiseManager = new DisguiseManager(this, packetHandler);

		_damageManager = new DamageManager(this, new CombatManager(this), new NpcManager(this, GetCreature()), disguiseManager);

		//Arcade Manager
		_gameManager = new ArcadeManager(this, ReadServerConfig(), _clientManager, _donationManager, conditionManager, _damageManager, disguiseManager, GetCreature(), GetBlood(), packetHandler);

		//Unreferenced Modules
		//new AntiStack();
		Scheduler.Initialize(this);
		//new Information(this);
		new Punish(this, GetWebServerAddress());
		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);
	}


	@Override 
	public void onDisable()
	{
		GetModules().onDisable();

		for (Player player : UtilServer.getPlayers())
			player.kickPlayer("Server Shutdown");

		if (_gameManager.GetGame() != null)
			if (_gameManager.GetGame().WorldData != null)
				_gameManager.GetGame().WorldData.Uninitialize();
	}

	public GameServerConfig ReadServerConfig()
	{
		GameServerConfig config = new GameServerConfig();

		//Load Track Data
		String line = null;

		try
		{
			File file = new File("ArcadeSettings.config");
			if (!file.exists())
				WriteServerConfig(GetDefaultConfig());

			FileInputStream fstream = new FileInputStream("ArcadeSettings.config");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			while ((line = br.readLine()) != null)  
			{
				String[] tokens = line.split("=");

				if (tokens.length < 2)
					continue;

				if (tokens[0].equals("SERVER_TYPE"))
				{
					config.ServerType = tokens[1];
				}
				else if (tokens[0].equals("PLAYERS_MIN"))
				{
					config.MinPlayers = Integer.parseInt(tokens[1]);
				}
				else if (tokens[0].equals("PLAYERS_MAX"))
				{
					config.MaxPlayers = Integer.parseInt(tokens[1]);
				}
				//Games
				else
				{
					try
					{
						GameType type = GameType.valueOf(tokens[0]);
						boolean enabled = Boolean.valueOf(tokens[1]);

						if (enabled)
							config.GameList.add(type);
					}
					catch (Exception e)
					{

					}
				}

			}

			in.close();
		}
		catch (Exception e)
		{

		}

		if (!config.IsValid())
			config = GetDefaultConfig();
		
		WriteServerConfig(config);
		return config;
	}

	public GameServerConfig GetDefaultConfig()
	{
		GameServerConfig config = new GameServerConfig();

		config.ServerType = "Minigames";
		config.MinPlayers = 8;
		config.MaxPlayers = 16;

		return config;
	}

	public void WriteServerConfig(GameServerConfig config)
	{
		try
		{
			FileWriter fstream = new FileWriter("ArcadeSettings.config");
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("SERVER_TYPE=" + config.ServerType + "\n");
			out.write("PLAYERS_MIN=" + config.MinPlayers + "\n");
			out.write("PLAYERS_MAX=" + config.MaxPlayers + "\n");
			out.write("\n\nGames List;\n");

			for (GameType type : GameType.values())
			{
				out.write(type.toString() + "=" + config.GameList.contains(type) + "\n");
			}

			out.close();
		}
		catch (Exception e)
		{

		}
	}

	private void DeleteFolders() 
	{
		File curDir = new File(".");

		File[] filesList = curDir.listFiles();
		for(File file : filesList)
		{
			if (!file.isDirectory())
				continue;

			if (file.getName().length() < 4)
				continue;

			if (!file.getName().substring(0, 4).equalsIgnoreCase("Game"))
				continue;

			FileUtil.DeleteFolder(file);

			System.out.println("Deleted Old Game: " + file.getName());
		}
	}

	@Override
	public JavaPlugin GetPlugin() 
	{
		return this;
	}

	@Override
	public String GetWebServerAddress() 
	{ 
		String webServerAddress = getConfig().getString(WEB_CONFIG);

		return webServerAddress;
	}

	@Override
	public Server GetRealServer()
	{
		return getServer();
	}

	@Override
	public PluginManager GetPluginManager() 
	{
		return GetRealServer().getPluginManager();
	}

	@Override
	public void Log(String moduleName, String data) 
	{
		System.out.println(moduleName + " : " + data);   
	}

	@Override
	public ModuleManager GetModules() 
	{
		if (_moduleManager == null)
			_moduleManager = new ModuleManager();

		return _moduleManager;
	}

	@Override
	public Config GetConfig() 
	{
		if (_config == null)
			_config = new Config(this);

		return _config;
	}

	@Override
	public Utility GetUtility() 
	{
		if (_utility == null)
			_utility = new Utility(this);

		return _utility;
	}

	@Override
	public BlockRegenerate GetBlockRegenerate() 
	{
		if (_blockRegenerate == null)
			_blockRegenerate = new BlockRegenerate(this);

		return _blockRegenerate;
	}

	@Override
	public BlockRestore GetBlockRestore() 
	{
		if (_blockRestore == null)
			_blockRestore = new BlockRestore(this);

		return _blockRestore;
	}

	@Override
	public Blood GetBlood() 
	{
		if (_blood == null)
			_blood = new Blood(this);

		return _blood;
	}

	@Override
	public Creature GetCreature() 
	{
		if (_creature == null)
			_creature = new Creature(this);

		return _creature;
	}


	@Override
	public Fire GetFire()  
	{
		if (_fire == null)
			_fire = new Fire(this, _condition, _damageManager);

		return _fire;
	}

	@Override
	public Logger GetLogger()  
	{
		if (_logger == null)
			_logger = new Logger(this);

		return _logger;
	}

	@Override
	public LootFactory GetLoot()  
	{
		if (_lootFactory == null)
			_lootFactory = new LootFactory(this);

		return _lootFactory;
	}

	@Override
	public Observer GetObserver()  
	{
		if (_observer == null)
			_observer = new Observer(this);

		return _observer;
	}

	@Override
	public me.chiss.Core.Server.Server GetServer() 
	{
		if (_serverModule == null)
			_serverModule = new me.chiss.Core.Server.Server(this, _clientManager);

		return _serverModule;
	}

	@Override
	public Spawn GetSpawn()  
	{
		if (_spawn == null)
			_spawn = new Spawn(this);

		return _spawn;
	}

	@Override
	public Teleport GetTeleport()  
	{
		if (_teleport == null)
			_teleport = new Teleport(this, _clientManager, GetSpawn());

		return _teleport;
	}

	@Override
	public ProjectileManager GetThrow()   
	{
		if (_throw == null)
			_throw = new ProjectileManager(this);

		return _throw;
	}

	@Override
	public Location GetSpawnLocation() 
	{
		return null;
	}

	@Override
	public PetManager GetPetManager()
	{
		return _petManager;
	}

	@Override
	public Energy GetEnergy() 
	{
		return null;
	}
}
