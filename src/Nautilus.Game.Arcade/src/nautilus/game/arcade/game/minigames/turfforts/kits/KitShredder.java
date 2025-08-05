package nautilus.game.arcade.game.minigames.turfforts.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkFletcher;

public class KitShredder extends Kit 
{
	public KitShredder(ArcadeManager manager)
	{
		super(manager, "Shredder", KitAvailability.Blue, 
				new String[] 
				{
					"Arrows are weaker, but shred through forts."
				}, 
				new Perk[] 
				{
				new PerkConstructor("Constructor", 4, 6, Material.WOOL, "Wool", false),
				new PerkFletcher(4, 2, false),
				new PerkBarrage(5, 250, false),
				}, 
				EntityType.ZOMBIE,	
				new ItemStack(Material.BOW));

	}
	
	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.BOW));
		
		int amount = 4;
		if (!Manager.GetGame().IsLive())
			amount = 48;
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.WOOL, Manager.GetGame().GetTeam(player).GetColorData(), amount));
	}
}
