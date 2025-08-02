package mineplex.core.shop.item;

import net.minecraft.server.v1_6_R2.NBTTagList;
import net.minecraft.server.v1_6_R2.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ShopItem extends CraftItemStack
{
	protected String _name;
	private String _deliveryName;
	protected String[] _lore;
	private int _deliveryAmount;
	private boolean _locked;
	private boolean _displayItem;

	public ShopItem(ItemStack itemStack, String name, String deliveryName, int deliveryAmount, boolean locked, boolean displayItem)
	{
		super(itemStack);

		_name = name;
		_deliveryName = deliveryName;
		_displayItem = displayItem;
		_deliveryAmount = deliveryAmount;

		getHandle().tag = ((CraftItemStack)itemStack).getHandle().tag;

		UpdateVisual(true);
		getHandle().tag.set("AttributeModifiers", new NBTTagList());
	}

	public ShopItem(Material type, String name, int deliveryAmount, boolean locked)
	{
		this(type, name, null, deliveryAmount, locked);
	}

	public ShopItem(Material type, String name, String[] lore, int deliveryAmount, boolean locked)
	{
		this(type, name, lore, deliveryAmount, locked, false);
	}

	public ShopItem(Material type, String name, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
	{
		this(type, (byte)0, name, null, lore, deliveryAmount, locked, displayItem);
	}

	public ShopItem(Material type, byte data, String name, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
	{
		this(type, data, name, null, lore, deliveryAmount, locked, displayItem);
	}

	public ShopItem(Material type, byte data, String name, String deliveryName, String[] lore, int deliveryAmount, boolean locked, boolean displayItem)
	{
		super(type.getId(), Math.max(deliveryAmount, 1), data, null);

		_name = name;
		_deliveryName = deliveryName;
		_lore = lore;
		_displayItem = displayItem;
		_deliveryAmount = deliveryAmount;
		_locked = locked;

		UpdateVisual(false);

		// Fix for temp save junk(fixes problem when disconnecting and trying to return)
		if (getHandle().tag != null)
			getHandle().tag.setName("tag");

		getHandle().tag.setByte("Count", (byte)Math.max(deliveryAmount, 1)); 
		getHandle().tag.set("AttributeModifiers", new NBTTagList());
	}

	public boolean IsLocked()
	{
		return _locked;
	}

	public void SetDeliverySettings()
	{
		setAmount(_deliveryAmount);

		//Delivery Name
		if (_deliveryName != null)
			this.getHandle().c(_deliveryName);
	}

	public ShopItem clone()
	{
		return new ShopItem(super.clone(), _name, _deliveryName, _deliveryAmount, _locked, _displayItem);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!super.equals(obj)) 
		{
			return false;
		}

		net.minecraft.server.v1_6_R2.ItemStack original = ((CraftItemStack)this).getHandle();
		net.minecraft.server.v1_6_R2.ItemStack comparison = ((CraftItemStack)obj).getHandle();

		return original.tag == null || original.tag.equals(comparison.tag);
	}

	protected void UpdateVisual(boolean clone)
	{
		if (!clone)
		{
			if (_locked && !_displayItem)
			{
				this.getHandle().c(ChatColor.RED + "§l" + _name);
			}
			else
			{
				this.getHandle().c(ChatColor.GREEN + "§l" + _name);
			}
		}

		NBTTagList lore = new NBTTagList("Lore");

		if (_lore != null)
		{
			for (String line : _lore)
			{
				if (line != null && !line.isEmpty())
					lore.add(new NBTTagString("Test", line));
			}
		}

		getHandle().tag.getCompound("display").set("Lore", lore);
	}

	public boolean IsDisplay()
	{
		return _displayItem;
	}

	public void SetLocked(boolean owns)
	{
		_locked = owns;
		UpdateVisual(false);
	}

	public String GetName()
	{
		return _name;
	}
	
	public void SetName(String name)
	{
		_name = name;
	}

	public void SetLore(String[] string)
	{
		_lore = string; 

		NBTTagList lore = new NBTTagList("Lore");

		if (_lore != null)
		{
			for (String line : _lore)
			{
				if (line != null && !line.isEmpty())
					lore.add(new NBTTagString("Test", line));
			}
		}

		getHandle().tag.getCompound("display").set("Lore", lore);
	} 
}
