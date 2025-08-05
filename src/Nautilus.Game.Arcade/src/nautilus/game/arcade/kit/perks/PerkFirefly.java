package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.FireflyData;

public class PerkFirefly extends Perk
{
	private HashSet<FireflyData> _data = new HashSet<FireflyData>();
	private int _tick = 0;
	
	public PerkFirefly() 
	{
		super("Firefly", new String[]  
				{
				C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Firefly"
				});
	}

	@EventHandler
	public void Skill(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if (UtilBlock.usable(event.getClickedBlock()))
			return;
		
		if (event.getPlayer().getItemInHand() == null)
			return;
		
		if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE"))
			return;
		
		Player player = event.getPlayer();
		
		if (!Kit.HasKit(player))
			return;
		
		if (!Recharge.Instance.use(player, GetName(), 10000, true))
			return;
		
		_data.add(new FireflyData(player));
		
		UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		_tick = (_tick + 1)%3;
		
		Iterator<FireflyData> dataIterator = _data.iterator();
		
		while (dataIterator.hasNext())
		{
			FireflyData data = dataIterator.next();
			
			//Teleport
			if (!UtilTime.elapsed(data.Time, 1000))
			{
				data.Player.setVelocity(new Vector(0,0,0));//.teleport(data.Location);	
				data.Player.getWorld().playSound(data.Location, Sound.EXPLODE, 0.2f, 0.6f);
				
				if (_tick == 0)
				{
					//Firework
					FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.ORANGE).with(Type.BURST).trail(false).build();

					try 
					{
						Manager.GetFirework().playFirework(data.Player.getLocation(), effect);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				
			}
			//Velocity
			else if (!UtilTime.elapsed(data.Time, 2000))
			{
				data.Player.setVelocity(data.Player.getLocation().getDirection().multiply(0.7).add(new Vector(0,0.1,0)));
				data.Player.getWorld().playSound(data.Location, Sound.EXPLODE, 0.6f, 1.2f);
				
				if (_tick == 0)
				{
					//Firework
					FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.RED).with(Type.BURST).trail(false).build();

					try 
					{
						Manager.GetFirework().playFirework(data.Player.getLocation(), effect);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}	
				}	
				
				
				for (Player other : UtilPlayer.getNearby(data.Player.getLocation(), 3))
				{
					if (other.equals(data.Player))
						continue;
					
					if (!Manager.GetGame().IsAlive(other))
						continue;
					
					other.playEffect(EntityEffect.HURT);
					
					if (_tick == 0)
						if (!data.Targets.contains(other))
						{
							data.Targets.add(other);
							
							//Damage Event
							Manager.GetDamage().NewDamageEvent(other, data.Player, null, 
									DamageCause.CUSTOM, 12, true, true, false,
									data.Player.getName(), GetName());
							
							UtilPlayer.message(other, F.main("Game", F.elem(Manager.GetColor(data.Player) + data.Player.getName()) + " hit you with " + F.elem(GetName()) + "."));
						}
				}
			}
			else
			{
				dataIterator.remove();
			}
		}		
	}
	
	@EventHandler
	public void FireflyDamage(CustomDamageEvent event)
	{
		Iterator<FireflyData> dataIterator = _data.iterator();
		
		while (dataIterator.hasNext())
		{
			FireflyData data = dataIterator.next();
			
			if (!data.Player.equals(event.GetDamageeEntity()))
				continue;
			
			if (!UtilTime.elapsed(data.Time, 1000) && event.GetCause() == DamageCause.PROJECTILE)
			{
				dataIterator.remove();
			}
			else
			{
				event.SetCancelled("Firefly Immunity");
			}
		}
	}
	
	@EventHandler
	public void Knockback(CustomDamageEvent event)
	{
		if (event.GetReason() == null || !event.GetReason().contains(GetName()))
			return;
		
		event.AddKnockback(GetName(), 2.5);
	}
}
