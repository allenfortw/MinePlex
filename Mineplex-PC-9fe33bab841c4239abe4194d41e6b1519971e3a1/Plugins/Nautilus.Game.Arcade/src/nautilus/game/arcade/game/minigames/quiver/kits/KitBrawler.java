package nautilus.game.arcade.game.minigames.quiver.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
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
				"Missed your arrow? Not a big deal."
						}, 

						new Perk[] 
								{ 
				new PerkStrength(1)
								}, 
								EntityType.ZOMBIE,
								new ItemStack(Material.IRON_SWORD));

	}
	
	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD));
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.BOW));
		
		if (Manager.GetGame().GetState() == GameState.Live)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Super Arrow")));
	}
}
