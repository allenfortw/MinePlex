package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkWitherImage;
import nautilus.game.arcade.kit.perks.PerkWitherSkull;

public class KitWitherSkeleton extends SmashKit
{
	public KitWitherSkeleton(ArcadeManager manager)
	{
		super(manager, "Wither Skeleton", KitAvailability.Blue, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(6, 1.2, 0.3, 6),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkWitherSkull(),
				new PerkWitherImage(),
								}, 
								EntityType.SKELETON,
								new ItemStack(Material.IRON_SWORD));
	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Guided Wither Skull",
				new String[]
						{
			ChatColor.RESET + "Launch a Wither Skull forwards, hold",
			ChatColor.RESET + "block to guide the missile! Release",
			ChatColor.RESET + "block to detonate it midair.",
						}));
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Wither Image",
				new String[]
						{
			ChatColor.RESET + "Create an exact image of yourself.",
			ChatColor.RESET + "The copy is launched forwards with",
			ChatColor.RESET + "high speeds. Lasts 8 seconds.",
			ChatColor.RESET + "",
			ChatColor.RESET + "Use the skill again to swap positions",
			ChatColor.RESET + "with your image.",
						}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
		
		//Disguise
		DisguiseSkeleton disguise = new DisguiseSkeleton(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		disguise.SetSkeletonType(SkeletonType.WITHER);
		disguise.hideArmor();
		Manager.GetDisguise().disguise(disguise);
	}
	
	@Override
	public int GetCost() 
	{
		return 6000;
	}
}
