package me.chiss.Core.Modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilWorld;

public class Fix extends AModule
{
	public Fix(JavaPlugin plugin) 
	{
		super("Glitch Fix", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{

	}

	@Override
	public void commands() 
	{
		AddCommand("speed1");
		AddCommand("speed2");
		AddCommand("speed3");
		AddCommand("speed4");
		AddCommand("speed5");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (cmd.equals("speed1"))
			Condition().Factory().Speed("test", caller, caller, 20, 0, false, true);
		if (cmd.equals("speed2"))
			Condition().Factory().Speed("test", caller, caller, 20, 1, false, true);
		if (cmd.equals("speed3"))
			Condition().Factory().Speed("test", caller, caller, 20, 2, false, true);
		if (cmd.equals("speed4"))
			Condition().Factory().Speed("test", caller, caller, 20, 3, false, true);
		if (cmd.equals("speed5"))
			Condition().Factory().Speed("test", caller, caller, 20, 4, false, true);
	}


	@EventHandler
	public void fixDoorGlitch(BlockPlaceEvent event)
	{
		if (event.getBlock().getTypeId() != 64)
			return;

		//Make Iron Door
		event.getBlockPlaced().setType(Material.IRON_DOOR_BLOCK);

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main(_moduleName, "Please use Iron Doors."));
	}

	@EventHandler
	public void fixWallClimb(BlockBreakEvent event)
	{
		if (!event.isCancelled())
			return;

		Block player = event.getPlayer().getLocation().getBlock();
		Block block = event.getBlock();

		if (player.getRelative(BlockFace.DOWN).getTypeId() != 0)
			return;

		if (block.getY() != player.getY() + 2)
			return;	

		//One HAS to be the same.
		if (block.getX() != player.getX() && block.getZ() != player.getZ())
			return;	

		//One HAS to be offset by 1.
		if (Math.abs(block.getX() - player.getX()) != 1 && Math.abs(block.getZ() - player.getZ()) != 1)
			return;	

		//Teleport
		Teleport().TP(event.getPlayer(), UtilWorld.locMerge(event.getPlayer().getLocation(), player.getLocation().add(0.5, 0, 0.5)));

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main(_moduleName, "Wall Climb Prevented."));
	}

	@EventHandler
	public void fixBlockClimb(PlayerInteractEvent event)
	{
		if (!event.isCancelled())
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!UtilBlock.isBlock(event.getPlayer().getItemInHand()))
			return;

		Block player = event.getPlayer().getLocation().getBlock();
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());

		if (
				Math.abs(event.getPlayer().getLocation().getX() - ((double)block.getX()+0.5)) > 0.8 || 
				Math.abs(event.getPlayer().getLocation().getZ() - ((double)block.getZ()+0.5)) > 0.8 || 
				player.getY() < block.getY() || player.getY() > block.getY() + 1)
			return;

		if (!UtilBlock.solid(block.getRelative(BlockFace.DOWN)))
			return;

		//Teleport
		Teleport().TP(event.getPlayer(), UtilWorld.locMerge(event.getPlayer().getLocation(), block.getLocation().add(0.5, 0, 0.5)));

		//Inform
		UtilPlayer.message(event.getPlayer(), F.main(_moduleName, "Block Climb Prevented."));
	}

	/* 
	private HashMap<Player, Entry<Long,Location>> _sprintMonitor = new HashMap<Player, Entry<Long,Location>>();
	@EventHandler(priority = EventPriority.MONITOR)
	public void fixSprintMod(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player player = event.GetDamagerPlayer(false);
		if (player == null)		return;

		if (!UtilEnt.isGrounded(player))
			return;

		if (!_sprintMonitor.containsKey(player))
			_sprintMonitor.put(player, new AbstractMap.SimpleEntry<Long, Location>(System.currentTimeMillis(), player.getLocation()));
	}

	@EventHandler
	public void fixSprintMove(PlayerMoveEvent event)
	{
		if (!_sprintMonitor.containsKey(event.getPlayer()))
			return;

		if (!UtilEnt.isGrounded(event.getPlayer()))
		{
			_sprintMonitor.remove(event.getPlayer());
			return;
		}
		
		if (event.getPlayer().getLocation().getBlock().isLiquid())
		{
			_sprintMonitor.remove(event.getPlayer());
			return;
		}
		
		if (System.currentTimeMillis() - _sprintMonitor.get(event.getPlayer()).getKey() < 100)
			return;
		
		if (event.getPlayer().isSprinting())
		{
			_sprintMonitor.remove(event.getPlayer());
			return;
		}

		double limit = 0.22;
		for (PotionEffect pe : event.getPlayer().getActivePotionEffects())
			if (pe.getType().equals(PotionEffectType.SPEED))
			{
				if (pe.getAmplifier() == 0) limit = 0.26;
				else if (pe.getAmplifier() == 1) limit = 0.30;
				else if (pe.getAmplifier() == 2) limit = 0.34;
				else if (pe.getAmplifier() == 3) limit = 0.39;
				else if (pe.getAmplifier() == 4) limit = 0.43;
			}

		if (UtilMath.offset(event.getFrom(), event.getTo()) < limit)
		{
			_sprintMonitor.remove(event.getPlayer());
			return;
		}

		if (System.currentTimeMillis() - _sprintMonitor.get(event.getPlayer()).getKey() > 400)
		{
			_sprintMonitor.remove(event.getPlayer());

			Condition().Factory().Slow("Sprint Hack", event.getPlayer(), event.getPlayer(), 12, 4, false, true, true);
			Condition().Factory().Weakness("Sprint Hack", event.getPlayer(), event.getPlayer(), 12, 4, false, true);
			Condition().Factory().Poison("Sprint Hack", event.getPlayer(), event.getPlayer(), 12, 0, false, true);

			//Inform
			UtilPlayer.message(event.getPlayer(), F.main(_moduleName, "Sprint Hack Prevented."));

			System.out.println("Sprint Hack: " + event.getPlayer().getName());
		}
	}

	@EventHandler
	public void fixSprintQuit(PlayerQuitEvent event)
	{
		_sprintMonitor.remove(event.getPlayer());
	}
	
	@EventHandler
	public void fixSprintDamage(CustomDamageEvent event)
	{
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;
		
		_sprintMonitor.remove(damagee);
	}*/
}
