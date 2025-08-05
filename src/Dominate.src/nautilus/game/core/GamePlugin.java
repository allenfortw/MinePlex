package nautilus.game.core;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import me.chiss.Core.MemoryFix.MemoryFix;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.Blood;
import me.chiss.Core.Modules.JoinQuit;
import mineplex.core.account.CoreClientManager;
import mineplex.core.antistack.AntiStack;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.command.CommandCenter;
import mineplex.core.creature.Creature;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.message.MessageManager;
import mineplex.core.monitor.LagMeter;
import mineplex.core.movement.Movement;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.punish.Punish;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.ServerListener;
import mineplex.core.server.ServerTalker;
import mineplex.core.spawn.Spawn;
import mineplex.core.status.ServerStatusManager;
import mineplex.core.teleport.Teleport;
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
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.minecraft.game.core.mechanics.PistonJump;
import mineplex.minecraft.game.core.mechanics.Weapon;
import nautilus.game.core.util.NullChunkGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

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
  
  public void onEnable()
  {
    try
    {
      getConfig().addDefault(this.WEB_CONFIG, "http://accounts.mineplex.com/");
      getConfig().set(this.WEB_CONFIG, getConfig().getString(this.WEB_CONFIG));
      saveConfig();
      
      this._spawnLocation = new Location((World)getServer().getWorlds().get(0), -7.5D, 18.5D, 24.5D, 90.0F, 0.0F);
      
      this.ClientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
      CommandCenter.Initialize(this, this.ClientManager);
      
      mineplex.core.fakeEntity.FakeEntityManager.Initialize(this);
      ItemStackFactory.Initialize(this, true);
      Recharge.Initialize(this);
      
      this._moduleManager = new ModuleManager();
      
      Updater updater = new Updater(this);
      this._creature = new Creature(this);
      this.Energy = new Energy(this);
      this.DonationManager = new DonationManager(this, GetWebServerAddress());
      this.ConditionManager = new SkillConditionManager(this);
      this.CombatManager = new CombatManager(this);
      this._blockRestore = new BlockRestore(this);
      this._throw = new ProjectileManager(this);
      this._spawn = new Spawn(this);
      this._teleport = new Teleport(this, this.ClientManager, this._spawn);
      this.NpcManager = new NpcManager(this, this._creature);
      this.PacketHandler = new PacketHandler(this);
      this._damage = new DamageManager(this, this.CombatManager, this.NpcManager, new DisguiseManager(this, this.PacketHandler));
      this._fire = new Fire(this, this.ConditionManager, this._damage);
      new Punish(this, GetWebServerAddress(), this.ClientManager);
      new ServerStatusManager(this, new LagMeter(this, this.ClientManager));
      

      this.SkillManager = new SkillFactory(this, this._damage, this, this.CombatManager, this.ConditionManager, this._throw, this._blockRestore, this._fire, new Movement(this), this._teleport, this.Energy, GetWebServerAddress());
      this.ClassManager = new ClassManager(this, this.ClientManager, this.DonationManager, this.SkillManager, GetWebServerAddress());
      new ItemFactory(this, this._blockRestore, this.ClassManager, this.ConditionManager, this._damage, this.Energy, this._fire, this._throw, GetWebServerAddress(), new HashSet());
      
      new MessageManager(this, this.ClientManager);
      
      new Blood(this);
      new JoinQuit();
      new mineplex.core.server.Server();
      new AntiStack(this);
      new MemoryFix(this);
      new PistonJump(this);
      new Weapon(this, this.Energy);
      new mineplex.core.updater.FileUpdater(this, new Portal(this));
      
      getServer().getScheduler().scheduleSyncRepeatingTask(this, updater, 1L, 1L);
      








      ClassShopManager shopManager = new ClassShopManager(this, this.ClassManager, this.SkillManager, null);
      new ClassCombatShop(shopManager, this.ClientManager, this.DonationManager, "Select Class Here");
      new ClassCombatPurchaseShop(shopManager, this.ClientManager, this.DonationManager, "Skill Shop");
      new ClassCombatCustomBuildShop(shopManager, this.ClientManager, this.DonationManager, "Class Setup");
    }
    catch (Exception exception)
    {
      System.out.println("Exception during startup.  Restarting in 15 seconds.");
      
      getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
      {

        public void run() {}


      }, 300L);
    }
  }
  

  protected abstract String GetServerName();
  
  public void onDisable()
  {
    this._moduleManager.onDisable();
    this._serverListener.Shutdown();
  }
  
  public String GetWebServerAddress()
  {
    return getConfig().getString(this.WEB_CONFIG);
  }
  
  public Location GetSpawnLocation()
  {
    return this._spawnLocation;
  }
  

  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
  {
    return new NullChunkGenerator();
  }
}
