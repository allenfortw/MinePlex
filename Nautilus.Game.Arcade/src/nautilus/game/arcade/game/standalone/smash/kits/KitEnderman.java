package nautilus.game.arcade.game.standalone.smash.kits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.disguise.disguises.DisguiseEnderman;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkBlink;
import nautilus.game.arcade.kit.perks.PerkBlockToss;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.event.PerkBlockGrabEvent;
import nautilus.game.arcade.kit.perks.event.PerkBlockThrowEvent;

public class KitEnderman extends SmashKit
{
	public HashMap<Player, DisguiseEnderman> _disguises = new HashMap<Player, DisguiseEnderman>();

	public KitEnderman(ArcadeManager manager)
	{
		super(manager, "Enderman", KitAvailability.Green, 

				new String[] 
						{
						}, 

						new Perk[] 
								{
				new PerkSmashStats(7, 1.3, 0.25, 6),
				new PerkDoubleJump("Double Jump", 0.9, 0.9, false),
				new PerkBlink("Blink", 12, 6000),
				new PerkBlockToss()
								}, 
								EntityType.ENDERMAN,
								new ItemStack(Material.ENDER_PEARL));

	}

	@Override
	public void GiveItems(Player player) 
	{
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, (byte)0, 1, 
				C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Block Toss",
				new String[]
						{
			ChatColor.RESET + "Picks up a block from the ground, and",
			ChatColor.RESET + "then hurls it at opponents, causing huge",
			ChatColor.RESET + "damage and knockback if it hits.",
			ChatColor.RESET + "",
			ChatColor.RESET + "The longer you hold the block, the harder",
			ChatColor.RESET + "you throw it. You will hear a 'tick' sound",
			ChatColor.RESET + "when it is fully charged.",
						}));

		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, (byte)0, 1, 
				C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Blink",
				new String[]
						{
			ChatColor.RESET + "Instantly teleport in the direction",
			ChatColor.RESET + "you are looking.",
			ChatColor.RESET + "",
			ChatColor.RESET + "You cannot pass through blocks.",
						}));

		player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
		player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));

		//Disguise
		DisguiseEnderman disguise = new DisguiseEnderman(player);
		disguise.SetName(C.cYellow + player.getName());
		disguise.SetCustomNameVisible(true);
		disguise.a(false);
		Manager.GetDisguise().disguise(disguise);

		_disguises.put(player, disguise);
	}

	@EventHandler
	public void BlockGrab(PerkBlockGrabEvent event)
	{
		SetBlock(_disguises.get(event.GetPlayer()), event.GetId(), event.GetData());
	}

	@EventHandler
	public void BlockThrow(PerkBlockThrowEvent event)
	{
		SetBlock(_disguises.get(event.GetPlayer()), 0, (byte)0);
	}

	@EventHandler
	public void Death(PlayerDeathEvent event)
	{
		SetBlock(_disguises.get(event.getEntity()), 0, (byte)0);
	}

	public void SetBlock(DisguiseEnderman disguise, int id, byte data)
	{
		if (disguise == null)
			return;

		disguise.SetCarriedId(id);
		disguise.SetCarriedData(data);

		Manager.GetDisguise().updateDisguise(disguise);
	}
	
	@EventHandler
	public void cleanDisguises(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Iterator<Entry<Player, DisguiseEnderman>> iterator = _disguises.entrySet().iterator(); iterator.hasNext();)
		{
			Entry<Player, DisguiseEnderman> current = iterator.next();
			
			if (!Manager.GetDisguise().isDisguised(current.getKey()))
			{
				iterator.remove();
			}
			else if (Manager.GetDisguise().getDisguise(current.getKey()) != current.getValue())
			{
				iterator.remove();
			}
		}
	}
	
	@Override
	public int GetCost() 
	{
		return 4000;
	}
}
