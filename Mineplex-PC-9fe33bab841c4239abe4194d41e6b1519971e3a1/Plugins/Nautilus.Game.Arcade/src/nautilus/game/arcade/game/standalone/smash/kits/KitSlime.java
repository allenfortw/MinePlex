package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSlimeRocket;
import nautilus.game.arcade.kit.perks.PerkSlimeSlam;
import nautilus.game.arcade.kit.perks.PerkSmashStats;

public class KitSlime extends SmashKit
{
	public KitSlime(ArcadeManager manager)
	{
		super(manager, "Slime", KitAvailability.Free, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(6, 1.75, 0.4, 3),
				new PerkDoubleJump("Double Jump", 1.2, 1, false),
				new PerkSlimeSlam(),
				new PerkSlimeRocket(),
								}, 
								EntityType.SLIME,
								new ItemStack(Material.SLIME_BALL));
	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Slime Rocket",
				new String[]
						{
			ChatColor.RESET + "Slowly transfer your slimey goodness into",
			ChatColor.RESET + "a new slime. When you release block, the",
			ChatColor.RESET + "new slime is propelled forward.",
			ChatColor.RESET + "",
			ChatColor.RESET + "The more you charge the ability, the stronger",
			ChatColor.RESET + "the new slime is projected forwards.",
			ChatColor.RESET + "",
			ChatColor.RESET + C.cAqua + "Slime Rocket uses Energy (Experience Bar)",
						}));
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Slime Slam",
				new String[]
						{
			ChatColor.RESET + "Throw your slimey body forwards. If you hit",
			ChatColor.RESET + "another player before you land, you deal",
			ChatColor.RESET + "large damage and knockback to them.",
			ChatColor.RESET + "",
			ChatColor.RESET + "However, you take 50% of the damage and",
			ChatColor.RESET + "knockback in the opposite direction.",
						}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		
		//Disguise
		DisguiseSlime disguise = new DisguiseSlime(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
		
		disguise.SetSize(3);
	}
}
