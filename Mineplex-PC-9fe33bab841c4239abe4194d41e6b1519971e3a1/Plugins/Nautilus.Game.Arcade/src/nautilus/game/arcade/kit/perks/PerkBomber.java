package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import nautilus.game.arcade.kit.Perk;

public class PerkBomber extends Perk
{
	private HashMap<Entity, Player> _tntMap = new HashMap<Entity, Player>();
	
	private int _spawnRate;
	private int _max;
	
	public PerkBomber(int spawnRate, int max) 
	{
		super("Bomber", new String[] 
				{
				C.cGray + "Receive 1 TNT every " + spawnRate + " seconds. Maximum of " + max + ".",
				C.cYellow + "Click" + C.cGray + " with TNT to " + C.cGreen + "Throw TNT"
				});
		
		_spawnRate = spawnRate;
		_max = max;
	}
	
	public void Apply(Player player) 
	{
		Recharge.Instance.use(player, GetName(), _spawnRate*1000, false);
	}
	
	@EventHandler
	public void TNTSpawn(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			if (!Kit.HasKit(cur))
				continue;
			
			if (!Manager.GetGame().IsAlive(cur))
				continue;

			if (!Recharge.Instance.use(cur, GetName(), _spawnRate*1000, false))
				continue;

			if (UtilInv.contains(cur, Material.TNT, (byte)0, _max))
				continue;

			//Add
			cur.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.TNT, (byte)0, 1, F.item("Throwing TNT")));

			cur.playSound(cur.getLocation(), Sound.ITEM_PICKUP, 2f, 1f);
		}
	}

	@EventHandler
	public void TNTDrop(PlayerDropItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.TNT, (byte)0))
			return;

		//Cancel
		event.setCancelled(true);

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Throwing TNT") + "."));
	}

	@EventHandler
	public void TNTDeathRemove(PlayerDeathEvent event)
	{	
		HashSet<org.bukkit.inventory.ItemStack> remove = new HashSet<org.bukkit.inventory.ItemStack>();

		for (org.bukkit.inventory.ItemStack item : event.getDrops())
			if (UtilInv.IsItem(item, Material.TNT, (byte)0))
				remove.add(item);

		for (org.bukkit.inventory.ItemStack item : remove)
			event.getDrops().remove(item);
	}

	@EventHandler
	public void TNTInvClick(InventoryClickEvent event)
	{
		UtilInv.DisallowMovementOf(event, "Throwing TNT", Material.TNT, (byte)0, true);
	}

	@EventHandler
	public void TNTThrow(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK &&
			event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		Player player = event.getPlayer();
		
		if (!UtilInv.IsItem(player.getItemInHand(), Material.TNT, (byte)0))
			return;
				
		if (!Kit.HasKit(player))
			return;
		
		event.setCancelled(true);
		
		UtilInv.remove(player, Material.TNT, (byte)0, 1);
		UtilInv.Update(player);
		
		TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), TNTPrimed.class);
		
		UtilAction.velocity(tnt, player.getLocation().getDirection(), 0.5, false, 0, 0.1, 10, false);
		
		_tntMap.put(tnt, player);
	}
	
	@EventHandler
	public void ExplosionPrime(ExplosionPrimeEvent event)
	{
		if (!_tntMap.containsKey(event.getEntity()))
			return;
		
		Player player = _tntMap.remove(event.getEntity());
		
		for (Player other : UtilPlayer.getNearby(event.getEntity().getLocation(), 14))
		{
			Manager.GetCondition().Factory().Explosion("Throwing TNT", other, player, 50, 0.1, false, false);
		}
	}
}
