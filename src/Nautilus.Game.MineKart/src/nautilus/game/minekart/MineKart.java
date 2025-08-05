package nautilus.game.minekart;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import me.chiss.Core.Config.Config;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.MemoryFix.MemoryFix;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.Plugin.IPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.command.CommandCenter;
import mineplex.core.common.CurrencyType;
import mineplex.core.creature.Creature;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import mineplex.core.fakeEntity.FakeEntity;
import mineplex.core.fakeEntity.FakeEntityManager;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.pet.PetManager;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.punish.Punish;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.ServerListener;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.Updater;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;

import nautilus.game.minekart.gp.GPManager;
import nautilus.game.minekart.kart.KartManager;
import nautilus.game.minekart.menu.KartMenu;
import nautilus.game.minekart.repository.KartRepository;
import nautilus.game.minekart.shop.KartShop;
import nautilus.game.minekart.track.TrackManager;
import nautilus.game.minekart.track.TrackProcessor;
import nautilus.minecraft.core.INautilusPlugin;
import net.minecraft.server.v1_6_R2.EntityPlayer;

import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
 
public class MineKart extends JavaPlugin implements INautilusPlugin, IPlugin, Listener
{
	private String WEB_CONFIG = "webServer";
	
	//Modules
	private ModuleManager _moduleManager;
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private Config _config;
	private Utility _utility;
	private BlockRegenerate _blockRegenerate;
	private BlockRestore _blockRestore;
	private Blood _blood;
	private Creature _creature;
	private Energy _energy;
	private Fire _fire;
	private Logger _logger;
	private LootFactory _lootFactory;
	private Observer _observer;
	private me.chiss.Core.Server.Server _serverModule;
	private Spawn _spawn;
	private Teleport _teleport;
	private ProjectileManager _throw;
	private NpcManager _npcManager;
	
	private GPManager _gpManager;

	private ServerListener _serverListener;

	private Location _spawnLocation;
	
	private FakeEntity _chicken;
	private FakeEntity _wolf;
	private FakeEntity _pig;
	private FakeEntity _spider;
	private FakeEntity _sheep;
	private FakeEntity _cow;
	private FakeEntity _golem;
	private FakeEntity _blaze;
	private FakeEntity _enderman;
	
