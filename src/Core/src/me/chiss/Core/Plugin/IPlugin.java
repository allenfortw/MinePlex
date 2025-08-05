package me.chiss.Core.Plugin;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Config.Config;
import me.chiss.Core.Loot.LootFactory;
import me.chiss.Core.Module.ModuleManager;
import me.chiss.Core.Modules.*;
import me.chiss.Core.Server.Server;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.creature.Creature;
import mineplex.core.energy.Energy;
import mineplex.core.packethandler.INameColorer;
import mineplex.core.pet.PetManager;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.spawn.Spawn;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.core.fire.Fire;

public interface IPlugin 
{
	public void Log(String moduleName, String data);
	
	public JavaPlugin GetPlugin();
	
	public ModuleManager GetModules();
	public Config GetConfig();
	public Utility GetUtility();

	public Blood GetBlood();
	public BlockRegenerate GetBlockRegenerate();
	public BlockRestore GetBlockRestore();
	public Creature GetCreature();
	public Energy GetEnergy();
	public Fire GetFire();
	public me.chiss.Core.Modules.Logger GetLogger();
	public LootFactory GetLoot();
	public Observer GetObserver();
	public Server GetServer();
	public Spawn GetSpawn();
	public Teleport GetTeleport();
	public ProjectileManager GetThrow();
	public Location GetSpawnLocation();

	public String GetWebServerAddress();

	public PetManager GetPetManager();
}
