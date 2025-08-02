package nautilus.game.pvp;

import java.util.HashSet;

import me.chiss.Core.Class.ClassFactory;
import me.chiss.Core.Config.Config;
import me.chiss.Core.Field.Field;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.MemoryFix.MemoryFix;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.NAC.NAC;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Plugin.IRelation;
import me.chiss.Core.Scheduler.Scheduler;
import mineplex.core.message.Message;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.TabLobbyList;
import mineplex.core.pet.PetFactory;
import mineplex.core.pet.PetManager;
import mineplex.core.pet.PetShop;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.ServerListener;
import mineplex.core.server.event.PlayerVoteEvent;
import mineplex.core.updater.Updater;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.chat.Chat;
import mineplex.core.command.CommandCenter;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.creature.Creature;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.minecraft.game.core.mechanics.Weapon;
import mineplex.minecraft.punish.Punish;
import nautilus.game.pvp.modules.Farming;
import nautilus.game.pvp.modules.Gameplay;
import nautilus.game.pvp.modules.Recipes;
import nautilus.game.pvp.modules.ShopManager;
import nautilus.game.pvp.modules.SoundTest;
import nautilus.game.pvp.modules.TreeRemover;
import nautilus.game.pvp.modules.WorldBorder;
import nautilus.game.pvp.modules.Benefit.BenefitManager;
import nautilus.game.pvp.modules.Fishing.FishManager;
import nautilus.game.pvp.modules.clans.Clans;
import nautilus.game.pvp.modules.serverreset.ServerReset;
import nautilus.game.pvp.repository.PvPRepository;
import nautilus.game.pvp.worldevent.EventManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
 
public class PvP extends JavaPlugin implements IPlugin, Listener
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
	private ProjectileManager _throw;
	private Weapon _weapon;
	private Wiki _wiki;

	//Interfaces
	private IRelation _relation;

	//Repo
	private PvPRepository _repository;
	
	private ServerListener _serverListener;

	@Override
	public void onEnable()
	{
		getConfig().addDefault(WEB_CONFIG, "http://www.nautmc.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();
		
		//Repo
		_repository = new PvPRepository(GetWebServerAddress());

		//Init Modules
		GetModules();

		_clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
		CommandCenter.Initialize(this, _clientManager);
		
		CombatManager.Initialize(this);
		ItemStackFactory.Initialize(this);
		
		new Punish(this, GetWebServerAddress());
		GetBlood();
		GetClans();
		GetClasses();
		GetClients();
		GetCreature();
		GetCondition();
		GetEnergy();
		GetExplosion();
		GetField();
		GetFire();
		
		new Fix(this);
		new Give(this);
		
		GetIgnore();
		GetLoot();
		
		new Message(this);
		new NAC(this, _repository);
		
		GetObserver();
		GetServer();  
		GetWeapon();
		//GetWiki();

		_petManager = new PetManager(this, GetWebServerAddress(), GetCreature(), GetClients());
		
		//Unreferenced Modules
		new AntiStack();
		new Chat(this, _clientManager, GetClans());
		new EventManager(this);
		new Farming(this);
		new Firework(this);
		Scheduler.Initialize(this, _repository);
		FishManager.Initialize(this, Scheduler.Instance, _repository);		
		new Gameplay(this);
		new mineplex.minecraft.game.core.mechanics.PistonJump(this);
		new Information(this);
		new PlayerInfo(this);
		new PointManager(this, Scheduler.Instance, _repository, 4000, 150, 80, 8, 80, 8, 300);
		new PetShop(this, _repository, GetClients(), new PetFactory(_repository), GetPetManager());
		new Quit(this);
		new Recipes(this);
		new SoundTest(this);
		new ServerReset(this, GetWebServerAddress());
		new Tester(this);	
		new WorldBorder(this); 
		new MemoryFix(this);
		
		//Remove Skills
		_skillFactory.RemoveSkill("Longshot", "Heavy Arrows");
		_skillFactory.RemoveSkill("Dwarf Toss", "Flesh Hook");
		_skillFactory.RemoveSkill("Fissure", null);
		
		//Activate Class Save
		//GetClasses().GetRestore().Activate();

		new Portal(this);
		
		//Shops
		new ShopManager(this, _repository, new BenefitManager(this, GetWebServerAddress(), GetEnergy()));
		new NpcManager(this);
		
		//Items
		HashSet<String> itemIgnore = new HashSet<String>();
		itemIgnore.add("Bolas");
		itemIgnore.add("Shuriken");
		ItemFactory.Initialize(this, _repository, itemIgnore);

		//Set Relation
		_relation = GetClans();

		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);

		new TabLobbyList(this, _clientManager, new PlayerNamer(this, GetNameColorer()).PacketHandler, true);
		
		new TreeRemover(this);
		
        _serverListener = new ServerListener(GetWebServerAddress(), getServer().getIp(), getServer().getPort() + 1);
        _serverListener.start();
        
        this.getServer().getPluginManager().registerEvents(this, this);
	}

    @EventHandler
    public void onPlayerVote(PlayerVoteEvent event)
    {
    	CoreClient client = _clientManager.Get(event.GetPlayerName());
    	
		if (client != null)
		{
			client.Donor().AddPoints(event.GetPointsReceived());
		
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().sendMessage(C.cDGreen + "           Thanks for voting!");
    		client.GetPlayer().sendMessage(C.cDGreen + "       You received " + ChatColor.YELLOW + event.GetPointsReceived() + C.cDGreen + " points! ");
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().playSound(client.GetPlayer().getLocation(), Sound.LEVEL_UP, .3f, 1f);
    		
    		for (Player player : getServer().getOnlinePlayers())
    		{
    			if (player == client.GetPlayer())
    				continue;
    			
    			player.sendMessage(F.main("Vote", ChatColor.YELLOW + event.GetPlayerName() + ChatColor.GRAY + " voted at bettermc.com/Vote for " + ChatColor.YELLOW + event.GetPointsReceived() + C.cGray + " points! "));
    		}
		}
    }
	
	@Override 
	public void onDisable()
	{
		GetModules().onDisable();
		_serverListener.Shutdown();
	}
	
	@EventHandler
	public void SpawnTeleport(PlayerInteractEvent event)
	{	
		Player player = event.getPlayer();

		if (!UtilEvent.isAction(event, ActionType.R_BLOCK))
			return;

		if (event.getClickedBlock().getType() != Material.ENDER_PORTAL_FRAME)
			return;

		event.setCancelled(true);
		
		if (spawnList.isEmpty())
			return;

		int closestIndex = 0;
		double dist = 99999;
		
		for (int i=0 ; i<spawnList.size() ; i++)
		{
			if (UtilMath.offset(spawnList.get(i), player.getLocation()) < dist)
			{
				closestIndex = i;
				dist = UtilMath.offset(spawnList.get(i), player.getLocation());
			}
		}
		
		UtilPlayer.message(player, F.main("Spawn", "You teleported to " + F.elem("Spawn " + (closestIndex+1)) + "."));
		Teleport().TP(player, spawnList.get((closestIndex+1)%spawnList.size()));
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
			_clans = new Clans(this, _repository, "PVP");

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
			_field = new Field(this, _repository, "PVP");

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
