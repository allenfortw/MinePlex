package org.bukkit.craftbukkit.v1_6_R2.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import net.minecraft.server.v1_6_R2.EntityHuman;
import net.minecraft.server.v1_6_R2.IInventory;
import net.minecraft.server.v1_6_R2.ItemStack;

public class MinecraftInventory implements IInventory {
    private final ItemStack[] items;
    private int maxStack = MAX_STACK;
    private final List<HumanEntity> viewers;
    private String title;
    private InventoryType type;
    private final InventoryHolder owner;

    public MinecraftInventory(InventoryHolder owner, InventoryType type) {
        this(owner, type.getDefaultSize(), type.getDefaultTitle());
        this.type = type;
    }

    public MinecraftInventory(InventoryHolder owner, int size) {
        this(owner, size, "Chest");
    }

    public MinecraftInventory(InventoryHolder owner, int size, String title) {
        this.items = new ItemStack[size];
        this.title = title;
        this.viewers = new ArrayList<HumanEntity>();
        this.owner = owner;
        this.type = InventoryType.CHEST;
    }

    public int getSize() {
        return items.length;
    }

    public ItemStack getItem(int i) {
        return items[i];
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack stack = this.getItem(i);
        ItemStack result;
        if (stack == null) return null;
        if (stack.count <= j) {
            this.setItem(i, null);
            result = stack;
        } else {
            result = CraftItemStack.copyNMSStack(stack, j);
            stack.count -= j;
        }
        this.update();
        return result;
    }

    public ItemStack splitWithoutUpdate(int i) {
        ItemStack stack = this.getItem(i);
        ItemStack result;
        if (stack == null) return null;
        if (stack.count <= 1) {
            this.setItem(i, null);
            result = stack;
        } else {
            result = CraftItemStack.copyNMSStack(stack, 1);
            stack.count -= 1;
        }
        return result;
    }

    public void setItem(int i, ItemStack itemstack) {
        items[i] = itemstack;
        if (itemstack != null && this.getMaxStackSize() > 0 && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }
    }

    public void setName(String name) {
    	title = name;
    }
    
    public String getName() {
        return title;
    }

    public int getMaxStackSize() {
        return maxStack;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    public void update() {}

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public ItemStack[] getContents() {
        return items;
    }

    public void onOpen(CraftHumanEntity who) {
        viewers.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        viewers.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return viewers;
    }

    public InventoryType getType() {
        return type;
    }

    public void g() {}

    public InventoryHolder getOwner() {
        return owner;
    }

    public void startOpen() {}

    public boolean c() {
        return false;
    }

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }
}