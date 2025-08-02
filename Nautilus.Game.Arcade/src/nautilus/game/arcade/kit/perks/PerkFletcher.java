package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import nautilus.game.arcade.kit.Perk;

public class PerkFletcher extends Perk
{
	private HashSet<Entity> _fletchArrows = new HashSet<Entity>();

	private int _max = 0;
	private int _time = 0;
	private boolean _remove;
	
	public PerkFletcher(int time, int max, boolean remove) 
	{
		super("Fletcher", new String[] 
				{
				C.cGray + "Receive 1 Arrow every " + time + " seconds. Maximum of " + max + ".",
				});
		
		_time = time;
		_max = max;
		_remove = remove;
	}

	@EventHandler
	public void FletchShootBow(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player)event.getEntity();

		if (!Kit.HasKit(player))
			return;

		for (int i=0 ; i<=8 ; i++)
			if (player.getInventory().getItem(i) != null)
				if (UtilInv.IsItem(player.getInventory().getItem(i), Material.ARROW, (byte)1))
				{
					_fletchArrows.add(event.getProjectile());
					return;
				}	
	}

	@EventHandler
	public void FletchProjectileHit(ProjectileHitEvent event)
	{
		if (_remove)
			if (_fletchArrows.remove(event.getEntity()))
				event.getEntity().remove();
	}

	@EventHandler
	public void Fletch(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			if (!Kit.HasKit(cur))
				continue;
			
			if (!Manager.GetGame().IsAlive(cur))
				continue;

			if (!Recharge.Instance.use(cur, GetName(), _time * 1000, false))
				continue;

			if (UtilInv.contains(cur, Material.ARROW, (byte)1, _max))
				continue;

			//Add
			cur.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Fletched Arrow")));

			cur.playSound(cur.getLocation(), Sound.ITEM_PICKUP, 2f, 1f);
		}
	}

	@EventHandler
	public void FletchDrop(PlayerDropItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.ARROW, (byte)1))
			return;

		//Cancel
		event.setCancelled(true);

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Fletched Arrow") + "."));
	}

	@EventHandler
	public void FletchDeathRemove(PlayerDeathEvent event)
	{	
		HashSet<org.bukkit.inventory.ItemStack> remove = new HashSet<org.bukkit.inventory.ItemStack>();

		for (org.bukkit.inventory.ItemStack item : event.getDrops())
			if (UtilInv.IsItem(item, Material.ARROW, (byte)1))
				remove.add(item);

		for (org.bukkit.inventory.ItemStack item : remove)
			event.getDrops().remove(item);
	}

	@EventHandler
	public void FletchInvClick(InventoryClickEvent event)
	{
		UtilInv.DisallowMovementOf(event, "Fletched Arrow", Material.ARROW, (byte)1, true);
	}

	@EventHandler
	public void FletchClean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Iterator<Entity> arrowIterator = _fletchArrows.iterator(); arrowIterator.hasNext();) 
		{
			Entity arrow = arrowIterator.next();

			if (arrow.isDead() || !arrow.isValid())
				arrowIterator.remove();
		}
	}
}
