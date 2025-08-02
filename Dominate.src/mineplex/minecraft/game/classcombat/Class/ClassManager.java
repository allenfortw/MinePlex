package mineplex.minecraft.game.classcombat.Class;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.repository.ClassRepository;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassTokenWrapper;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;




public class ClassManager
  extends MiniClientPlugin<ClientClass>
  implements IClassFactory
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
    
    this._plugin = plugin;
    this._clientManager = clientManager;
    this._donationManager = donationManager;
    this._skillFactory = skillFactory;
    this._repository = new ClassRepository(webAddress);
    this._classes = new HashMap();
    this._classSalesPackageIdMap = new HashMap();
    
    PopulateClasses();
  }
  
  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    ClientClassTokenWrapper token = (ClientClassTokenWrapper)new Gson().fromJson(event.GetResponse(), ClientClassTokenWrapper.class);
    LoadClassBuilds(token);
  }
  
  private void LoadClassBuilds(ClientClassTokenWrapper token)
  {
    synchronized (this._clientLock)
    {
      Set(token.Name, new ClientClass(this, this._skillFactory, this._clientManager.Get(token.Name), this._donationManager.Get(token.Name), token.DonorToken));
    }
  }
  
  public ClientClass Get(String name)
  {
    synchronized (this._clientLock)
    {
      return (ClientClass)super.Get(name);
    }
  }
  
  private void PopulateClasses()
  {
    this._classes.clear();
    AddClass(new PvpClass(this, -1, IPvpClass.ClassType.Knight, 
      new String[] { "Trained in the arts of melee combat.", "Able to stand his ground against foes." }, 
      Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, 
      null));
    
    AddClass(new PvpClass(this, -1, IPvpClass.ClassType.Ranger, 
      new String[] { "Mastery with a Bow and Arrow.", "Adept in Wilderness Survival" }, 
      Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, 
      null));
    
    AddClass(new PvpClass(this, 3, IPvpClass.ClassType.Brute, 
      new String[] { "Uses pure strength to dominate.", "Great at crowd control." }, 
      Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, 
      null));
    
    AddClass(new PvpClass(this, 4, IPvpClass.ClassType.Mage, 
      new String[] { "Trained in the ancient arts.", "Able to adapt to many roles in combat." }, 
      Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, 
      null));
    
    AddClass(new PvpClass(this, 5, IPvpClass.ClassType.Assassin, 
      new String[] { "Extremely nimble and smart.", "Excels at ambushing and takedowns.", "", "Permanent Speed II" }, 
      Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, 
      null));
    







    List<ClassToken> classTokens = new ArrayList();
    
    for (IPvpClass pvpClass : this._classes.values())
    {
      ClassToken classToken = new ClassToken();
      classToken.Name = pvpClass.GetName();
      classToken.SalesPackage = new GameSalesPackageToken();
      classToken.SalesPackage.Gems = pvpClass.GetCost();
      
      classTokens.add(classToken);
    }
    
    for (ClassToken classToken : this._repository.GetClasses(classTokens))
    {
      if (this._classes.containsKey(classToken.Name))
      {
        ((IPvpClass)this._classes.get(classToken.Name)).Update(classToken);
        this._classSalesPackageIdMap.put(classToken.SalesPackage.GameSalesPackageId, (IPvpClass)this._classes.get(classToken.Name));
      }
    }
  }
  
  public IPvpClass GetClass(String className)
  {
    return (IPvpClass)this._classes.get(className);
  }
  
  public IPvpClass GetClass(int id)
  {
    return (IPvpClass)this._classSalesPackageIdMap.get(Integer.valueOf(id));
  }
  
  public Collection<IPvpClass> GetAllClasses()
  {
    return this._classes.values();
  }
  
  public void AddClass(PvpClass newClass)
  {
    this._classes.put(newClass.GetName(), newClass);
  }
  

  public Collection<IPvpClass> GetGameClasses()
  {
    return this._classes.values();
  }
  
  @EventHandler
  public void update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (IPvpClass cur : this._classes.values()) {
      cur.checkEquip();
    }
  }
  
  public SkillFactory GetSkillFactory() {
    return this._skillFactory;
  }
  
  public ClassRestore GetRestore()
  {
    if (this._classRestore == null) {
      this._classRestore = new ClassRestore(this._plugin, this, this._skillFactory);
    }
    return this._classRestore;
  }
  

  protected ClientClass AddPlayer(String player)
  {
    return new ClientClass(this, this._skillFactory, this._clientManager.Get(player), this._donationManager.Get(player), null);
  }
  
  public ClassRepository GetRepository()
  {
    return this._repository;
  }
}
