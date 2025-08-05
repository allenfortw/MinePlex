package nautilus.game.arcade.game.games.sneakyassassins.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkSmokebomb;

public class KitAssassin extends SneakyAssassinKit
{
	public KitAssassin(ArcadeManager manager, EntityType disguiseType)
	{
		super(manager, "Ranged Assassin", KitAvailability.Gem, 
				new String[]
						{
				"Skilled at ranged assassination!"
						}, 
						new Perk[]
								{
				new PerkSmokebomb(Material.INK_SACK, 3, true)
								}, 
								new ItemStack(Material.BOW),
				disguiseType);
	}

	@Override
	public void GiveItems(Player player)
	{
		super.GiveItems(player);

		player.getInventory().addItem(new ItemStack(Material.BOW));
		player.getInventory().addItem(new ItemStack(Material.ARROW, 32));
	}
}
