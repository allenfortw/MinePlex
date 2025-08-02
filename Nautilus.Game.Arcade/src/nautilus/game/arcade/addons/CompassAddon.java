package nautilus.game.arcade.addons;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.GameTeam;

public class CompassAddon extends MiniPlugin
{
	public ArcadeManager Manager;
	
	public CompassAddon(JavaPlugin plugin, ArcadeManager manager)
	{
		super("Compass Addon", plugin);
		
		Manager = manager;
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		if (Manager.GetGame() == null)
			return;
				
		if (!Manager.GetGame().IsLive())
			return;
		
		for (Player player : UtilServer.getPlayers())
		{
			if (!Manager.GetGame().CompassEnabled && Manager.GetGame().IsAlive(player)) 
				continue;
			
			GameTeam team = Manager.GetGame().GetTeam(player);
			
			Player target = null;
			GameTeam targetTeam = null;
			double bestDist = 0;
			
			for (Player other : Manager.GetGame().GetPlayers(true))
			{
				if (other.equals(player))
					continue;
				
				GameTeam otherTeam = Manager.GetGame().GetTeam(other);
				
				//Same Team (Not Solo Game) && Alive
				if (Manager.GetGame().GetTeamList().size() > 1 && (team != null && team.equals(otherTeam)) && Manager.GetGame().IsAlive(player))
					continue;
				
				double dist = UtilMath.offset(player, other);
				
				if (target == null || dist < bestDist)
				{
					target = other;
					targetTeam = otherTeam;
					bestDist = dist;
				}
			}
			
			if (target != null)
			{
				if (!player.getInventory().contains(Material.COMPASS))
					player.getInventory().addItem(new ItemStack(Material.COMPASS));
				
				player.setCompassTarget(target.getLocation());
				
				for (int i : player.getInventory().all(Material.COMPASS).keySet()) 
				{
					ItemStack stack = player.getInventory().getItem(i);

					double heightDiff = target.getLocation().getY() - player.getLocation().getY();

					ItemMeta itemMeta = stack.getItemMeta();
					itemMeta.setDisplayName(
							"    " + C.cWhite + C.Bold + "Nearest Player: " + targetTeam.GetColor() + target.getName() + 
							"    " + C.cWhite + C.Bold + "Distance: " + targetTeam.GetColor() + UtilMath.trim(1, bestDist) +
							"    " + C.cWhite + C.Bold + "Height: " + targetTeam.GetColor() + UtilMath.trim(1, heightDiff));
					stack.setItemMeta(itemMeta);
					
					player.getInventory().setItem(i, stack);
				}
			}
		}
	}
	
	@EventHandler
	public void DropItem(PlayerDropItemEvent event)
	{
		if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.COMPASS, (byte)0))
			return;

		//Cancel
		event.setCancelled(true);

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot drop " + F.item("Target Compass") + "."));
	}

	@EventHandler
	public void DeathRemove(PlayerDeathEvent event)
	{	
		HashSet<org.bukkit.inventory.ItemStack> remove = new HashSet<org.bukkit.inventory.ItemStack>();

		for (org.bukkit.inventory.ItemStack item : event.getDrops())
			if (UtilInv.IsItem(item, Material.COMPASS, (byte)0))
				remove.add(item);

		for (org.bukkit.inventory.ItemStack item : remove)
			event.getDrops().remove(item);
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent event)
	{
		UtilInv.DisallowMovementOf(event, "Target Compass", Material.COMPASS, (byte)0, true);
	}
	
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent event)
	{
		if (Manager.GetGame() == null)
			return;
	
		Player player = event.getPlayer();
		
		if (!UtilGear.isMat(player.getItemInHand(), Material.COMPASS))
			return;
			
		if (Manager.GetGame().IsAlive(player))
			return;
		
		GameTeam team = Manager.GetGame().GetTeam(player);
		
		Player target = null;
		double bestDist = 0;
		
		for (Player other : Manager.GetGame().GetPlayers(true))
		{
			GameTeam otherTeam = Manager.GetGame().GetTeam(other);
			
			//Same Team (Not Solo Game) && Alive
			if (Manager.GetGame().GetTeamList().size() > 1 && (team != null && team.equals(otherTeam)) && Manager.GetGame().IsAlive(player))
				continue;
			
			double dist = UtilMath.offset(player, other);
			
			if (target == null || dist < bestDist)
			{
				target = other;
				bestDist = dist;
			}
		}
		
		if (target != null)
		{
			player.teleport(target.getLocation().add(0, 1, 0));
		}
	}
}
