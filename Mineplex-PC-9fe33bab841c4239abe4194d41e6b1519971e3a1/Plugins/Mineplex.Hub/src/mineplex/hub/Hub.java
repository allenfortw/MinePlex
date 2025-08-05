package mineplex.hub;

import me.chiss.Core.MemoryFix.MemoryFix;
import mineplex.core.account.CoreClientManager;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.chat.Chat;
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
import mineplex.core.pet.PetManager;
import mineplex.core.portal.Portal;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.punish.Punish;
import mineplex.core.recharge.Recharge;
import mineplex.core.spawn.Spawn;
import mineplex.core.task.TaskManager;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.FileUpdater;
import mineplex.core.updater.Updater;
import mineplex.hub.server.ServerManager;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Condition.SkillConditionManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.shop.ClassCombatCustomBuildShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatPurchaseShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import nautilus.minecraft.core.INautilusPlugin;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Hub extends JavaPlugin implements INautilusPlugin, IRelation
{
	private String WEB_CONFIG = "webServer";

	@Override
	public void onEnable()
	{
		getConfig().addDefault(WEB_CONFIG, "http://api.mineplex.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();

		//Core Modules
		CoreClientManager clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
		DonationManager donationManager = new DonationManager(this, GetWebServerAddress());
		
		//Static Modules
		CommandCenter.Initialize(this, clientManager);
		ItemStackFactory.Initialize(this, false);
		Recharge.Initialize(this);
		
		//Other Modules
		new Punish(this, GetWebServerAddress());
		Creature creature = new Creature(this);
		new MessageManager(this, clientManager);
		NpcManager npcManager = new NpcManager(this, creature);
		new PetManager(this, clientManager, donationManager, creature, GetWebServerAddress());
		
		//Main Modules
		PacketHandler packetHandler = new PacketHandler(this);
		Portal portal = new Portal(this);
		new HubManager(this, clientManager, donationManager, new DisguiseManager(this, packetHandler), new TaskManager(this, GetWebServerAddress()), portal);
		new Stacker(this);
		new ServerManager(this, clientManager, donationManager, portal);
		new Chat(this, clientManager);
		new MemoryFix(this);
		new FileUpdater(this, portal);
		new LagMeter(this, clientManager);
		
		CombatManager combatManager = new CombatManager(this);
		BlockRestore blockRestore = new BlockRestore(this);
		ProjectileManager throwManager = new ProjectileManager(this);
		SkillConditionManager conditionManager = new SkillConditionManager(this);
		
		DamageManager damage = new DamageManager(this, combatManager, npcManager, new DisguiseManager(this, packetHandler));
		Fire fire = new Fire(this, conditionManager, damage);
		Teleport teleport = new Teleport(this, clientManager, new Spawn(this));
		
		SkillFactory skillManager = new SkillFactory(this, damage, this, combatManager, conditionManager, throwManager, blockRestore, fire, new Movement(this), teleport, new Energy(this), GetWebServerAddress());
		ClassManager classManager = new ClassManager(this, clientManager, donationManager, skillManager, GetWebServerAddress());
   
        ClassShopManager shopManager = new ClassShopManager(this, classManager, skillManager, null);
        new ClassCombatShop(shopManager, clientManager, donationManager, "Select Class Here");
        new ClassCombatPurchaseShop(shopManager, clientManager, donationManager, "Skill Shop");
        new ClassCombatCustomBuildShop(shopManager, clientManager, donationManager, "Class Setup");
		
		//Updates
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1, 1);
	}

	@Override
	public void onDisable()
	{

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
	public boolean CanHurt(Player a, Player b)
	{
		return false;
	}

	@Override
	public boolean CanHurt(String a, String b)
	{
		return false;
	}

	@Override
	public boolean IsSafe(Player a)
	{
		return true;
	}
}
