package nautilus.game.core;

import java.util.HashSet;

import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.command.CommandCenter;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.fakeEntity.FakeEntityManager;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.MemoryFix.MemoryFix;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import mineplex.core.message.MessageManager;
import mineplex.core.movement.Movement;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.punish.Punish;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.Server;
import mineplex.core.server.ServerListener;
import mineplex.core.server.ServerTalker;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.FileUpdater;
import mineplex.core.updater.Updater;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Condition.SkillConditionManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.shop.ClassCombatCustomBuildShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatPurchaseShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.combat.*;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.minecraft.game.core.mechanics.PistonJump;
import mineplex.minecraft.game.core.mechanics.Weapon;
import nautilus.game.core.util.NullChunkGenerator;

import org.bukkit.Location;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GamePlugin extends JavaPlugin implements IRelation
{
	private String WEB_CONFIG = "webServer";
	
	private BlockRestore _blockRestore;
	protected ClassManager ClassManager;
	protected CombatManager CombatManager;
	protected ConditionManager ConditionManager;
	protected CoreClientManager ClientManager;
	private Creature _creature;
	private DamageManager _damage;
	protected DonationManager DonationManager;
	protected PacketHandler PacketHandler;
	protected Energy Energy;
	private Fire _fire;
	private ModuleManager _moduleManager;
	protected NpcManager NpcManager;
	protected SkillFactory SkillManager;
	private Spawn _spawn;
	private Teleport _teleport;
	private ProjectileManager _throw;
    
    private ServerListener _serverListener;
    private Location _spawnLocation;
    
    protected ServerTalker HubConnection;

    @Override
    public void onEnable()
    {
		getConfig().addDefault(WEB_CONFIG, "http://api.mineplex.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();
		
		_spawnLocation = new Location(getServer().getWorlds().get(0), -7.5, 18.5, 24.5, 90, 0);
		
		ClientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
    	CommandCenter.Initialize(this, ClientManager);
    	
    	FakeEntityManager.Initialize(this);
    	ItemStackFactory.Initialize(this, true);
    	Recharge.Initialize(this);
    	
        _moduleManager = new ModuleManager();
        
        Updater updater = new Updater(this);
        _creature = new Creature(this);
        Energy = new Energy(this);
        DonationManager = new DonationManager(this, GetWebServerAddress());
        ConditionManager = new SkillConditionManager(this);
        CombatManager = new CombatManager(this);
        _blockRestore = new BlockRestore(this);
        _throw = new ProjectileManager(this);
        _spawn = new Spawn(this);
        _teleport = new Teleport(this, ClientManager, _spawn);
        NpcManager = new NpcManager(this, _creature);
        PacketHandler = new PacketHandler(this);
        _damage = new DamageManager(this, CombatManager, NpcManager, new DisguiseManager(this, PacketHandler));
        _fire = new Fire(this, ConditionManager, _damage);
        new Punish(this, GetWebServerAddress());
                 
        
        SkillManager = new SkillFactory(this, _damage, this, CombatManager, ConditionManager, _throw, _blockRestore, _fire, new Movement(this), _teleport, Energy, GetWebServerAddress());
        ClassManager = new ClassManager(this, ClientManager, DonationManager, SkillManager, GetWebServerAddress());
        new ItemFactory(this, _blockRestore, ClassManager, ConditionManager, _damage, Energy, _fire, _throw, GetWebServerAddress(), new HashSet<String>());
        
        new MessageManager(this, ClientManager);
        
        new Blood(this);
        new JoinQuit();
        new Server();
        new AntiStack(this);
        new MemoryFix(this);
        new PistonJump(this);
        new Weapon(this, Energy);
        new FileUpdater(this, new Portal(this));
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, updater, 1, 1);
        
       // _serverListener = new ServerListener(GetWebServerAddress(), getServer().getIp(), getServer().getPort() + 1);
       // _serverListener.start();
        
        //HubConnection = new ServerTalker(getConfig().getString(HUB_SERVER));
        // HubConnection.start();
        
        //HubConnection.QueuePacket(new ServerReadyPacket(getServer().getIp() + ":" + getServer().getPort()));
        
        ClassShopManager shopManager = new ClassShopManager(this, ClassManager, SkillManager, null);
        new ClassCombatShop(shopManager, ClientManager, DonationManager, "Select Class Here");
        new ClassCombatPurchaseShop(shopManager, ClientManager, DonationManager, "Skill Shop");
        new ClassCombatCustomBuildShop(shopManager, ClientManager, DonationManager, "Class Setup");
    }
    
    protected abstract String GetServerName();
    
    @Override
    public void onDisable()
    {
         _moduleManager.onDisable();
         _serverListener.Shutdown();
    }

	public String GetWebServerAddress() 
	{ 
		return getConfig().getString(WEB_CONFIG);
	}
	
    public Location GetSpawnLocation()
    {
    	return _spawnLocation;
    }

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new NullChunkGenerator();
	}
}