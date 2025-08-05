package mineplex.minecraft.game.classcombat.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.energy.Energy;
import mineplex.minecraft.game.core.fire.Fire;
import mineplex.core.projectile.ProjectileManager;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.item.Consume.*;
import mineplex.minecraft.game.classcombat.item.Throwable.*;
import mineplex.minecraft.game.classcombat.item.Tools.Scanner;
import mineplex.minecraft.game.classcombat.item.repository.ItemRepository;
import mineplex.minecraft.game.classcombat.item.repository.ItemToken;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;

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
    private java.lang.reflect.Field _itemMaxDurability;
	private HashMap<String, Item> _items;
	private HashSet<String> _ignore;
	
	public ItemFactory(JavaPlugin plugin, BlockRestore blockRestore, ClassManager classManager, ConditionManager condition, DamageManager damage, Energy energy, Fire fire, ProjectileManager projectileManager, String webAddress, HashSet<String> ignore)
	{
		super("Item Factory", plugin);
		
		_blockRestore = blockRestore;
		_classManager = classManager;
		_condition = condition;
		_damage = damage;
		_energy = energy;
		_fire = fire;
		_projectileManager = projectileManager;
		
		_items = new HashMap<String, Item>();
		_ignore = ignore;
        
		try
        {
            _itemMaxDurability = net.minecraft.server.v1_6_R2.Item.class.getDeclaredField("durability");
            _itemMaxDurability.setAccessible(true);
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
	    _items.clear();
			    
	    AddConsumables();
	    AddPassive();
	    AddThrowable();
	    AddTools();
	    AddOther();
		
		for (Item cur : _items.values())
			RegisterEvents(cur);
		
		List<ItemToken> itemTokens = new ArrayList<ItemToken>();
		
		for (Item item : _items.values())
		{
			ItemToken itemToken = new ItemToken();
			itemToken.Name = item.GetName();
			itemToken.Material = item.GetType().toString();
			itemToken.SalesPackage = new GameSalesPackageToken();
			itemToken.SalesPackage.GameSalesPackageId = item.GetSalesPackageId();
			itemToken.SalesPackage.Gems = item.GetGemCost();
			
			itemTokens.add(itemToken);
		}
		
		for (ItemToken itemToken : new ItemRepository(webAddress).GetItems(itemTokens))
		{
			if (_items.containsKey(itemToken.Name))
			{
				_items.get(itemToken.Name).Update(itemToken);
			}
		}
	}

	private void AddConsumables() 
	{
		AddItem(new Apple(this, 100, 
				Material.APPLE, 1, false, 0, 
				ActionType.R, true, 500, 0, 
				ActionType.L, true, 500, 4, 1.2f, 
				-1, true, true, true, false));
		
		AddItem(new Soup(this, 103, 
				Material.MUSHROOM_SOUP, 1, true, 0, 
				ActionType.R, true, 500, 0, 
				null, false, 0, 0, 0f, 
				-1, true, true, true, false));
	}
	
	private void AddPassive()
	{
		
	}
	
	private void AddThrowable()
	{
		AddItem(new WaterBottle(this, 301, 
				Material.POTION, 1, false, 0, 
				ActionType.R, true, 500, 0, 
				ActionType.L, true, 500, 4, 1f, 
				-1, true, true, true, false));
		
		AddItem(new Web(this, 30, 
				Material.WEB, 2, false, 500,
				null, true, 0, 0, 
				ActionType.L, true, 250, 8, 1f, 
				-1, true, true, true, false));
		
		AddItem(new PoisonBall(this, 304, 
				Material.SLIME_BALL, 1, false, 1500, 
				null, true, 0, 0, 
				ActionType.L, true, 0, 6, 1.2f, 
				-1, true, true, true, false));
		
		AddItem(new ProximityExplosive(this, 307, 
				Material.COMMAND, 1, false, 1000,
				null, true, 0, 0, 
				ActionType.L, true, 250, 10, 0.8f, 
				4000, false, false, false, true));
		
		AddItem(new ProximityZapper(this, 308, 
				Material.REDSTONE_LAMP_ON, 1, false, 1000, 
				null, true, 0, 0, 
				ActionType.L, true, 250, 10, 0.8f, 
				4000, false, false, false, true));
	}
	
	private void AddTools()
	{
		AddItem(new Scanner(this, 303, 
				Material.SHEARS, 1, true, 1000,
				ActionType.R, false, 2000, 20, 
				null, true, 250, 6, 1.8f, 
				-1, true, true, true, false));
	}
	
	private void AddOther()
	{
		AddItem(new Item(this, 401, "Arrows", new String[] { "Standard Arrows" }, Material.ARROW, 8, true, 0));
		//AddItem(new Item(this, 402, "Pistol Ammo", new String[] { "Pistol Ammo" }, Material.MELON_SEEDS, 5, true, 5, 0));
	}

	public IItem GetItem(String weaponName)
	{
		return _items.get(weaponName);
	}

	@Override
	public Collection<Item> GetItems()
	{
		return _items.values();
	}

	public void AddItem(Item newItem)
	{
		if (_ignore.contains(newItem.GetName()))
		{
			System.out.println("Item Factory: Ignored " + newItem.GetName());
			return;
		}
		
	    try
        {
            _itemMaxDurability.setInt(net.minecraft.server.v1_6_R2.Item.byId[newItem.GetType().getId()], 56);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
	    
	    _items.put(newItem.GetName(), newItem);
	}

	public BlockRestore BlockRestore()
	{
		return _blockRestore;
	}
	
	public ClassManager ClassManager()
	{
		return _classManager;
	}
	
	public ConditionManager Condition()
	{
		return _condition;
	}
	
	public DamageManager Damage()
	{
		return _damage;
	}

	public Energy Energy()
	{
		return _energy;
	}
	
	public Fire Fire()
	{
		return _fire;
	}
	
	public ProjectileManager Throw()
	{
		return _projectileManager;
	}
}
