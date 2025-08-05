package nautilus.game.lobby;

import java.util.HashSet;
import java.util.Map.Entry;

import me.chiss.Core.MemoryFix.MemoryFix;
import nautilus.game.lobby.ServerMenu.ServerInfoManager;
import nautilus.game.lobby.gamequeue.QueueManager;
import nautilus.game.lobby.gamequeue.menu.QueueMenu;
import nautilus.minecraft.core.INautilusPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Chat.Chat;
import me.chiss.Core.Clans.Clans;
import me.chiss.Core.Class.ClassFactory;
import mineplex.core.Fire;
import mineplex.core.Rank;
import mineplex.core.Recharge;
import me.chiss.Core.Combat.CombatManager;
import me.chiss.Core.Commands.CommandCenter;
import me.chiss.Core.Commands.CommandManager;
import me.chiss.Core.Condition.ConditionManager;
import me.chiss.Core.Config.Config;
import me.chiss.Core.Damage.DamageManager;
import me.chiss.Core.Field.Field;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.message.Message;
import mineplex.core.npc.NpcManager;
import mineplex.core.projectile.ProjectileManager;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.NAC.NAC;
import me.chiss.Core.Pet.PetFactory;
import me.chiss.Core.Pet.PetManager;
import me.chiss.Core.Pet.PetShop;
import me.chiss.Core.PlayerTagNamer.INameColorer;
import me.chiss.Core.PlayerTagNamer.PlayerNamer;
import me.chiss.Core.PlayerTagNamer.TabLobbyList;
import me.chiss.Core.Plugin.IChat;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Plugin.IRelation;
import me.chiss.Core.Portal.Portal;
import me.chiss.Core.Shop.CustomBuildShop;
import me.chiss.Core.Shop.DonatorShop;
import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Spawn.Spawn;
import me.chiss.Core.Teleport.Teleport;
import mineplex.core.server.RemoteRepository;
import mineplex.core.server.ServerListener;
import mineplex.core.server.event.PlayerServerAssignmentEvent;
import mineplex.core.server.event.PlayerVoteEvent;
import mineplex.core.updater.Updater;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.creature.Creature;
import mineplex.core.creature.event.CreatureSpawnCustomEvent;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import me.chiss.Core.Weapon.WeaponFactory;
import mineplex.minecraft.account.CoreClient;
import mineplex.minecraft.account.CoreClientManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.classcombat.item.ItemFactory;
import mineplex.minecraft.game.core.mechanics.Gameplay;
import mineplex.minecraft.game.core.mechanics.Weapon;
import mineplex.minecraft.punish.Punish;
import mineplex.minecraft.punish.PunishChatEvent;

public class Lobby extends JavaPlugin implements INautilusPlugin, IPlugin, Listener, IChat, INameColorer
{
	private String WEB_CONFIG = "webServer";
	
	private ModuleManager _moduleManager;
	private Config _config;
	private CoreClientManager _clientManager;
	private Utility _utility;
	private BlockRegenerate _blockRegenerate;
	private BlockRestore _blockRestore;
	private Blood _blood;
	private Clans _clans;
	private ClassFactory _classFactory;
	private ConditionManager _condition;
	private Creature _creature;
	private DamageManager _damage;
	private Energy _energy;
	private Field _field;
	private Explosion _explosion;
	private Fire _fire;
	private Ignore _ignore;
	private Logger _logger;
	private Observer _observer;
	private PetManager _petManager;
	private Recharge _recharge;
	private me.chiss.Core.Server.Server _serverModule;
	private SkillFactory _skillFactory;
	private Spawn _spawn;
	private Teleport _teleport;
	private Weapon _weapon;
	private Wiki _wiki;

	//Interfaces
	private IRelation _relation;

	//Repo
	private RemoteRepository _repository;

	private DonatorShop _donationShop;
	private CustomBuildShop _customBuildShop;
	
	private Location _spawnLocation;
	private ServerListener _serverListener;
	
	private Portal _portal;
	
