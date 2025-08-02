package me.chiss.Core.Module;

import java.util.HashSet;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Clans.Clans;
import me.chiss.Core.Config.Config;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.Field.Field;
import me.chiss.Core.Modules.*;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Plugin.IRelation;
import me.chiss.Core.Server.Server;
import mineplex.core.account.CoreClientManager;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.creature.Creature;
import mineplex.core.energy.Energy;
import mineplex.core.explosion.Explosion;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.IRepository;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.minecraft.game.core.mechanics.Weapon;

public abstract class AModule implements Listener
{
	private IRepository _repository;
	
	protected String _moduleName = "Default";

	protected CoreClientManager _clients;
	protected JavaPlugin _plugin;
	
	protected Config _config;
	
	protected HashSet<String> _commands;
	
	public AModule(String moduleName, JavaPlugin plugin)
	{
	    this(moduleName, plugin, null);
	}
	
	public AModule(String moduleName, JavaPlugin plugin, IRepository repository)
	{
        _moduleName = moduleName;
        _plugin = plugin;
        _repository = repository;
        
        _commands = new HashSet<String>();
        
        //Register Self
        Modules().Register(this);
        
        //Register Events
        plugin.getServer().getPluginManager().registerEvents(this, _plugin);
        
        //Enable
        onEnable();
        
        commands();	    
	}

	public IRepository GetRepository()
	{
		return _repository;
	}
	
	public final AModule onEnable()
	{
		//long epoch = System.currentTimeMillis();
		Log("Initialising...");
		//config(); XXX
		enable();
		// Log("Enabled in " + UtilTime.convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT) + ".");
		
		return this;
	}

	public final void onDisable()
	{
		disable();
		
		Log("Disabled.");
	}

	public void enable() { }
	public void disable() { }
	public void config() { }
	public void commands() { }
	public void command(Player caller, String cmd, String[] args) { }

	public final String GetName()
	{
		return _moduleName;
	}
	
	public final void AddCommand(String command)
	{
		_commands.add(command.toLowerCase());
	}
	
	public final boolean HasCommand(String command)
	{
		return _commands.contains(command.toLowerCase());
	}
	
	public void Log(String data)
	{
		if (!(_plugin instanceof IPlugin))
			return;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		plugin.Log(_moduleName, data);
	}
	
	//Common Modules
	public JavaPlugin Plugin()
	{
		return _plugin;
	}
	
	public ModuleManager Modules()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetModules();
	}
	
	public Config Config()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetConfig();
	}
	
	public Utility Util()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetUtility();
	}
	
	public BlockRegenerate BlockRegenerate()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetBlockRegenerate();
	}
	
	public BlockRestore BlockRestore()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetBlockRestore();
	}
	
	public Blood Blood()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetBlood();
	}
	
	public Clans Clans()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetClans();
	}
	
	public ClassFactory Classes()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetClasses();
	}
		
	public ConditionManager Condition()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetCondition();
	}
	
	public Creature Creature()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetCreature();
	}
	
	public DamageManager Damage()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetDamage();
	}

	public Energy Energy()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetEnergy();
	}
	
	public Explosion Explosion()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetExplosion();
	}
	
	public Field Field()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetField();
	}
	
	public Fire Fire()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetFire();
	}
	
	public Ignore Ignore()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetIgnore();
	}

	public Logger Logger()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetLogger();
	}
	
	public LootFactory Loot()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetLoot();
	}
	
	public Observer Observer()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetObserver();
	}
	
	public IRelation Relation()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetRelation();
	}
	
	public Recharge Recharge()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetRecharge();
	}
	
	public Server ServerM()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetServer();
	}
	
	public SkillFactory Skills()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetSkills();
	}
	
	public Spawn Spawn()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetSpawn();
	}
	
	public Teleport Teleport()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetTeleport();
	}
	
	public ProjectileManager Throw()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetThrow();
	}
	
	public Weapon Weapon()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetWeapon();
	}
	
	public SkillFactory SkillFactory()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetSkills();
	}
	
	public Wiki Wiki()
	{
		if (!(_plugin instanceof IPlugin))
			return null;
		
		IPlugin plugin = (IPlugin)_plugin;
		
		return plugin.GetWiki();
	}
}
