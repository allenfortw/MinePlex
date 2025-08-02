package nautilus.game.arcade.game.minigames.spleef.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.*;

public class KitBrawler extends Kit
{
	public KitBrawler(ArcadeManager manager)
	{
		super(manager, "Brawler", KitAvailability.Green, 

				new String[] 
						{
				"Much stronger knockback than other kits."
						}, 

						new Perk[] 
								{
				new PerkSmasher(),
				new PerkKnockback(0.6)
								}, 
								EntityType.ZOMBIE,
								new ItemStack(Material.IRON_SWORD));

	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD));
	}
}
