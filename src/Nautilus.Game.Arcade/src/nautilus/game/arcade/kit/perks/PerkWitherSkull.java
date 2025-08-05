package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Perk;

public class PerkWitherSkull extends Perk
{
	private HashMap<WitherSkull, Vector> _active = new HashMap<WitherSkull, Vector>();
	private HashSet<Player> _ignoreControl = new HashSet<Player>();
	
	public PerkWitherSkull() 
	{
		super("Wither Skull", new String[] 
				{ 
				C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Wither Skull"
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
		
		if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD"))
			return;
		
		Player player = event.getPlayer();
		
		if (!Kit.HasKit(player))
			return;
		
		if (!Recharge.Instance.use(player, GetName(), 6000, true))
			return;
		
		//Fire
		WitherSkull skull = player.launchProjectile(WitherSkull.class);
		skull.setDirection(player.getLocation().getDirection());
	
		_active.put(skull, player.getLocation().getDirection());
		
		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1f, 1f);

		//Inform
		UtilPlayer.message(player, F.main("Skill", "You launched " + F.skill(GetName()) + "."));
		
		//Control
		_ignoreControl.remove(player);
	}
	
	@EventHandler
	public void Update(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.TICK)
			return;

		Iterator<WitherSkull> skullIterator = _active.keySet().iterator();
		
		while (skullIterator.hasNext())
		{
			WitherSkull skull = skullIterator.next();
			Player player = (Player)skull.getShooter();
			
			if (!skull.isValid())
			{
				skullIterator.remove();
				skull.remove();
				continue;
			}
			
			if (player.isBlocking() && !_ignoreControl.contains(player))
			{
				skull.setDirection(player.getLocation().getDirection());
				skull.setVelocity(player.getLocation().getDirection().multiply(0.6));
				_active.put(skull, player.getLocation().getDirection().multiply(0.6));
			}
			else
			{
				_ignoreControl.add(player);
				skull.setDirection(_active.get(skull));
				skull.setVelocity(_active.get(skull));
			}
		}
	}
	
	@EventHandler
	public void Explode(EntityExplodeEvent event)
	{
		if (!_active.containsKey(event.getEntity()))
			return;
		
		event.setCancelled(true);
		
		WitherSkull skull = (WitherSkull)event.getEntity();
		
		Explode(skull, event.getLocation(), skull.getShooter());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void ExplodeDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetProjectile() != null && event.GetProjectile() instanceof WitherSkull)
			event.SetCancelled("Wither Skull Cancel");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void DirectHitDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;
		
		if (event.GetDamageInitial() != 7)
			return;
		
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;
		
		if (!Kit.HasKit(damager))
			return;
		
		if (!Manager.IsAlive(damager))
			return;
		
		event.SetCancelled("Wither Skull Direct Hit");
	}
	
	private void Explode(WitherSkull skull, Location loc, LivingEntity shooter) 
	{	
		double scale = 0.4 + 0.6 * Math.min(1, skull.getTicksLived()/20d);
		
		//Damage
		for (Entity ent : skull.getWorld().getEntities())
		{
			if (!(ent instanceof LivingEntity))
				continue;
			
			if (UtilMath.offset(loc, ent.getLocation()) > 2)
				continue;

			if (ent instanceof Player)
				if (!Manager.GetGame().IsAlive((Player)ent))
					continue;

			LivingEntity livingEnt = (LivingEntity)ent;
			
			//Damage Event
			Manager.GetDamage().NewDamageEvent(livingEnt, shooter, null, 
					DamageCause.CUSTOM, 12 * scale, false, true, false,
					UtilEnt.getName(shooter), GetName());
			
			UtilAction.velocity(livingEnt, UtilAlg.getTrajectory2d(loc, livingEnt.getLocation()), 1.6 * scale, true, 0.8 * scale, 0, 10, true);
		}
		
		//Explosion
		loc.getWorld().createExplosion(loc, (float) 2.5);
	}
}
