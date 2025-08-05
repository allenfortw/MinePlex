package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseSnowman;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkArcticAura;
import nautilus.game.arcade.kit.perks.PerkBlizzard;
import nautilus.game.arcade.kit.perks.PerkDamageSnow;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkIcePath;
import nautilus.game.arcade.kit.perks.PerkSmashStats;

public class KitSnowman extends SmashKit
{
	public KitSnowman(ArcadeManager manager)
	{
		super(manager, "Snowman", KitAvailability.Green, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(5, 1.4, 0.4, 6),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkDamageSnow(3, 1.25),
				new PerkArcticAura(),
				new PerkBlizzard(),
				new PerkIcePath(),
								}, 
								EntityType.SNOWMAN,
								new ItemStack(Material.SNOW_BALL));

	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Blizzard",
				new String[]
						{
			ChatColor.RESET + "Release a windy torrent of snow, able",
			ChatColor.RESET + "to blow opponents off the stage.",
			ChatColor.RESET + "",
			ChatColor.RESET + C.cAqua + "Blizzard uses Energy (Experience Bar)",
						}));

		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Ice Path",
				new String[]
						{
			ChatColor.RESET + "Create a temporary icy path in the",
			ChatColor.RESET + "direction you are looking.",
						}));

		if (Manager.GetGame().GetState() == GameState.Recruit)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.SNOW_BLOCK, (byte)0, 1, 
					C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Arctic Aura",
					new String[]
							{
				ChatColor.RESET + "Creates a field of snow around you",
				ChatColor.RESET + "granting 150% damage to opponents",
				ChatColor.RESET + "who are standing on it.",
				ChatColor.RESET + "",
				ChatColor.RESET + "Your aura shrinks on low energy.",
							}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));

		//Disguise
		DisguiseSnowman disguise = new DisguiseSnowman(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}

	@Override
	public int GetCost() 
	{
		return 5000;
	}
}
