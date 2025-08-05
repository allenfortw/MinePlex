package nautilus.game.arcade.game.standalone.castlesiege.kits;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkIronSkin;

public class KitUndeadArcher extends Kit 
{
	public KitUndeadArcher(ArcadeManager manager)
	{
		super(manager, "Undead Archer", KitAvailability.Blue, 

				new String[] 
						{
				"Makes use of arrows scavenged from human archers."
						}, 

						new Perk[] 
								{
				new PerkIronSkin(1)
								}, 
								EntityType.SKELETON,
								new ItemStack(Material.BOW));

	}
	
	@Override
	public void GiveItems(Player player)
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.STONE_AXE));
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.BOW));
		
		DisguiseSkeleton disguise = new DisguiseSkeleton(player);
		disguise.SetName(Manager.GetGame().GetTeam(player).GetColor() + player.getName());
		disguise.SetCustomNameVisible(true);
		Manager.GetDisguise().disguise(disguise);
	}
}
