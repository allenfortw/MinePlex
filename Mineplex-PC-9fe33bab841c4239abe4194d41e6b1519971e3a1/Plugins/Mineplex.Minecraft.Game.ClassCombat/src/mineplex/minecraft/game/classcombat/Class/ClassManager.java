package mineplex.minecraft.game.classcombat.Class;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Class.repository.ClassRepository;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassTokenWrapper;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassManager extends MiniClientPlugin<ClientClass> implements IClassFactory
{
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private SkillFactory _skillFactory;
	private ClassRepository _repository;
	private HashMap<String, IPvpClass> _classes;
	private HashMap<Integer, IPvpClass> _classSalesPackageIdMap;
	
	private ClassRestore _classRestore;
	
	private Object _clientLock = new Object();
	
	public ClassManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, SkillFactory skillFactory, String webAddress) 
	{
		super("Class Manager", plugin);
		
		_plugin = plugin;
		_clientManager = clientManager;
		_donationManager = donationManager;
		_skillFactory = skillFactory;
		_repository = new ClassRepository(webAddress);		
        _classes = new HashMap<String, IPvpClass>();
        _classSalesPackageIdMap = new HashMap<Integer, IPvpClass>();
        
        PopulateClasses();
	}
	
	@EventHandler
	public void OnClientWebResponse(ClientWebResponseEvent event)
	{
		ClientClassTokenWrapper token = new Gson().fromJson(event.GetResponse(), ClientClassTokenWrapper.class);
		LoadClassBuilds(token);
	}

	private void LoadClassBuilds(ClientClassTokenWrapper token)
	{
		synchronized (_clientLock)
		{
			Set(token.Name, new ClientClass(this, _skillFactory, _clientManager.Get(token.Name), _donationManager.Get(token.Name), token.DonorToken));
		}
	}

	public ClientClass Get(String name)
	{
		synchronized (_clientLock)
		{
			return super.Get(name);
		}
	}
	
	private void PopulateClasses()
	{
		_classes.clear();
		AddClass(new PvpClass(this, -1, ClassType.Knight, 
				new String[] { "Trained in the arts of melee combat.", "Able to stand his ground against foes."}, 
				Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, 
				null));
		
		AddClass(new PvpClass(this, -1, ClassType.Ranger, 
				new String[] { "Mastery with a Bow and Arrow.", "Adept in Wilderness Survival" }, 
				Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, 
				null));
		
		AddClass(new PvpClass(this, 3, ClassType.Brute, 
				new String[] { "Uses pure strength to dominate.", "Great at crowd control."}, 
				Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, 
				null));
		
		AddClass(new PvpClass(this, 4, ClassType.Mage, 
				new String[] { "Trained in the ancient arts.", "Able to adapt to many roles in combat."}, 
				Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, 
				null));
		
		AddClass(new PvpClass(this, 5, ClassType.Assassin, 
				new String[] { "Extremely nimble and smart.", "Excels at ambushing and takedowns.", "", "Permanent Speed II" }, 
				Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, 
				null));
		
		/*
		AddClass(new PvpClass(this, 6, ClassType.Shapeshifter, 
				new String[] { "Able to transform into various creatures." }, 
				Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, 
				Color.fromRGB(20, 100, 0)));
				*/
		
		List<ClassToken> classTokens = new ArrayList<ClassToken>();
		
		for (IPvpClass pvpClass : _classes.values())
		{
			ClassToken classToken = new ClassToken();
			classToken.Name = pvpClass.GetName();
			classToken.SalesPackage = new GameSalesPackageToken();
			classToken.SalesPackage.Gems = pvpClass.GetCost();
			
			classTokens.add(classToken);
		}
		
		for (ClassToken classToken : _repository.GetClasses(classTokens))
		{
			if (_classes.containsKey(classToken.Name))
			{
				_classes.get(classToken.Name).Update(classToken);
				_classSalesPackageIdMap.put(classToken.SalesPackage.GameSalesPackageId, _classes.get(classToken.Name));
			}
		}
	}

	public IPvpClass GetClass(String className)
	{
		return _classes.get(className);
	}
	
	public IPvpClass GetClass(int id)
	{
		return _classSalesPackageIdMap.get(id);
	}

	public Collection<IPvpClass> GetAllClasses()
	{
		return _classes.values();
	}

	public void AddClass(PvpClass newClass)
	{
		_classes.put(newClass.GetName(), newClass);
	}

	@Override
	public Collection<IPvpClass> GetGameClasses() 
	{
		return _classes.values();
	}
	
	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return; 
		
		for (IPvpClass cur : _classes.values())
			cur.checkEquip();
	}

	public SkillFactory GetSkillFactory() 
	{
		return _skillFactory;
	}
	
	public ClassRestore GetRestore()
	{
		if (_classRestore == null)
			_classRestore = new ClassRestore(_plugin, this, _skillFactory);
		
		return _classRestore;
	}
	
	@Override
	protected ClientClass AddPlayer(String player)
	{
		return new ClientClass(this, _skillFactory, _clientManager.Get(player), _donationManager.Get(player), null);
	}

	public ClassRepository GetRepository()
	{
		return _repository;
	}
}
