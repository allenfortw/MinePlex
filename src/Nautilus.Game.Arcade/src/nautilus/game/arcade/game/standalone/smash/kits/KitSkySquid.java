package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseSquid;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkInkBlast;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkSuperSquid;

public class KitSkySquid extends SmashKit
{
	public KitSkySquid(ArcadeManager manager)
	{
		super(manager, "Sky Squid", KitAvailability.Blue, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(5, 1.5, 0.25, 5),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkSuperSquid(),
				new PerkInkBlast(),
								}, 
								EntityType.SQUID,
								new ItemStack(Material.INK_SACK));
	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Super Squid",
				new String[]
						{
			ChatColor.RESET + "You become invulnerable and fly through",
			ChatColor.RESET + "the sky in the direction you are looking.",
						}));
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Ink Shotgun",
				new String[]
						{
			ChatColor.RESET + "Blasts 6 ink pellets out at high velocity.",
			ChatColor.RESET + "They explode upon hitting something, dealing",
			ChatColor.RESET + "damage and knockback.",
						}));

		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
		
		//Disguise
		DisguiseSquid disguise = new DisguiseSquid(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}
}
