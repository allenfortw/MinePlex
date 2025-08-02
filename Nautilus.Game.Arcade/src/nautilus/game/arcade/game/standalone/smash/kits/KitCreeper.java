package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkCreeperElectricity;
import nautilus.game.arcade.kit.perks.PerkCreeperExplode;
import nautilus.game.arcade.kit.perks.PerkCreeperSulphurBomb;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;

public class KitCreeper extends SmashKit
{
	public KitCreeper(ArcadeManager manager)
	{
		super(manager, "Creeper", KitAvailability.Green, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(6, 1.65, 0.4, 3.5),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkCreeperElectricity(),
				new PerkCreeperSulphurBomb(),
				new PerkCreeperExplode(),
								}, 
								EntityType.CREEPER,
								new ItemStack(Material.TNT));
	} 

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Sulphur Bomb",
				new String[]
						{
			ChatColor.RESET + "Throw a small bomb of sulphur.",
			ChatColor.RESET + "Explodes on contact with players,",
			ChatColor.RESET + "dealing some damage and knockback.",

						}));

		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Explosive Leap",
				new String[]
						{
			ChatColor.RESET + "You freeze in location and charge up",
			ChatColor.RESET + "for 1.5 seconds. Then you explode!",
			ChatColor.RESET + "You are sent flying in the direction",
			ChatColor.RESET + "you are looking, while opponents take",
			ChatColor.RESET + "large damage and knockback.",

						}));
		if (Manager.GetGame().GetState() == GameState.Recruit)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.NETHER_STAR, (byte)0, 1, 
					C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Lightning Shield",
					new String[]
							{
				ChatColor.RESET + "When attacked by a non-melee attack,",
				ChatColor.RESET + "you gain Lightning Shield for 3 seconds.",
				ChatColor.RESET + "",
				ChatColor.RESET + "Lightning Shield blocks 1 melee attack,",
				ChatColor.RESET + "striking lightning on the attacker.",
							}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.LEATHER_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.LEATHER_BOOTS));

		//Disguise
		DisguiseCreeper disguise = new DisguiseCreeper(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}

	@Override
	public int GetCost() 
	{
		return 4000;
	}
}
