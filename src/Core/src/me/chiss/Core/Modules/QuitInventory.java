package me.chiss.Core.Modules;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class QuitInventory 
{
	//Quit Limits
	public int[] limSword = new int[] {3, 0};
	public int[] limSwordEnch = new int[] {12, 0};

	public int[] limAxe = new int[] {3, 0};
	public int[] limAxeEnch = new int[] {12, 0};

	public int[] limBow = new int[] {2, 0};
	public int[] limBowEnch = new int[] {8, 0};;
	public int[] limBowArrow = new int[] {128, 0};

	public int[] limHelm = new int[] {3, 0};
	public int[] limHelmEnch = new int[] {8, 0};

	public int[] limChest = new int[] {3, 0};
	public int[] limChestEnch = new int[] {8, 0};

	public int[] limLeg = new int[] {3, 0};
	public int[] limLegEnch = new int[] {8, 0};

	public int[] limBoot = new int[] {3, 0};
	public int[] limBootEnch = new int[] {8, 0};

	public int[] limTNT = new int[] {8, 0};

	public int[] limMaterial = new int[] {128, 0};
	public int[] limEmerald = new int[] {64, 0};
	
	private Quit Quit;
	public QuitInventory(Quit quit)
	{
		Quit = quit;
	}
	
	public int CountEnch(ItemStack stack)
	{
		int count = 0;

		for (int cur : stack.getEnchantments().values())
			count += cur;

		return count;
	}
	
	public boolean Check(Player player)
	{
		PlayerInventory inv = player.getInventory();

		for (ItemStack cur : inv.getContents())
		{	
			if (cur == null)						continue;
			if (cur.getType() == Material.AIR)		continue;
			
			//Sword
			if (cur.getType() == Material.IRON_SWORD)				{ limSword[1]++; limSwordEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_SWORD)				{ limSword[1]++; limSwordEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_SWORD)			{ limSword[1]++; limSwordEnch[1] += CountEnch(cur); }
			
			//Axe
			else if (cur.getType() == Material.IRON_AXE)				{ limAxe[1]++; limAxeEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_AXE)				{ limAxe[1]++; limAxeEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_AXE)				{ limSword[1]++; limSwordEnch[1] += CountEnch(cur); }
			
			//Bow
			else if (cur.getType() == Material.BOW)						{ limBow[1]++; limBowEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.ARROW)					{ limBowArrow[1] += cur.getAmount();}
			
			//Helm
			else if (cur.getType() == Material.IRON_HELMET)				{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_HELMET)				{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_HELMET)			{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_HELMET)			{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_HELMET)		{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			
			//Chest
			else if (cur.getType() == Material.IRON_CHESTPLATE)			{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_CHESTPLATE)			{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_CHESTPLATE)		{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_CHESTPLATE)		{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_CHESTPLATE)	{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			
			//Leg
			else if (cur.getType() == Material.IRON_LEGGINGS)			{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_LEGGINGS)			{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			
			//Boot
			else if (cur.getType() == Material.IRON_BOOTS)				{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_BOOTS)				{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			
			//Items
			else if (cur.getType() == Material.TNT)						{ limTNT[1]++;}
			else if (cur.getType() == Material.IRON_ORE)				{ limMaterial[1]++;}
			else if (cur.getType() == Material.IRON_INGOT)				{ limMaterial[1]++;}
			else if (cur.getType() == Material.IRON_BLOCK)				{ limMaterial[1] += 9;}
			else if (cur.getType() == Material.GOLD_ORE)				{ limMaterial[1]++;}
			else if (cur.getType() == Material.GOLD_INGOT)				{ limMaterial[1]++;}
			else if (cur.getType() == Material.GOLD_BLOCK)				{ limMaterial[1] += 9;}
			else if (cur.getType() == Material.DIAMOND)					{ limMaterial[1]++;}
			else if (cur.getType() == Material.DIAMOND_BLOCK)			{ limMaterial[1] += 9;}
			else if (cur.getType() == Material.LEATHER)					{ limMaterial[1]++;}
			else if (cur.getType() == Material.EMERALD)					{ limEmerald[1]++;}
			else if (cur.getType() == Material.EMERALD_BLOCK)			{ limEmerald[1] += 9;}
		}

		for (ItemStack cur : inv.getArmorContents())
		{
			if (cur == null)						continue;
			if (cur.getType() == Material.AIR)		continue;
			
			//Helm
			if (cur.getType() == Material.IRON_HELMET)					{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_HELMET)				{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_HELMET)			{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_HELMET)			{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_HELMET)		{ limHelm[1]++; limHelmEnch[1] += CountEnch(cur); }
			
			//Chest
			else if (cur.getType() == Material.IRON_CHESTPLATE)			{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_CHESTPLATE)			{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_CHESTPLATE)		{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_CHESTPLATE)		{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_CHESTPLATE)	{ limChest[1]++; limChestEnch[1] += CountEnch(cur); }
			
			//Leg
			else if (cur.getType() == Material.IRON_LEGGINGS)			{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_LEGGINGS)			{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_LEGGINGS)		{ limLeg[1]++; limLegEnch[1] += CountEnch(cur); }
			
			//Boot
			else if (cur.getType() == Material.IRON_BOOTS)				{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.GOLD_BOOTS)				{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.DIAMOND_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.LEATHER_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
			else if (cur.getType() == Material.CHAINMAIL_BOOTS)			{ limBoot[1]++; limBootEnch[1] += CountEnch(cur); }
		}

		boolean valid = true;
		
		//Weapons
		if (limSword[1] > limSword[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Swords [" + limSword[1] + "/" + limSword[0] + "]."));
			valid = false;
		}
		
		if (limSwordEnch[1] > limSwordEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Sword Enchantments [" + limSwordEnch[1] + "/" + limSwordEnch[0] + "]."));
			valid = false;
		}
		
		if (limAxe[1] > limAxe[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Axes [" + limAxe[1] + "/" + limAxe[0] + "]."));
			valid = false;
		}
		
		if (limAxeEnch[1] > limAxeEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Axe Enchantments [" + limAxeEnch[1] + "/" + limAxeEnch[0] + "]."));
			valid = false;
		}
		
		if (limBow[1] > limBow[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Bows [" + limBow[1] + "/" + limBow[0] + "]."));
			valid = false;
		}
		
		if (limBowEnch[1] > limBowEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Bow Enchantments [" + limBowEnch[1] + "/" + limBowEnch[0] + "]."));
			valid = false;
		}
		
		if (limBowArrow[1] > limBowArrow[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Arrows [" + limBowArrow[1] + "/" + limBowArrow[0] + "]."));
			valid = false;
		}
		
		//Armor
		if (limHelm[1] > limHelm[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Helmets [" + limHelm[1] + "/" + limHelm[0] + "]."));
			valid = false;
		}
		
		if (limHelmEnch[1] > limHelmEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Helmet Enchantments [" + limHelmEnch[1] + "/" + limHelmEnch[0] + "]."));
			valid = false;
		}
		
		if (limChest[1] > limChest[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Chestplates [" + limChest[1] + "/" + limChest[0] + "]."));
			valid = false;
		}
		
		if (limChestEnch[1] > limChestEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Chestplate Enchantments [" + limChestEnch[1] + "/" + limChestEnch[0] + "]."));
			valid = false;
		}
		
		if (limLeg[1] > limLeg[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Leggings [" + limLeg[1] + "/" + limLeg[0] + "]."));
			valid = false;
		}
		
		if (limLegEnch[1] > limLegEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Legging Enchantments [" + limLegEnch[1] + "/" + limLegEnch[0] + "]."));
			valid = false;
		}
		
		if (limBoot[1] > limBoot[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Boots [" + limBoot[1] + "/" + limBoot[0] + "]."));
			valid = false;
		}
		
		if (limBootEnch[1] > limBootEnch[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Boot Enchantments [" + limBootEnch[1] + "/" + limBootEnch[0] + "]."));
			valid = false;
		}
		
		//Items
		if (limTNT[1] > limTNT[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too much TNT [" + limTNT[1] + "/" + limTNT[0] + "]."));
			valid = false;
		}
		
		if (limMaterial[1] > limMaterial[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too much Ore/Material [" + limMaterial[1] + "/" + limMaterial[0] + "]."));
			valid = false;
		}
		
		if (limEmerald[1] > limEmerald[0])
		{
			UtilPlayer.message(player, F.main(Quit.GetName(), "You have too many Emeralds [" + limEmerald[1] + "/" + limEmerald[0] + "]."));
			valid = false;
		}
		
		return valid;
	}
}
