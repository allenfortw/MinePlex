package nautilus.game.arcade.game.games.draw.kits;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;

public class KitTools extends Kit
{
	public KitTools(ArcadeManager manager)
	{
		super(manager, "Extra Tools", KitAvailability.Achievement, 

				new String[] 
						{
				"Can draw lines, circles and squares!"
						}, 

						new Perk[] 
								{ 
				
								}, 
								EntityType.SKELETON,
								null);
	}

	@Override
	public void GiveItems(Player player) 
	{
		
	}
}
