package nautilus.game.tutorial;

import java.util.HashSet;

import me.chiss.Core.Clans.Clans;
import me.chiss.Core.Class.ClassFactory;
import me.chiss.Core.Combat.CombatManager;
import me.chiss.Core.Commands.CommandCenter;
import me.chiss.Core.Commands.CommandManager;
import me.chiss.Core.Condition.ConditionManager;
import me.chiss.Core.Config.Config;
import me.chiss.Core.Damage.DamageManager;
import me.chiss.Core.Field.Field;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.creature.Creature;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.message.Message;
import mineplex.core.npc.NpcManager;
import mineplex.core.projectile.ProjectileManager;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.MemoryFix.MemoryFix;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.Pet.PetManager;
import me.chiss.Core.PlayerTagNamer.INameColorer;
import me.chiss.Core.PlayerTagNamer.PlayerNamer;
import me.chiss.Core.PlayerTagNamer.TabLobbyList;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Plugin.IRelation;
import me.chiss.Core.Spawn.Spawn;
import me.chiss.Core.Teleport.Teleport;
import mineplex.core.Fire;
import mineplex.core.Recharge;
import mineplex.core.server.RemoteRepository;
import mineplex.core.server.ServerListener;
import mineplex.core.updater.Updater;
import me.chiss.Core.Weapon.WeaponFactory;
import mineplex.minecraft.account.CoreClientManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.classcombat.item.ItemFactory;
import mineplex.minecraft.game.core.mechanics.Gameplay;
import mineplex.minecraft.game.core.mechanics.Weapon;
import mineplex.minecraft.punish.Punish;

import nautilus.game.tutorial.modules.ShopManager;
import nautilus.minecraft.core.INautilusPlugin;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
 
public class Tutorial extends JavaPlugin implements INautilusPlugin, IPlugin
{
	private String WEB_CONFIG = "webServer";
	
	//Modules
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
	private Explosion _explosion;
	private Field _field;
	private Fire _fire;
	private Ignore _ignore;
	private Logger _logger;
	private LootFactory _lootFactory;
	private Observer _observer;
	private PetManager _petManager;
	private Recharge _recharge;
	private me.chiss.Core.Server.Server _serverModule;
	private SkillFactory _skillFactory;
	private Spawn _spawn;
	private Teleport _teleport;
	private ProjectileManager _throw;
	private Weapon _weapon;
	private Wiki _wiki;

	//Interfaces
	private IRelation _relation;

	//Repo
	private RemoteRepository _repository;
	
	private ServerListener _serverListener;

	@Override
	public void onEnable()
	{  
		getConfig().addDefault(WEB_CONFIG, "http://bettermc.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();
		
		//Repo
		_repository = new RemoteRepository(GetWebServerAddress());

		//Commands
		CommandCenter.Initialize(this);
		
		//Init Modules
		CombatManager.Initialize(this);
		ItemFactory.Initialize(this, _repository, new HashSet<String>());
		ItemStackFactory.Initialize(this);
		WeaponFactory.Initialize(this, GetWebServerAddress());
		
		GetModules();

		GetBlood();
		//GetClans();
		GetClasses();
		GetClients();
		GetCreature();
		GetCondition();
		GetDamage();
		GetEnergy();
		GetExplosion();
		GetField();
		GetFire();
		
		new Give(this);
		new Message(this);

		GetObserver();
		GetServer();  
		GetSkills();
		//GetSpawn();
		GetTeleport();
		GetWeapon();
		//GetWiki();

		//Unreferenced Modules
		new AntiStack(this);
		//new Chat(this, GetClans(), _repository);
		new CommandManager(this);
		new Firework(this);
		new Gameplay(this);
		new mineplex.minecraft.game.core.mechanics.Gameplay(this);
		new PlayerInfo(this);
		new Punish(this, GetWebServerAddress());
		new Quit(this);
		new MemoryFix(this);
		
		new TutorialManager(this, _repository);
		
		new ShopManager(this, _repository);
		new NpcManager(this);
		
		//Set Relation
		//_relation = GetClans();

		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);

		new TabLobbyList(this, new PlayerNamer(this, GetNameColorer()).PacketHandler, true);
		
        _serverListener = new ServerListener(GetWebServerAddress(), getServer().getIp(), getServer().getPort() + 1);
        _serverListener.start();
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
			_clans = new Clans(this, _repository, "TUT");

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
			_field = new Field(this, _repository, "Tutorial");

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
		if (_throw == null)
			_throw = new ProjectileManager(this);

		return _throw;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INameColorer GetNameColorer()
	{
		return _clans;
	}

	@Override
	public PetManager GetPetManager()
	{
		return _petManager;
	}
}
