package nautilus.game.arcade.game.games.wizards.kit;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.wizards.Wizards;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;

public class KitWitchDoctor extends Kit
{
	public KitWitchDoctor(ArcadeManager manager)
	{
		super(manager, "Witch Doctor", KitAvailability.Achievement, new String[]
			{
				"Max mana increased to 150"
			}, new Perk[0], EntityType.WITCH, new ItemStack(Material.IRON_HOE));
	}

	@Override
	public void GiveItems(Player player)
	{
		((Wizards) this.Manager.GetGame()).setupWizard(player);
	}
}