	@Override
	public void onEnable()
	{  
		getConfig().addDefault(WEB_CONFIG, "http://bettermc.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();
		
		//Repo
		_repository = new RemoteRepository(GetWebServerAddress());

		CommandCenter.Initialize(this);
		
		CombatManager.Initialize(this);
		
		//Init Modules
		GetModules();

		new Punish(this, GetWebServerAddress());
		GetBlood();
		GetClasses();
		GetClients();
		GetCreature();
		GetCondition();
		GetDamage();
		GetField();
		GetEnergy();
		GetExplosion();
		GetFire();
		
		new Give(this);
		
		new Message(this);
		
		new NAC(this, _repository);
		
		GetObserver();
		GetServer();  
		GetSkills();
		//GetSpawn();
		GetTeleport();
		GetWeapon();
		//GetWiki();

		//Unreferenced Modules
		new AntiStack(this);
		new Chat(this, this, _repository);
		new CommandManager(this);
		new Compass(this);
		new Explosion(this);
		new Firework(this);
		new Gameplay(this);
		new mineplex.minecraft.game.core.mechanics.Gameplay(this);
		new NpcManager(this);
		new PlayerInfo(this);
		new Tester(this);
		new MemoryFix(this);
		
		_petManager = new PetManager(this, _repository, GetCreature(), GetClients());
		
		new PetShop(this, _repository, GetClients(), new PetFactory(_repository), GetPetManager());
		
		new LootFactory(this);

		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);

		new TabLobbyList(this, new PlayerNamer(this, GetNameColorer()).PacketHandler, true);
		
		_portal = new Portal(this);
		new ServerInfoManager(this, _portal);
		
		getServer().getPluginManager().registerEvents(this,  this);
		
		ItemStackFactory.Initialize(this);
        WeaponFactory.Initialize(this, GetWebServerAddress());
        ItemFactory.Initialize(this, _repository, new HashSet<String>());
        new QueueMenu(new QueueManager(this, GetWebServerAddress()));
        
        _donationShop = new DonatorShop(this, _repository, GetClients(), GetClasses(), GetSkills(), WeaponFactory.Instance, ItemFactory.Instance);
        _customBuildShop = new CustomBuildShop(this, _repository, GetClients(), GetClasses(), GetSkills(), WeaponFactory.Instance, ItemFactory.Instance);
        
        _spawnLocation = new Location(this.getServer().getWorlds().get(0), 40.5, 18, 44.5, 180f, 0f);
        
        _serverListener = new ServerListener(GetWebServerAddress(), getServer().getIp(), getServer().getPort() + 1);
        _serverListener.start();
	}
	