	@Override
	public void onEnable()
	{
		ClearRaceFolders();
		
		getConfig().addDefault(WEB_CONFIG, "http://bettermc.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();

		_spawnLocation = new Location(this.getServer().getWorlds().get(0), 8.5, 17, -22.5, 0f, 0f);
		
		_clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
		CommandCenter.Initialize(this, _clientManager);
		FakeEntityManager.Initialize(this);
		_donationManager = new DonationManager(this, GetWebServerAddress());
		Recharge.Initialize(this);
		
		//Init Modules
		GetModules();
		GetCreature();
		
		new Punish(this, GetWebServerAddress());
		new Explosion(this, _blockRestore);

		GetServer();  
		GetTeleport();

		//Unreferenced Modules
		new AntiStack(this);
		//new Chat(this, GetClans(), _repository);
		new JoinQuit();
		
		ItemStackFactory.Initialize(this, true);
		
		//Kart
		_gpManager = new GPManager(this, _donationManager, GetTeleport(), Recharge.Instance, new KartManager(this, Recharge.Instance), new TrackManager(this, GetTeleport()));
		new TrackProcessor();
		
		//Updates
		new Updater(this);

		//_serverListener = new ServerListener(GetWebServerAddress(), getServer().getIp(), getServer().getPort() + 1);

		//new TabLobbyList(this, playerNamer.PacketHandler, _clientManager, _donationManager, true);
		
		FakeEntityManager.Instance.SetPacketHandler(new PacketHandler(this));
		DonationManager donationManager = new DonationManager(this, GetWebServerAddress());

		_npcManager = new NpcManager(this, _creature);
		KartFactory _kartFactory = new KartFactory(this, new KartRepository(GetWebServerAddress()));
		new KartShop(_kartFactory, _clientManager, donationManager, CurrencyType.Gems);
		new KartMenu(_kartFactory, _clientManager, donationManager, _gpManager);
		
		new MemoryFix(this);
		
		getServer().getPluginManager().registerEvents(this,  this);
		
		CreateFakeKarts();
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().teleport(GetSpawnLocation());
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		event.getPlayer().setFoodLevel(20);
		event.getPlayer().setHealth(20d);
		ShowFakeKarts(event.getPlayer());
	}

	@EventHandler
	public void PreventFoodChange(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player && !_gpManager.InGame((Player)event.getEntity()))
		{
			event.setCancelled(true);
		}
	}
	
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        if (!event.getPlayer().isOp())
        {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event)
    {
        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void PreventDrop(PlayerDropItemEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void BurnCancel(BlockBurnEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void SpreadCancel(BlockFromToEvent event)
    {
    	event.setCancelled(true);
    }

    @EventHandler
    public void GrowCancel(BlockGrowEvent event)
    {
    	event.setCancelled(true);
    }
	
	@Override
	public void onDisable()
	{
		GetModules().onDisable();
		_serverListener.Shutdown();
	}

	@Override
	public JavaPlugin GetPlugin() 
	{
		return this;
	}

	@Override
	public String GetWebServerAddress()  
	{
		return getConfig().getString(WEB_CONFIG);
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
	public Energy GetEnergy()  
	{
		if (_energy == null)
			_energy = new Energy(this);

		return _energy;
	}
	
	@Override
	public Fire GetFire()  
	{
		if (_fire == null)
			_fire = new Fire(this, new ConditionManager(this), new DamageManager(this, new CombatManager(this), _npcManager));

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
			_serverModule = new me.chiss.Core.Server.Server(this,_clientManager);

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
		return _spawnLocation;
	}
	
	private void CreateFakeKarts()
	{
		_chicken = new FakeEntity(EntityType.CHICKEN, new Location(_spawnLocation.getWorld(), 6.5, 17.5, -39.5, 0f, 0f));
		_wolf = new FakeEntity(EntityType.WOLF, new Location(_spawnLocation.getWorld(), 8.5, 17.5, -39.5, 0f, 0f));
		_pig = new FakeEntity(EntityType.PIG, new Location(_spawnLocation.getWorld(), 10.5, 17.5, -39.5, 0f, 0f));
		_spider = new FakeEntity(EntityType.SPIDER, new Location(_spawnLocation.getWorld(), 6.5, 19.5, -39.5, 0f, 0f));
		_sheep = new FakeEntity(EntityType.SHEEP, new Location(_spawnLocation.getWorld(), 8.5, 19.5, -39.5, 0f, 0f));
		_cow = new FakeEntity(EntityType.COW, new Location(_spawnLocation.getWorld(), 10.5, 19.5, -39.5, 0f, 0f));
		_golem = new FakeEntity(EntityType.IRON_GOLEM, new Location(_spawnLocation.getWorld(), 6.5, 21.5, -39.5, 0f, 0f));
		_blaze = new FakeEntity(EntityType.BLAZE, new Location(_spawnLocation.getWorld(), 8.5, 21.5, -39.5, 0f, 0f));
		_enderman = new FakeEntity(EntityType.ENDERMAN, new Location(_spawnLocation.getWorld(), 10.5, 21.5, -39.5, 0f, 0f));
	}
	
	private void ShowFakeKarts(Player player)
	{
		EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		
		mcPlayer.playerConnection.sendPacket(_chicken.Spawn());
		mcPlayer.playerConnection.sendPacket(_wolf.Spawn());
		mcPlayer.playerConnection.sendPacket(_pig.Spawn());
		mcPlayer.playerConnection.sendPacket(_spider.Spawn());
		mcPlayer.playerConnection.sendPacket(_sheep.Spawn());
		mcPlayer.playerConnection.sendPacket(_cow.Spawn());
		mcPlayer.playerConnection.sendPacket(_golem.Spawn());
		mcPlayer.playerConnection.sendPacket(_blaze.Spawn());
		mcPlayer.playerConnection.sendPacket(_enderman.Spawn());
	}
	
	private void ClearRaceFolders()
	{
		File mainDirectory = new File(".");
	    
	    FileFilter statsFilter = new FileFilter() 
	    {
			@Override
			public boolean accept(File arg0)
			{
				return arg0.isDirectory() && arg0.getName().contains("-");
			}
	    };
	    
	    for (File f : mainDirectory.listFiles(statsFilter))
	    {
	    	try
			{
				FileDeleteStrategy.FORCE.delete(f);
				
			} 
	    	catch (IOException e)
			{
				System.out.println("Error deleting " + f.getName() + " on startup.");
			}
	    }
	}

	@Override
	public PetManager GetPetManager()
	{
		return null;
	}
}
