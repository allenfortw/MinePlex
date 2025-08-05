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

public class KitElementalist extends Kit
{
	public KitElementalist(ArcadeManager manager)
	{
		super(manager, "Enchanter", KitAvailability.Blue, 

				new String[] 
						{
				"3 Kills, 1 Arrow."
						}, 

						new Perk[] 
								{ 
				new PerkArrowRebound(2, 1.2f)
								}, 
								EntityType.ZOMBIE,
								new ItemStack(Material.BOW));

	}
	
	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.STONE_SWORD));
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.BOW));
		
		if (Manager.GetGame().GetState() == GameState.Live)
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Super Arrow")));
	}
}
