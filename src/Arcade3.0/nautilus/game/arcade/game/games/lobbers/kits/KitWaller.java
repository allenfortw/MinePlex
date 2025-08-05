package nautilus.game.arcade.game.games.lobbers.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemBuilder;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.lobbers.kits.perks.PerkCraftman;
import nautilus.game.arcade.game.games.lobbers.kits.perks.PerkWaller;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;

public class KitWaller extends Kit
{
	public KitWaller(ArcadeManager manager)
	{
		super(manager, "Waller", KitAvailability.Achievement, 0, new String[]
				{
				"When the times get tough,",
				"build yourself a wall!"
				}, new Perk[]
						{
				new PerkWaller(),
				new PerkCraftman()
						}, EntityType.ZOMBIE,
				new ItemBuilder(Material.SMOOTH_BRICK).setUnbreakable(true).build());
	}

	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().setItem(1, new ItemBuilder(Material.STONE_SPADE).setAmount(3).setTitle(F.item("Wall Builder")).build());
	}

}