    public void HandleChat(final AsyncPlayerChatEvent event, final String filteredMessage) 
    {
		PunishChatEvent chatEvent = new PunishChatEvent(event.getPlayer());
		
		getServer().getPluginManager().callEvent(chatEvent);
		
		if (chatEvent.isCancelled())
			return;
		
        final Player sender = event.getPlayer();
        String message = event.getMessage();

        if (message.length() < 1)
            return;

        StringBuilder playerNameBuilder = new StringBuilder();
        
        if (GetClients().GetNull(sender.getName()) != null)
        {
        	
        	CoreClient client = GetClients().GetNull(sender.getName());
        	String prefixChar = "*";
        	
        	if (client.NAC().IsUsing())
        	{
        		playerNameBuilder.append(ChatColor.GREEN + prefixChar);
        	}
        	else
        	{
        		playerNameBuilder.append(ChatColor.DARK_GRAY + prefixChar);
        	}            	

        	if (client.Rank().Has(Rank.OWNER, false))
        	{
        		playerNameBuilder.append(ChatColor.DARK_RED);
        	}
        	else if (client.Rank().Has(Rank.MODERATOR, false))
        	{
        		playerNameBuilder.append(ChatColor.RED);
        	}
        	else if (client.Rank().Has(Rank.DIAMOND, false))
        	{
        		playerNameBuilder.append(ChatColor.AQUA);
        	}
        	else if (client.Rank().Has(Rank.EMERALD, false))
        	{
        		playerNameBuilder.append(ChatColor.GREEN);
        	}
        	else if (client.Donor().HasDonated())
        	{
        		playerNameBuilder.append(ChatColor.GOLD);
        	}
        	else
        	{
        		playerNameBuilder.append(ChatColor.YELLOW);
        	}

            event.setFormat(playerNameBuilder.toString() + "%1$s " + C.cWhite + "%2$s");
            
            GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
            {
            	public void run()
            	{
		            for (Player player : event.getRecipients())
		            {
		            	CoreClient client = GetClients().GetNull(sender.getName());
		            	
		            	if (client != null)
		            	{
		            		player.sendMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), client.Game().GetFilterChat() ? filteredMessage : event.getMessage()));
		            	}
		            }
            	}
            });
        }
    }

	@EventHandler
	public void PlayerAssignedToServer(PlayerServerAssignmentEvent event)
	{
		_portal.SendPlayerToServer(getServer().getPlayer(event.GetPlayerName()), event.GetServerName());
	}
    
    @EventHandler
    public void OnEntityDamage(EntityDamageEvent event)
    {
    	event.setCancelled(true);
    	
    	if (event.getCause() == DamageCause.VOID)
    	{
    		event.getEntity().teleport(GetSpawnLocation());
    		
    		if (event.getEntity() instanceof Player)
    			GivePlayerLobbyItems((Player)event.getEntity());
    	}
    }
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        if (!event.getPlayer().isOp())
        {
        	event.getPlayer().teleport(GetSpawnLocation());
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
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void OnWeatherChange(WeatherChangeEvent event) 
    {
        if (event.toWeatherState())
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void DecayCancel(LeavesDecayEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerFoodBarChange(FoodLevelChangeEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
    	if (event.getRightClicked() instanceof LivingEntity && ((LivingEntity)event.getRightClicked()).isCustomNameVisible() && ((LivingEntity)event.getRightClicked()).getCustomName() != null)    		
    	{
    		if (ChatColor.stripColor(((LivingEntity)event.getRightClicked()).getCustomName()).equalsIgnoreCase("Right-Click to Join - Domination"))
    		{
    			_portal.SendPlayerToServer(event.getPlayer(), "dom");
    		}
    		else if (ChatColor.stripColor(((LivingEntity)event.getRightClicked()).getCustomName()).equalsIgnoreCase("Right-Click to Join - Tutorial"))
    		{
    			_portal.SendPlayerToServer(event.getPlayer(), "tut");
    		}
    		else if (ChatColor.stripColor(((LivingEntity)event.getRightClicked()).getCustomName()).equalsIgnoreCase("Right-Click to Join - Survival Pvp"))
    		{
    			_portal.SendPlayerToServer(event.getPlayer(), "pvp");
    		}
    		else if (ChatColor.stripColor(((LivingEntity)event.getRightClicked()).getCustomName()).equalsIgnoreCase("Right-Click to Join - MarioKart64"))
    		{
    			_portal.SendPlayerToServer(event.getPlayer(), "mk64");
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.PHYSICAL)
        {
            if (event.getClickedBlock().getType() == Material.SOIL)
            {
                event.setCancelled(true);
                return;
            }
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (_donationShop.ShouldOpenShop(event.getClickedBlock()))
            {
            	_donationShop.OpenShopForPlayer(event.getPlayer());
                event.setCancelled(true);
            }
            else if (_customBuildShop.ShouldOpenShop(event.getClickedBlock()))
            {
            	_customBuildShop.OpenShopForPlayer(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnCustomEvent event)
    {
    	event.setCancelled(true);
    }
	
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().teleport(GetSpawnLocation());
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		GivePlayerLobbyItems(event.getPlayer());
	}
	
	@EventHandler
	public void OnPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);
	}

    protected void GivePlayerLobbyItems(Player player)
    {
    	CoreClient client = GetClients().Get(player);
    	
    	player.getInventory().clear();
    	player.getInventory().setArmorContents(new ItemStack[4]);
        
        UpdatePlayerLobbyItemBalances(client);
        
        for (Entry<EntityType, String> petToken : client.Donor().GetPets().entrySet())
        {
        	ItemStack petEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte)petToken.getKey().getTypeId()); 
        	ItemMeta meta = petEgg.getItemMeta();
        	meta.setDisplayName(ChatColor.GREEN + petToken.getValue());
        	
        	petEgg.setItemMeta(meta);
        	player.getInventory().addItem(petEgg);
        }
        
        int nameTagCount = client.Donor().GetPetNameTagCount();
        
        if (nameTagCount > 0)
        {
	        ItemStack nameTags = new ItemStack(Material.SIGN, client.Donor().GetPetNameTagCount());
	        ItemMeta meta = nameTags.getItemMeta();
	        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Name Tag");	
	        nameTags.setItemMeta(meta);
	        player.getInventory().addItem(nameTags);
        }
    }
	
    public void UpdatePlayerLobbyItemBalances(CoreClient client)
    {

    }
    
    @EventHandler
    public void onPlayerVote(PlayerVoteEvent event)
    {
    	CoreClient client = GetClients().GetNull(event.GetPlayerName());
    	
    	if (client != null)
    	{
    		UpdatePlayerLobbyItemBalances(client);
    	}
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
	public org.bukkit.Server GetRealServer()
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
	public IRelation GetRelation()
	{
		return _relation;
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
	public CoreClientManager GetClients() 
	{
		if (_clientManager == null)
			_clientManager = new CoreClientManager(this, new me.chiss.Core.Plugin.Logger(), _repository);  

		return _clientManager;
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
	public Clans GetClans() 
	{
		if (_clans == null)
			_clans = new Clans(this, _repository, "Lobby");

		return _clans;
	}

	@Override
	public ClassFactory GetClasses() 
	{
		if (_classFactory == null)
			_classFactory = new ClassFactory(this, _repository, GetSkills());

		return _classFactory;
	}

	@Override
	public ConditionManager GetCondition() 
	{
		if (_condition == null)
			_condition = new ConditionManager(this);

		return _condition;
	}

	@Override
	public Creature GetCreature() 
	{
		if (_creature == null)
			_creature = new Creature(this);

		return _creature;
	}

	@Override
	public DamageManager GetDamage()  
	{
		if (_damage == null)
			_damage = new DamageManager(this, CombatManager.Instance);

		return _damage;
	}

	@Override
	public Energy GetEnergy()  
	{
		if (_energy == null)
			_energy = new Energy(this);

		return _energy;
	}

	@Override
	public Explosion GetExplosion()  
	{
		if (_explosion == null)
			_explosion = new Explosion(this);

		return _explosion;
	}

	@Override
	public Field GetField()
	{
		if (_field == null)
			_field = new Field(this, _repository, "Lobby");

		return _field;
	}

	@Override
	public Fire GetFire()  
	{
		if (_fire == null)
			_fire = new Fire(this);

		return _fire;
	}

	@Override
	public Ignore GetIgnore() 
	{
		if (_ignore == null)
			_ignore = new Ignore(this, _repository);

		return _ignore;
	}
	
	@Override
	public Logger GetLogger()  
	{
		if (_logger == null)
			_logger = new Logger(this);

		return _logger;
	}

	@Override
	public Observer GetObserver()  
	{
		if (_observer == null)
			_observer = new Observer(this);

		return _observer;
	}

	@Override
	public Recharge GetRecharge()  
	{
		if (_recharge == null)
			_recharge = new Recharge(this);

		return _recharge;
	}

	@Override
	public me.chiss.Core.Server.Server GetServer() 
	{
		if (_serverModule == null)
			_serverModule = new me.chiss.Core.Server.Server(this);

		return _serverModule;
	}

	@Override
	public SkillFactory GetSkills()  
	{
		if (_skillFactory == null)
			_skillFactory = new SkillFactory(this, _repository);

		return _skillFactory;
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
			_teleport = new Teleport(this);

		return _teleport;
	}

    @Override
    public ProjectileManager GetThrow()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public Weapon GetWeapon()  
	{
		if (_weapon == null)
			_weapon = new Weapon(this);

		return _weapon;
	}

	@Override
	public Wiki GetWiki()   
	{
		if (_wiki == null)
			_wiki = new Wiki(this, _repository);

		return _wiki;
	}

	@Override
	public Location GetSpawnLocation() 
	{
		return _spawnLocation;
	}

	@Override
	public INameColorer GetNameColorer()
	{
		return this;
	}

	@Override
	public PetManager GetPetManager()
	{
		return _petManager;
	}

	@Override
	public LootFactory GetLoot()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public ChatColor GetColorOfFor(String other, Player player)
    {
    	ChatColor prefixColor = null;
    	
        if (GetClients().GetNull(other) != null && GetClients().Get(other).Rank().Has(Rank.ADMIN, false))
        {
        	prefixColor = ChatColor.DARK_RED; 
        }
        else if (GetClients().GetNull(other) != null && GetClients().Get(other).Rank().Has(Rank.MODERATOR, false))
        {
        	prefixColor = ChatColor.RED; 
        }
        else if (GetClients().GetNull(other) != null && GetClients().Get(other).Donor().HasDonated())
        {
        	prefixColor = ChatColor.YELLOW; 
        }
        else
        {
        	prefixColor = ChatColor.YELLOW; 
        }
        
        return prefixColor;
    }
}
