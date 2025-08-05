package me.chiss.Core.Loot;

import java.util.HashSet;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Loot.LootFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.itemstack.ItemStackFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class LootBase implements Listener
{
	protected LootFactory Factory;

	private String _name;
	private String[] _lore;
	private Material _mat;
	private ActionType _trigger;
	private int _enchLevel;

	private HashSet<Player> _users;

	public LootBase(LootFactory factory, String name, String[] lore, Material mat, ActionType trigger, int level)
	{
		Factory = factory;

		_name = name;
		_lore = lore;
		_mat = mat;
		_trigger = trigger;
		
		_enchLevel = level;

		_users = new HashSet<Player>();
	}

	@EventHandler
	public void EquipHeld(PlayerItemHeldEvent event)
	{
		ItemStack newSlot = event.getPlayer().getInventory().getItem(event.getNewSlot());
		
		if (!_users.contains(event.getPlayer()))
			Equip(event.getPlayer(), newSlot);
	}

	@EventHandler
	public void EquipPickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		Equip(event.getPlayer(), event.getPlayer().getItemInHand());
	}

	@EventHandler
	public void EquipInventory(InventoryCloseEvent event)
	{
		if (event.getPlayer() instanceof Player)
			Equip((Player) event.getPlayer(), event.getPlayer().getItemInHand());
	}

	@EventHandler
	public void EquipJoin(PlayerJoinEvent event)
	{
		Equip(event.getPlayer(), event.getPlayer().getItemInHand());
	}

	public void Equip(Player ent, ItemStack item)
	{
		if (_users.contains(ent))
			return;

		if (!UtilGear.isMat(item, _mat))
			return;

		if (!CraftItemStack.asNMSCopy(item).getName().contains(_name)) 
			return;

		_users.add(ent);
	}

	@EventHandler
	public void UpdateUnequip(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		HashSet<Entity> remove = new HashSet<Entity>();

		for (Entity cur : _users)
		{
			if (!(cur instanceof Player))
				continue;

			if (Unequip((Player)cur))
				remove.add(cur);
		}

		for (Entity cur : remove)
			_users.remove(cur);	
	}
	
	public boolean Unequip(Player player)
	{
		if (!UtilGear.isMat(player.getItemInHand(), _mat))
			return true;

		if (!CraftItemStack.asNMSCopy(player.getItemInHand()).getName().contains(_name))
			return true;
		
		return false;
	}

	@EventHandler
	public void DamageTrigger(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!_users.contains(damager))
			return;

		Damage(event);
	}

	public abstract void Damage(CustomDamageEvent event);

	@EventHandler
	public void AbilityTrigger(PlayerInteractEvent event)
	{
		if (!UtilEvent.isAction(event, _trigger))
			return;
		
		if (UtilBlock.usable(event.getClickedBlock()))
			return;
		
		if (Unequip(event.getPlayer()))
			return;

		if (!_users.contains(event.getPlayer()))
			return;	

		Ability(event);
	}

	public abstract void Ability(PlayerInteractEvent event);

	public String GetName()
	{
		return _name;
	}

	public Material GetMaterial()
	{
		return _mat;
	}

	public HashSet<Player> GetUsers()
	{
		return _users;
	}

	public void GiveTo(Player caller) 
	{
		caller.getInventory().addItem(Get());
	}
	
	public ItemStack Get()
	{
		ItemStack loot = ItemStackFactory.Instance.CreateStack(_mat.getId(), (byte)0, 1, "§r" + ChatColor.RESET + C.mLoot +_name, _lore);

		loot.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, _enchLevel);
		
		return loot;
	}

	public void Reset(Player player) 
	{
		_users.remove(player);
		ResetCustom(player);
	}

	public abstract void ResetCustom(Player player);
}
