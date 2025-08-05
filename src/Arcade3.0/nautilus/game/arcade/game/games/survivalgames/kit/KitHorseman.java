package nautilus.game.arcade.game.games.survivalgames.kit;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkHorsePet;

public class KitHorseman extends Kit
{
	public KitHorseman(ArcadeManager manager)
	{
		super(manager, "Horseman", KitAvailability.Achievement,

				new String[] 
						{
				"Proud owner of a (rapidly growing) horse!"
						}, 

						new Perk[] 
								{
				new PerkHorsePet()
								}, 
								EntityType.HORSE,
								new ItemStack(Material.DIAMOND_BARDING));
	}

	@Override
	public void GiveItems(Player player) 
	{
		
	}
}
