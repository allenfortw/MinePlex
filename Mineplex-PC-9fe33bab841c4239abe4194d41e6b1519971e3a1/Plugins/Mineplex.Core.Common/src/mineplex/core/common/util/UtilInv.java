package mineplex.core.common.util;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class UtilInv
{
	@SuppressWarnings("deprecation")
	public static boolean insert(Player player, ItemStack stack)
	{
		//CHECK IF FIT
		
		//Insert
		player.getInventory().addItem(stack);
		player.updateInventory();
		return true;
	}
	
	public static boolean contains(Player player, Material item, byte data, int required)
	{
		for (int i : player.getInventory().all(item).keySet()) 
		{
			if (required <= 0)
				return true;

			ItemStack stack = player.getInventory().getItem(i);
			
			if (stack != null && stack.getAmount() > 0 && (stack.getData() == null || stack.getData().getData() == data))
			{
				required -= stack.getAmount();
			}
		}
		
		if (required <= 0)
		{
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean remove(Player player, Material item, byte data, int toRemove) 
	{
		if (!contains(player, item, data, toRemove))
			return false;
		
		for (int i : player.getInventory().all(item).keySet()) 
		{
			if (toRemove <= 0)
				continue;
			
			ItemStack stack = player.getInventory().getItem(i);

			if (stack.getData() == null || stack.getData().getData() == data)
			{
				int foundAmount = stack.getAmount();

				if (toRemove >= foundAmount) 
				{
					toRemove -= foundAmount;
					player.getInventory().setItem(i, null);
				} 

				else 
				{
					stack.setAmount(foundAmount - toRemove);
					player.getInventory().setItem(i, stack);
					toRemove = 0;
				}
			} 
		}
		
		player.updateInventory();
		return true;
	}

	public static void Clear(Player player)
	{
		PlayerInventory inv = player.getInventory();
		
		inv.clear();
		inv.clear(inv.getSize() + 0);
		inv.clear(inv.getSize() + 1);
		inv.clear(inv.getSize() + 2);
		inv.clear(inv.getSize() + 3);
		
		player.saveData();
	}
	
	public static void drop(Player player, boolean clear)
	{
		for (ItemStack cur : player.getInventory().getContents())
		{
			if (cur == null)
				continue;
			
			if (cur.getType() == Material.AIR)
				continue;
			
			player.getWorld().dropItemNaturally(player.getLocation(), cur);
		}
		
		for (ItemStack cur : player.getInventory().getArmorContents())
		{
			if (cur == null)
				continue;
			
			if (cur.getType() == Material.AIR)
				continue;
			
			player.getWorld().dropItemNaturally(player.getLocation(), cur);
		}
		
		if (clear)
			Clear(player);
	}

	@SuppressWarnings("deprecation")
	public static void Update(Entity player) 
	{
		if (!(player instanceof Player))
			return;
		
		((Player)player).updateInventory();
	}

	public static int removeAll(Player player, Material type, byte data) 
	{
		HashSet<ItemStack> remove = new HashSet<ItemStack>();
		int count = 0;
		
		for (ItemStack item : player.getInventory().getContents())
			if (item != null)
				if (item.getType() == type)
					if (data == -1 || item.getData() == null || (item.getData() != null && item.getData().getData() == data))
					{
						count += item.getAmount();
						remove.add(item);
					}
	
		for (ItemStack item : remove)
			player.getInventory().remove(item);	

		return count;
	}
	
	public static byte GetData(ItemStack stack)
	{
		if (stack == null)
			return (byte)0;
		
		if (stack.getData() == null)
			return (byte)0;
		
		return stack.getData().getData();
	}

	public static boolean IsItem(ItemStack item, Material type, byte data)
	{
		return IsItem(item, type.getId(), data);
	}
	
	public static boolean IsItem(ItemStack item, int id, byte data)
	{
		if (item == null)
			return false;
		
		if (item.getTypeId() != id)
			return false;
		
		if (data != -1 && GetData(item) != data)
			return false;
		
		return true;
	}
	
	public static void DisallowMovementOf(InventoryClickEvent event, String name, Material type, byte data, boolean inform) 
	{
		/*
		System.out.println("Inv Type: " + event.getInventory().getType());
		System.out.println("Click: " + event.getClick());
		System.out.println("Action: " + event.getAction());
		 
		System.out.println("Slot: " + event.getSlot());
		System.out.println("Slot Raw: " + event.getRawSlot());
		System.out.println("Slot Type: " + event.getSlotType());
		
		System.out.println("Cursor: " + event.getCursor());
		System.out.println("Current: " + event.getCurrentItem());
		
		System.out.println("View Type: " + event.getView().getType());
		System.out.println("View Top Type: " + event.getView().getTopInventory().getType());
		System.out.println("HotBar Button: " + event.getHotbarButton());
		*/
		
		//Do what you want in Crafting Inv
		if (event.getInventory().getType() == InventoryType.CRAFTING)
			return;
		
		//Hotbar Swap
		if (event.getAction() == InventoryAction.HOTBAR_SWAP ||
			event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
		{
			boolean match = false;
			
			if (IsItem(event.getCurrentItem(), type, data))
				match = true;

			if (IsItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()), type, data))
				match = true;

			if (!match) 
				return; 
			
			//Inform
			UtilPlayer.message(event.getWhoClicked(), F.main("Inventory", "You cannot hotbar swap " + F.item(name) + "."));
			event.setCancelled(true);
		}
		//Other
		else
		{
			if (event.getCurrentItem() == null)
				return;

			IsItem(event.getCurrentItem(), type, data);
			
			//Type
			if (!IsItem(event.getCurrentItem(), type, data))
				return;
			//Inform
			UtilPlayer.message(event.getWhoClicked(), F.main("Inventory", "You cannot move " + F.item(name) + "."));
			event.setCancelled(true);
		}
	}
	
	
}
