package nautilus.game.arcade.kit.perks;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Perk;

public class PerkHorseKick extends Perk
{
	private HashMap<Player, Long> _active = new HashMap<Player, Long>();
	
	public PerkHorseKick() 
	{
		super("Horse Kick", new String[] 
				{ 
				C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Horse Kick"
				});
	}
	
	
	@EventHandler
	public void Activate(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if (UtilBlock.usable(event.getClickedBlock()))
			return;
		
		if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE"))
			return;
		
		Player player = event.getPlayer();
		
		if (!Kit.HasKit(player))
			return;
		
		if (!Recharge.Instance.use(player, GetName(), 6000, true))
			return;
		
		Horse horse = GetHorse(player);
		
		//Horse Skill
		if (horse != null)
		{
			//Animation
			_active.put(player, System.currentTimeMillis());
			((CraftHorse)horse).getHandle().p(true);
			
			//Attack
			Location loc = horse.getLocation().add(horse.getLocation().getDirection().multiply(1.5));
			
			for (Entity other : horse.getWorld().getEntities())
			{
				if (!(other instanceof LivingEntity))
					continue;
				
				if (other.equals(horse.getPassenger()))
					continue;
				
				if (other.equals(horse))
					continue;
				
				if (UtilMath.offset(loc, other.getLocation()) > 2)
					continue;
				
				//Damage Event
				Manager.GetDamage().NewDamageEvent((LivingEntity)other, (LivingEntity)horse.getPassenger(), null, 
						DamageCause.ENTITY_ATTACK, 8, true, false, false,
						UtilEnt.getName(horse.getPassenger()), GetName());
			}

			UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
		}
		//Player Skill
		else
		{
			
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			if (!_active.containsKey(cur))
				continue;
			
			Horse horse = GetHorse(cur);
			
			//Horse Skill
			if (horse != null)
			{
				if (horse.getPassenger() == null || !(horse.getPassenger() instanceof LivingEntity))
				{
					_active.remove(cur);
					((CraftHorse)horse).getHandle().p(false);
					continue;
				}

				if (UtilTime.elapsed(_active.get(cur), 1000))
				{
					_active.remove(cur);
					((CraftHorse)horse).getHandle().p(false);
					continue;
				}
			}
			//Player Skill
			else
			{
				
			}
		}
	}
	
	public Horse GetHorse(Player player)
	{
		if (player.getVehicle() == null)
			return null;
		
		if (player.getVehicle() instanceof Horse)
			return (Horse)player.getVehicle();
		
		return null;
	}
	
	@EventHandler
	public void Knockback(CustomDamageEvent event)
	{
		if (event.GetReason() == null || !event.GetReason().contains(GetName()))
			return;
		
		event.AddKnockback(GetName(), 2);
	}
}
