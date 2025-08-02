package me.chiss.Core.Shop.salespackage;

import net.minecraft.server.v1_6_R2.NBTTagList;
import net.minecraft.server.v1_6_R2.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PvpShopItem extends ShopItem
{
	public PvpShopItem(Material type, byte data, String name,
			String deliveryName, String[] lore, int deliveryAmount,
			boolean locked, boolean displayItem) 
	{
		super(type, data, name, deliveryName, lore, deliveryAmount, locked, displayItem);
	}
	
	@Override
	protected void UpdateVisual(boolean clone)
	{
		if (!clone)
		{
			this.getHandle().c(ChatColor.YELLOW + _name);
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
}
