package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkChickenRocket;
import nautilus.game.arcade.kit.perks.PerkEggGun;
import nautilus.game.arcade.kit.perks.PerkFlap;
import nautilus.game.arcade.kit.perks.PerkSmashStats;

public class KitChicken extends SmashKit
{
	public KitChicken(ArcadeManager manager)
	{
		super(manager, "Chicken", KitAvailability.Blue, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(4, 2.0, 0.2, 1.5),
				new PerkFlap(0.8, 0.8, false),
				new PerkEggGun(),
				new PerkChickenRocket()

								}, 
								EntityType.CHICKEN,
								new ItemStack(Material.EGG));

	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Egg Blaster",
				new String[]
						{
			ChatColor.RESET + "Unleash a barrage of your precious eggs.",
			ChatColor.RESET + "They won't deal any knockback, but if",
			ChatColor.RESET + "they they can deal some serious damage.",
						}));

		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Chicken Missile",
				new String[]
						{
			ChatColor.RESET + "Launch one of your newborn babies.",
			ChatColor.RESET + "It will fly forwards and explode if it",
			ChatColor.RESET + "collides with anything, giving large",
			ChatColor.RESET + "damage and knockback to players.",
						}));

		if (Manager.GetGame().GetState() == GameState.Recruit)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.FEATHER, (byte)0, 1, 
					C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Flap",
					new String[]
							{
				ChatColor.RESET + "You are able to use your double jump",
				ChatColor.RESET + "up to 6 times in a row. However, with",
				ChatColor.RESET + "each flap, it loses some potency.",
				ChatColor.RESET + "",
				ChatColor.RESET + C.cAqua + "Flap uses Energy (Experience Bar)",
							}));

		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));

		//Disguise
		DisguiseChicken disguise = new DisguiseChicken(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}
}
