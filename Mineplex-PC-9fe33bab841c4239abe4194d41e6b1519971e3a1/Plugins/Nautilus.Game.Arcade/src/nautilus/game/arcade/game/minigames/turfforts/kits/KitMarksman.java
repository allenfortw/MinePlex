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

public class KitMarksman extends Kit 
{
	public KitMarksman(ArcadeManager manager)
	{
		super(manager, "Marksman", KitAvailability.Free, 
				new String[] 
				{
					"Unrivaled in archery. One hit kills anyone."
				}, 
				new Perk[] 
				{
				new PerkConstructor("Constructor", 4, 8, Material.WOOL, "Wool", false),
				new PerkFletcher(4, 2, false),
				
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
