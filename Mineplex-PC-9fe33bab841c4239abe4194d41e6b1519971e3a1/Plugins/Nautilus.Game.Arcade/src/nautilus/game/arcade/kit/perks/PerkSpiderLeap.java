package nautilus.game.arcade.kit.perks;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Perk;

public class PerkSpiderLeap extends Perk
{
	public PerkSpiderLeap() 
	{
		super("Spider Leap", new String[] 
				{
				C.cYellow + "Tap Jump Twice" + C.cGray + " to " + C.cGreen + "Spider Leap",
				C.cYellow + "Hold Crouch" + C.cGray + " to " + C.cGreen + "Wall Grab",
				C.cWhite + "Spider Leap and Wall Grab require Energy (Experience Bar)."
				
				});
	}
	
	@EventHandler
	public void WallClimb(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (player.getGameMode() == GameMode.CREATIVE)
				continue;
			 
			if (!Kit.HasKit(player))
				continue;
			
			if (!player.isSneaking())
			{
				player.setExp((float) Math.min(0.999, player.getExp()+0.01));
				continue;
			}
			
			player.setExp((float) Math.max(0, player.getExp()-(1f/60f)));
			
			if (player.getExp() <= 0)
				continue;
			
			if (!Recharge.Instance.usable(player, GetName()))
				continue;
			
			for (Block block : UtilBlock.getSurrounding(player.getLocation().getBlock()))
			{
				if (!UtilBlock.airFoliage(block))
				{
					player.setVelocity(new Vector(0,0,0));
					AllowFlight(player);
					continue;
				}
			}	
		}
	}

	@EventHandler
	public void FlightHop(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();
		
		if (!Kit.HasKit(player))
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		
		event.setCancelled(true);
		player.setFlying(false);
		
		//Disable Flight
		player.setAllowFlight(false);
		
		//Velocity
		UtilAction.velocity(player, 1.0, 0.2, 1.0, true);
		//UtilAction.velocity(player, player.getLocation().getDirection(), 1.0, true, 1.0, 0, 1.0, true);
		
		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.SPIDER_IDLE, 1f, 1.5f);
		
		Recharge.Instance.use(player, GetName(), 500, false);
	}
	
	@EventHandler
	public void FlightUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (player.getGameMode() == GameMode.CREATIVE)
				continue;
			
			if (!Kit.HasKit(player))
				continue;
			
			if (UtilEnt.isGrounded(player) || UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
				AllowFlight(player);
		}
	}
	
	public void AllowFlight(Player player)
	{
		if (player.getAllowFlight())
			return;
		
		if (player.getExp() > 3f/18f)
		{
			player.setExp((float) Math.max(0, player.getExp()-(3f/18f)));
			player.setAllowFlight(true);
		}
	}
}
