package nautilus.game.arcade;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.CustomTagFix;
import mineplex.core.TablistFix;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antihack.AntiHack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.blood.Blood;
import mineplex.core.chat.Chat;
import mineplex.core.command.CommandCenter;
import mineplex.core.common.util.FileUtil;
import mineplex.core.common.util.UtilServer;
import mineplex.core.cosmetic.CosmeticManager;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.gadget.GadgetManager;
import mineplex.core.give.Give;
import mineplex.core.hologram.HologramManager;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.leaderboard.LeaderboardManager;
import mineplex.core.memory.MemoryFix;
import mineplex.core.message.MessageManager;
import mineplex.core.mount.MountManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.pet.PetManager;
import mineplex.core.preferences.PreferencesManager;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.serverConfig.ServerConfiguration;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.Updater;
import mineplex.core.visibility.VisibilityManager;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.game.GameServerConfig;
import net.minecraft.server.v1_7_R4.BiomeBase;
import net.minecraft.server.v1_7_R4.MinecraftServer;

public class Arcade extends JavaPlugin
{      
	private String WEB_CONFIG = "webServer";

	//Modules   
	private CoreClientManager _clientManager;
	private DamageManager _damageManager;

	private ArcadeManager _gameManager;
	 
	@Override     
	public void onEnable() 
	{
		//Delete Old Games Folders
		DeleteFolders();
 
		//Configs
		getConfig().addDefault(WEB_CONFIG, "http://accounts.mineplex.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();
		
		String webServerAddress = getConfig().getString(WEB_CONFIG);

		//Logger.initialize(this);

		//Static Modules
		CommandCenter.Initialize(this);		
		_clientManager = new CoreClientManager(this, webServerAddress, _gameManager);
		CommandCenter.Instance.setClientManager(_clientManager);

		ItemStackFactory.Initialize(this, false);  
		Recharge.Initialize(this);   
		VisibilityManager.Initialize(this);
		Give.Initialize(this);

		new ServerConfiguration(this, _clientManager);
		
		PreferencesManager preferenceManager = new PreferencesManager(this, _clientManager);

		Creature creature = new Creature(this);
		new LeaderboardManager(this, _clientManager);
		Teleport teleport = new Teleport(this);		
		PacketHandler packetHandler = new PacketHandler(this);
		
		DisguiseManager disguiseManager = new DisguiseManager(this, packetHandler);

		_damageManager = new DamageManager(this, new CombatManager(this), disguiseManager, null);

		AntiHack.Initialize(this, preferenceManager, _clientManager);
		AntiHack.Instance.setKick(false);
		
        Chat chat = new Chat(this, _clientManager, preferenceManager, webServerAddress);
        new MessageManager(this, _clientManager, preferenceManager, chat);
		
		BlockRestore blockRestore = new BlockRestore(this);
		
		ProjectileManager projectileManager = new ProjectileManager(this);
		HologramManager hologramManager = new HologramManager(this);
		
		//Inventory
		PetManager petManager = new PetManager(this, _clientManager, disguiseManager, creature, blockRestore, webServerAddress);
		MountManager mountManager = new MountManager(this, _clientManager, blockRestore, disguiseManager);
		GadgetManager gadgetManager = new GadgetManager(this, _clientManager, mountManager, petManager, preferenceManager, disguiseManager, blockRestore, projectileManager);
		CosmeticManager cosmeticManager = new CosmeticManager(this, _clientManager,  gadgetManager, mountManager, petManager);
		cosmeticManager.setInterfaceSlot(7);
		
		//Arcade Manager  
		_gameManager = new ArcadeManager(this, ReadServerConfig(), _clientManager, _damageManager, disguiseManager, creature, teleport, new Blood(this), chat, preferenceManager, packetHandler, cosmeticManager, projectileManager, petManager, hologramManager, webServerAddress);		
		
		new MemoryFix(this);
		new CustomTagFix(this, packetHandler);
		new TablistFix(this);
		
		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);
		
		MinecraftServer.getServer().getPropertyManager().setProperty("debug", true);

		// Remove nasty biomes from natural terrain generation, used for UHC
		BiomeBase.getBiomes()[BiomeBase.OCEAN.id] = BiomeBase.PLAINS;
		BiomeBase.getBiomes()[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
		BiomeBase.getBiomes()[BiomeBase.SWAMPLAND.id] = BiomeBase.PLAINS;
		BiomeBase.getBiomes()[BiomeBase.RIVER.id] = BiomeBase.PLAINS;
	}

	@Override 
	public void onDisable() 
	{
		for (Player player : UtilServer.getPlayers())
			player.kickPlayer("Server Shutdown");

		if (_gameManager.GetGame() != null)
			if (_gameManager.GetGame().WorldData != null)
				_gameManager.GetGame().WorldData.Uninitialize();
	}

	public GameServerConfig ReadServerConfig() 
	{
		GameServerConfig config = new GameServerConfig();

		try
		{
			config.ServerType = "Minigames";
			config.MinPlayers = 8;
			config.MaxPlayers = 16;
			config.Tournament = false;
			config.TeamRejoin = true;
			config.TeamAutoJoin = true;
			config.TeamForceBalance = true;
			config.GameAutoStart = true;
			config.GameTimeout = false;
			config.RewardGems = false;
			config.RewardItems = false;
			config.RewardStats = false;
			config.RewardAchievements = false;
			config.HotbarInventory = true;
			config.HotbarHubClock = true;
			config.PlayerKickIdle = false;
			}
		catch (Exception ex)
		{
			System.out.println("Error reading ServerConfiguration values : " + ex.getMessage());
		}

		if (!config.IsValid())
			config = GetDefaultConfig();

		return config;
	}

	public GameServerConfig GetDefaultConfig()
	{
		GameServerConfig config = new GameServerConfig();

		config.ServerType = "Minigames";
		config.MinPlayers = 8;
		config.MaxPlayers = 16;
		config.Tournament = false;

		return config;
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
}
