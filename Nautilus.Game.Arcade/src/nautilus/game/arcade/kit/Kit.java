package nautilus.game.arcade.kit;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Kit implements Listener
{
	public ArcadeManager Manager;

	private String _kitName;
	private String[] _kitDesc;
	private KitAvailability _kitAvailability;
	
	private Perk[] _kitPerks;
	
	protected EntityType _entityType;
	protected ItemStack _itemInHand;
	
	protected Material _displayItem;
	
	public Kit(ArcadeManager manager, String name, KitAvailability kitAvailability, String[] kitDesc, Perk[] kitPerks, EntityType entityType, ItemStack itemInHand)
	{
		Manager = manager;

		_kitName = name;
		_kitDesc = kitDesc;
		_kitPerks = kitPerks;
		
		for (Perk perk : _kitPerks)
			perk.SetHost(this);
		
		_kitAvailability = kitAvailability;
		
		_entityType = entityType;
		_itemInHand = itemInHand;
		
		_displayItem = Material.BOOK;
		if (itemInHand != null)
			_displayItem = itemInHand.getType();
	}

	public String GetFormattedName()
	{
		return GetAvailability().GetColor() + "§l" + _kitName;
	}
	
	public String GetName()
	{	
		return _kitName;
	}

	public ItemStack GetItemInHand()
	{
		return _itemInHand;
	}
	
	public KitAvailability GetAvailability()
	{
		return _kitAvailability;
	}
	
	public String[] GetDesc()
	{
		return _kitDesc;
	}
	
	public Perk[] GetPerks()
	{
		return _kitPerks;
	}

	public boolean HasKit(Player player)
	{
		if (Manager.GetGame() == null)
			return false;

		return Manager.GetGame().HasKit(player, this);
	}	

	public void ApplyKit(Player player)
	{
		UtilInv.Clear(player);
		
		for (Perk perk : _kitPerks)
			perk.Apply(player);
		
		GiveItems(player);
	}
	
	public abstract void GiveItems(Player player);
	
	public Entity SpawnEntity(Location loc)
	{
		EntityType type = _entityType;
		if (type == EntityType.PLAYER)
			type = EntityType.ZOMBIE;
		
		LivingEntity entity = (LivingEntity) Manager.GetCreature().SpawnEntity(loc, type);

		entity.setRemoveWhenFarAway(false);
		entity.setCustomName(GetAvailability().GetColor() + GetName() + " Kit" + (GetAvailability() == KitAvailability.Blue ? ChatColor.GRAY + " (" + ChatColor.WHITE + "Ultra" + ChatColor.GRAY + ")" : ""));
		entity.setCustomNameVisible(true);
		entity.getEquipment().setItemInHand(_itemInHand);
		
		if (type == EntityType.SKELETON && GetName().contains("Wither"))
		{
			Skeleton skel = (Skeleton)entity;
			skel.setSkeletonType(SkeletonType.WITHER);
		}

		UtilEnt.Vegetate(entity);

		SpawnCustom(entity); 

		return entity;
	}

	public void SpawnCustom(LivingEntity ent) { }

	public void DisplayDesc(Player player) 
	{
		for (int i=0 ; i<3 ; i++)
			UtilPlayer.message(player, "");
		
		UtilPlayer.message(player, ArcadeFormat.Line);

		UtilPlayer.message(player, "§aKit - §f§l" + GetName());
		
		//Desc
		for (String line : GetDesc())
		{
			UtilPlayer.message(player, C.cGray + "  " + line);
		}

		//Perk Descs
		for (Perk perk : GetPerks())
		{
			if (!perk.IsVisible())
				continue;
			
			UtilPlayer.message(player, "");
			UtilPlayer.message(player, C.cWhite + C.Bold + perk.GetName());
			for (String line : perk.GetDesc())
			{
				UtilPlayer.message(player, C.cGray + "  " + line);
			}
		}
		
		UtilPlayer.message(player, ArcadeFormat.Line);

	}

	public int GetCost() 
	{
		return 2000;
	}

	public Material getDisplayMaterial()
	{
		return _displayItem;
	}
}
