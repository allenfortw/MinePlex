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
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkFletcher;

public class KitInfiltrator extends Kit 
{
	public KitInfiltrator(ArcadeManager manager)
	{
		super(manager, "Infiltrator", KitAvailability.Green, 
				new String[] 
				{
					"Able to travel into the enemies turf, but you",
					"must return to your turf fast, or receive Slow."
				}, 
				new Perk[] 
				{
				new PerkConstructor("Constructor", 4, 4, Material.WOOL, "Wool", false),
				new PerkFletcher(12, 1, false),
				}, 
				EntityType.ZOMBIE,	
				new ItemStack(Material.IRON_SWORD));

	}
	
	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD));
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.BOW));
		
		int amount = 4;
		if (!Manager.GetGame().IsLive())
			amount = 48;
		
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.WOOL, Manager.GetGame().GetTeam(player).GetColorData(), amount));
	}
}
