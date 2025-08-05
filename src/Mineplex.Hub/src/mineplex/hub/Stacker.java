package mineplex.hub;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;

public class Stacker extends MiniPlugin implements IThrown
{
	private ProjectileManager _projectileManager;
	
	private HashSet<String> _disabled = new HashSet<String>();
	private HashSet<Entity> _tempStackShift = new HashSet<Entity>();
	
	public Stacker(JavaPlugin plugin) 
	{
		super("Stacker", plugin);
		
		_projectileManager = new ProjectileManager(plugin);
	}
	
	@EventHandler
	public void ToggleInvolvement(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		if (UtilGear.isMat(player.getItemInHand(), Material.GRILLED_PORK))
		{
			_disabled.add(player.getName());
			UtilPlayer.message(player, F.main("Stacker", "You are no longer stackable... boring..."));
			
			player.getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(Material.PORK, (byte)0, 1, C.cGreen + "Enable Stacker"));
			UtilInv.Update(player);
			
			event.setCancelled(true);
		}
		else if (UtilGear.isMat(player.getItemInHand(), Material.PORK))
		{
			_disabled.remove(player.getName());
			UtilPlayer.message(player, F.main("Stacker", "You are back in the stacking games! Squeeeee!"));
			
			player.getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(Material.GRILLED_PORK, (byte)0, 1, C.cRed + "Disable Stacker"));
			UtilInv.Update(player);
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(Material.GRILLED_PORK, (byte)0, 1, C.cRed + "Disable Stacker"));
	}
	
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		_disabled.remove(event.getPlayer().getName());
		_tempStackShift.remove(event.getPlayer());
	}
	
	@EventHandler
	public void GrabEntity(PlayerInteractEntityEvent event)
	{
		if (event.isCancelled())
			return;
		
		Player stacker = event.getPlayer();

		if (stacker.getGameMode() != GameMode.SURVIVAL)
			return;
		
		if (_disabled.contains(stacker.getName()))
		{
			UtilPlayer.message(stacker, F.main("Stacker", "You are not playing stacker."));
			return;
		}
		
		if (stacker.getVehicle() != null || _tempStackShift.contains(stacker))
		{
			UtilPlayer.message(stacker, F.main("Stacker", "You cannot stack while stacked..."));
			return;
		}
			
		Entity stackee = event.getRightClicked();
		if (stackee == null)
			return;
		
		if (!(stackee instanceof LivingEntity))
			return;
		
		if (stackee instanceof Horse)
			return;
		
		if (stackee instanceof Player && ((Player)stackee).getGameMode() != GameMode.SURVIVAL)
			return;
		
		if (stackee instanceof Player && _disabled.contains(((Player)stackee).getName()))
		{
			UtilPlayer.message(stacker, F.main("Stacker", F.name(UtilEnt.getName(stackee)) + " is not playing stacker."));
			return;
		}		
		
		if (stackee instanceof LivingEntity)
		{
			if (((LivingEntity)stackee).isCustomNameVisible())
			{
				UtilPlayer.message(stacker, F.main("Stacker", "You cannot stack this entity."));
				return;
			}
		}
		
		while (stackee.getVehicle() != null)
			stackee = stackee.getVehicle();
		
		if (stackee.equals(stacker))
			return;
		
		Entity top = stacker;
		while (top.getPassenger() != null)
			top = top.getPassenger();
		
		top.setPassenger(stackee);
		
		UtilPlayer.message(stacker, F.main("Stacker", "You stacked " + F.name(UtilEnt.getName(stackee) + ".")));
		UtilPlayer.message(stackee, F.main("Stacker", "You were stacked by " + F.name(stacker.getName() + ".")));
		UtilPlayer.message(stackee, F.main("Stacker", "Push " + F.skill("Crouch") + " to escape!"));
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void ThrowEntity(PlayerInteractEvent event)
	{
		if (!UtilEvent.isAction(event, ActionType.L))
			return;
		
		Player thrower = event.getPlayer();
		
		if (thrower.getVehicle() != null)
			return;
		
		Entity throwee = thrower.getPassenger();
		if (throwee == null)
			return;
		
		thrower.eject();
		
		Entity throweeStack = throwee.getPassenger();
		if (throweeStack != null)
		{
			throwee.eject();
			throweeStack.leaveVehicle();
			
			final Entity fThrower = thrower;
			final Entity fThroweeStack = throweeStack;
			
			_tempStackShift.add(throweeStack);
			
			GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
			{
				public void run()
				{
					fThrower.setPassenger(fThroweeStack);
					_tempStackShift.remove(fThroweeStack);
				}
			}, 2);
		}
		
		UtilPlayer.message(thrower, F.main("Stacker", "You threw " + F.name(UtilEnt.getName(throwee))));
		UtilPlayer.message(throwee, F.main("Stacker", "You were thrown by " + F.name(thrower.getName())));
		
		UtilAction.velocity(throwee, thrower.getLocation().getDirection(), 1.8, false, 0, 0.3, 2, false);
		
		_projectileManager.AddThrow(throwee, thrower, this, -1, true, false, true, false, 2.4d);
	}
	
	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{
		if (target == null)
			return;
		
		//Velocity
		UtilAction.velocity(target, UtilAlg.getTrajectory2d(data.GetThrown(), target), 1, true, 0.8, 0, 10, true);

		Entity rider = target.getPassenger();
		while (rider != null)
		{
			rider.leaveVehicle();
			rider.setVelocity(new Vector(0.25 - Math.random()/2, Math.random()/2, 0.25 - Math.random()/2));
			rider = rider.getPassenger();
		}
		
		UtilPlayer.message(target, F.main("Stacker", F.name(UtilEnt.getName(data.GetThrower())) + " hit you with " + F.name(UtilEnt.getName(data.GetThrown()))));
		
		//Effect
		data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.HURT, 1f, 1f);
	}

	@Override
	public void Idle(ProjectileUser data) 
	{
		
	}

	@Override
	public void Expire(ProjectileUser data) 
	{
		
	}
	

}
