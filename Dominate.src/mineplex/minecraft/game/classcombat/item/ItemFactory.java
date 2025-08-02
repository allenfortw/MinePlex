package mineplex.minecraft.game.classcombat.item;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.energy.Energy;
import mineplex.core.projectile.ProjectileManager;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.item.Consume.Soup;
import mineplex.minecraft.game.classcombat.item.Throwable.WaterBottle;
import mineplex.minecraft.game.classcombat.item.Throwable.Web;
import mineplex.minecraft.game.classcombat.item.Tools.Scanner;
import mineplex.minecraft.game.classcombat.item.repository.ItemRepository;
import mineplex.minecraft.game.classcombat.item.repository.ItemToken;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFactory extends MiniPlugin implements IItemFactory
{
  private BlockRestore _blockRestore;
  private ClassManager _classManager;
  private ConditionManager _condition;
  private DamageManager _damage;
  private Energy _energy;
  private Fire _fire;
  private ProjectileManager _projectileManager;
  private Field _itemMaxDurability;
  private HashMap<String, Item> _items;
  private HashSet<String> _ignore;
  
  public ItemFactory(JavaPlugin plugin, BlockRestore blockRestore, ClassManager classManager, ConditionManager condition, DamageManager damage, Energy energy, Fire fire, ProjectileManager projectileManager, String webAddress, HashSet<String> ignore)
  {
    super("Item Factory", plugin);
    
    this._blockRestore = blockRestore;
    this._classManager = classManager;
    this._condition = condition;
    this._damage = damage;
    this._energy = energy;
    this._fire = fire;
    this._projectileManager = projectileManager;
    
    this._items = new HashMap();
    this._ignore = ignore;
    
    try
    {
      this._itemMaxDurability = net.minecraft.server.v1_6_R3.Item.class.getDeclaredField("durability");
      this._itemMaxDurability.setAccessible(true);
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchFieldException e)
    {
      e.printStackTrace();
    }
    
    PopulateFactory(webAddress);
  }
  
  private void PopulateFactory(String webAddress)
  {
    this._items.clear();
    
    AddConsumables();
    AddPassive();
    AddThrowable();
    AddTools();
    AddOther();
    
    for (Item cur : this._items.values()) {
      RegisterEvents(cur);
    }
    List<ItemToken> itemTokens = new java.util.ArrayList();
    
    for (Item item : this._items.values())
    {
      ItemToken itemToken = new ItemToken();
      itemToken.Name = item.GetName();
      itemToken.Material = item.GetType().toString();
      itemToken.SalesPackage = new GameSalesPackageToken();
      itemToken.SalesPackage.GameSalesPackageId = Integer.valueOf(item.GetSalesPackageId());
      itemToken.SalesPackage.Gems = Integer.valueOf(item.GetGemCost());
      
      itemTokens.add(itemToken);
    }
    
    for (ItemToken itemToken : new ItemRepository(webAddress).GetItems(itemTokens))
    {
      if (this._items.containsKey(itemToken.Name))
      {
        ((Item)this._items.get(itemToken.Name)).Update(itemToken);
      }
    }
  }
  
  private void AddConsumables()
  {
    AddItem(new mineplex.minecraft.game.classcombat.item.Consume.Apple(this, 100, 
      Material.APPLE, 1, false, 0, 
      UtilEvent.ActionType.R, true, 500L, 0, 
      UtilEvent.ActionType.L, true, 500L, 4, 1.2F, 
      -1L, true, true, true, false));
    
    AddItem(new Soup(this, 103, 
      Material.MUSHROOM_SOUP, 1, true, 0, 
      UtilEvent.ActionType.R, true, 500L, 0, 
      null, false, 0L, 0, 0.0F, 
      -1L, true, true, true, false));
  }
  


  private void AddPassive() {}
  

  private void AddThrowable()
  {
    AddItem(new WaterBottle(this, 301, 
      Material.POTION, 1, false, 0, 
      UtilEvent.ActionType.R, true, 500L, 0, 
      UtilEvent.ActionType.L, true, 500L, 4, 1.0F, 
      -1L, true, true, true, false));
    
    AddItem(new Web(this, 30, 
      Material.WEB, 2, false, 500, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 8, 1.0F, 
      -1L, true, true, true, false));
    
    AddItem(new mineplex.minecraft.game.classcombat.item.Throwable.PoisonBall(this, 304, 
      Material.SLIME_BALL, 1, false, 1500, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 0L, 6, 1.2F, 
      -1L, true, true, true, false));
    
    AddItem(new mineplex.minecraft.game.classcombat.item.Throwable.ProximityExplosive(this, 307, 
      Material.COMMAND, 1, false, 1000, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 10, 0.8F, 
      4000L, false, false, false, true));
    
    AddItem(new mineplex.minecraft.game.classcombat.item.Throwable.ProximityZapper(this, 308, 
      Material.REDSTONE_LAMP_ON, 1, false, 1000, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 10, 0.8F, 
      4000L, false, false, false, true));
  }
  
  private void AddTools()
  {
    AddItem(new Scanner(this, 303, 
      Material.SHEARS, 1, true, 1000, 
      UtilEvent.ActionType.R, false, 2000L, 20, 
      null, true, 250L, 6, 1.8F, 
      -1L, true, true, true, false));
  }
  
  private void AddOther()
  {
    AddItem(new Item(this, 401, "Arrows", new String[] { "Standard Arrows" }, Material.ARROW, 8, true, 0));
  }
  

  public IItem GetItem(String weaponName)
  {
    return (IItem)this._items.get(weaponName);
  }
  

  public Collection<Item> GetItems()
  {
    return this._items.values();
  }
  
  public void AddItem(Item newItem)
  {
    if (this._ignore.contains(newItem.GetName()))
    {
      System.out.println("Item Factory: Ignored " + newItem.GetName());
      return;
    }
    
    try
    {
      this._itemMaxDurability.setInt(net.minecraft.server.v1_6_R3.Item.byId[newItem.GetType().getId()], 56);
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    
    this._items.put(newItem.GetName(), newItem);
  }
  
  public BlockRestore BlockRestore()
  {
    return this._blockRestore;
  }
  
  public ClassManager ClassManager()
  {
    return this._classManager;
  }
  
  public ConditionManager Condition()
  {
    return this._condition;
  }
  
  public DamageManager Damage()
  {
    return this._damage;
  }
  
  public Energy Energy()
  {
    return this._energy;
  }
  
  public Fire Fire()
  {
    return this._fire;
  }
  
  public ProjectileManager Throw()
  {
    return this._projectileManager;
  }
}
