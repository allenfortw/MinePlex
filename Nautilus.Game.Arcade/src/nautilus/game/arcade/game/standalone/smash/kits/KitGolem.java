package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseIronGolem;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.*;

public class KitGolem extends SmashKit
{
	public KitGolem(ArcadeManager manager)
	{
		super(manager, "Iron Golem", KitAvailability.Free, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(7, 1.0, 0.25, 8),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkSlow(0),
				new PerkFissure(),
				new PerkSeismicSlam(),
				
								}, 
								EntityType.IRON_GOLEM,
								new ItemStack(Material.IRON_BLOCK));
	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Fissure",
				new String[]
						{
			ChatColor.RESET + "Smash the ground with such power that",
			ChatColor.RESET + "a line of earth fissures infront of you.",
			ChatColor.RESET + "",
			ChatColor.RESET + "The initial slam path Slows opponents.",
			ChatColor.RESET + "The fissure gives damage and knockback.",
			
						}));
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Seismic Slam",
				new String[]
						{
			ChatColor.RESET + "Take a mighty leap into the air, then",
			ChatColor.RESET + "slam back into the gruond with huge force.",
			ChatColor.RESET + "Nearby opponents take damage and knockback.",
						}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.IRON_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.IRON_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.IRON_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_BOOTS));
		
		//Disguise
		DisguiseIronGolem disguise = new DisguiseIronGolem(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}
}
