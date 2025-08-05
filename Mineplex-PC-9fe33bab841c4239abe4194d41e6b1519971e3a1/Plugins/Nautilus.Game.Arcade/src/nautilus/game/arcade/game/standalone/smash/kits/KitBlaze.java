package nautilus.game.arcade.game.standalone.smash.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseBlaze;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkFirefly;
import nautilus.game.arcade.kit.perks.PerkInferno;
import nautilus.game.arcade.kit.perks.PerkKnockbackFire;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkSpeed;

public class KitBlaze extends SmashKit
{
	public KitBlaze(ArcadeManager manager)
	{
		super(manager, "Blaze", KitAvailability.Green, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(6, 1.5, 0.25, 5),
				new PerkDoubleJump("Double Jump", 1, 1, false),
				new PerkKnockbackFire(1.50),
				new PerkSpeed(0),
				new PerkInferno(),
				new PerkFirefly()
								}, 
								EntityType.BLAZE,
								new ItemStack(Material.BLAZE_ROD));

	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Inferno",
				new String[]
						{
			ChatColor.RESET + "Releases a deadly torrent of flames,",
			ChatColor.RESET + "which ignite and damage opponents.",
						}));


		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Firefly",
				new String[]
						{
			ChatColor.RESET + "After a short startup time, you fly",
			ChatColor.RESET + "forward with great power, destroying",
			ChatColor.RESET + "anyone you touch.",
			ChatColor.RESET + "",
			ChatColor.RESET + "If hit are hit by a projectile during",
			ChatColor.RESET + "startup time, the skill is cancelled.",
						}));
		

		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));

		//Disguise
		DisguiseBlaze disguise = new DisguiseBlaze(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}


	@EventHandler
	public void FireItemResist(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (Manager.GetGame() == null)
			return;

		for (Player player : Manager.GetGame().GetPlayers(true))
		{
			if (!HasKit(player))
				continue;

			Manager.GetCondition().Factory().FireItemImmunity(GetName(), player, player, 1.9, false);
		}
	}
	
	@Override
	public int GetCost() 
	{
		return 6000;
	}
}
