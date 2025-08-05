package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseSpider;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkSpiderLeap;
import nautilus.game.arcade.kit.perks.PerkNeedler;
import nautilus.game.arcade.kit.perks.PerkWebShot;

public class KitSpider extends SmashKit
{
	public KitSpider(ArcadeManager manager)
	{
		super(manager, "Spider", KitAvailability.Free, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(6, 1.75, 0.25, 5.5),
				new PerkSpiderLeap(),
				new PerkNeedler(),
				new PerkWebShot(),
								}, 
								EntityType.SPIDER,
								new ItemStack(Material.WEB));
	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Needler",
				new String[]
						{
			ChatColor.RESET + "Quickly spray up to 5 needles from ",
			ChatColor.RESET + "your mouth, dealing damage and small",
			ChatColor.RESET + "knockback to opponents.",
						}));

		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Web Shot",
				new String[]
						{
			ChatColor.RESET + "Launch a web forwards. Upon collision,",
			ChatColor.RESET + "it creates a temporary web that traps.",
			ChatColor.RESET + "opponents.",
						}));

		if (Manager.GetGame().GetState() == GameState.Recruit)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.SPIDER_EYE, (byte)0, 1, 
					C.cYellow + C.Bold + "Double Jump" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Spider Leap",
					new String[]
							{
				ChatColor.RESET + "Your double jump is special. It goes",
				ChatColor.RESET + "exactly in the direction you are looking.",
				ChatColor.RESET + "",
				ChatColor.RESET + C.cAqua + "Spider Leap uses Energy (Experience Bar)",
							}));

		if (Manager.GetGame().GetState() == GameState.Recruit)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.FERMENTED_SPIDER_EYE, (byte)0, 1, 
					C.cYellow + C.Bold + "Crouch" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Wall Grab",
					new String[]
							{
				ChatColor.RESET + "While crouching, you stick to walls.",
				ChatColor.RESET + "",
				ChatColor.RESET + "Grasping onto a wall allows you to",
				ChatColor.RESET + "use Spider Leap again.",
				ChatColor.RESET + "",
				ChatColor.RESET + C.cAqua + "Wall Grab uses Energy (Experience Bar)",
							}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));

		//Disguise
		DisguiseSpider disguise = new DisguiseSpider(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}

	@Override
	public int GetCost() 
	{
		return 2000;
	}
}
