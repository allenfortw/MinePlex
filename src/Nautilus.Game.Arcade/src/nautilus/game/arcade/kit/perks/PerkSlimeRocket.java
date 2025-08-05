package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;

import nautilus.game.arcade.kit.Perk;

public class PerkSlimeRocket extends Perk implements IThrown
{
	private HashMap<Player, Slime> _active = new HashMap<Player, Slime>();
	private HashMap<Slime, Player> _owner = new HashMap<Slime, Player>();
	private HashMap<Slime, Long> _lastAttack = new HashMap<Slime, Long>();
	
	public PerkSlimeRocket() 
	{
		super("Slime Rocket", new String[] 
				{ 
				C.cYellow + "Hold/Release Block" + C.cGray + " to use " + C.cGreen + "Slime Rocket"
				});
	}

	@EventHandler
	public void EnergyUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player player : Manager.GetGame().GetPlayers(true))
		{
			if (!Kit.HasKit(player))
				continue;
			
			int size = 1;
			if (player.getExp() > 0.8)			size = 3;
			else if (player.getExp() > 0.55)		size = 2;
			
			
			DisguiseSlime slime = (DisguiseSlime)Manager.GetDisguise().getDisguise(player);
			if (slime != null && slime.GetSize() != size)
			{
				slime.SetSize(size);
				Manager.GetDisguise().updateDisguise(slime);
			}

			if (player.isBlocking())
				continue;

			player.setExp((float) Math.min(0.999, player.getExp()+0.004));
		}
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

		if (!Recharge.Instance.use(player, GetName(), 4000, true))
			return;

		//Spawn Slime
		Manager.GetGame().CreatureAllowOverride = true;
		Slime slime = player.getWorld().spawn(player.getEyeLocation(), Slime.class);
		slime.setSize(1);
		slime.setCustomNameVisible(true);
		Manager.GetGame().CreatureAllowOverride = false;

		player.setPassenger(slime);

		_active.put(player, slime);
		_owner.put(slime, player);

		UtilPlayer.message(player, F.main("Skill", "You are charging " + F.skill(GetName()) + "."));
	}

	@EventHandler
	public void ChargeRelease(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.TICK)
			return;

		Iterator<Player> chargeIterator = _active.keySet().iterator();

		while (chargeIterator.hasNext())
		{
			Player player = chargeIterator.next();
			Slime slime = _active.get(player);

			//Invalid
			if (!slime.isValid())
			{
				slime.remove();
				FireRocket(player);
				chargeIterator.remove();
			}		
			//Charge
			if (player.isBlocking())
			{
				//Energy Depleted
				if (player.getExp() < 0.1)
				{
					FireRocket(player);
					chargeIterator.remove();
				}
				else
				{
					slime.getWorld().playSound(slime.getLocation(), Sound.SLIME_WALK, 0.5f, (float)(0.5 + Math.max(3d, (double)slime.getTicksLived()/20d)/3d));
					
					if (slime.getTicksLived() > 60)
					{
						if (slime.getTicksLived() > 100)
						{
							FireRocket(player);
							chargeIterator.remove();
						}
					}
					else
					{
						player.setExp((float) Math.max(0, player.getExp()-0.01f));

						//Size
						slime.setSize((int) (1.25 + slime.getTicksLived()/25d));
						
						slime.setMaxHealth(5 + slime.getTicksLived()/3);
						slime.setHealth(slime.getMaxHealth());

						//NameTag
						String out = C.cRed;

						if (slime.getSize() == 1)		out = C.cGold;
						else if (slime.getSize() == 2)	out = C.cYellow;
						else if (slime.getSize() == 3)	out = C.cGreen;

						for (int i=0 ; i < slime.getTicksLived()/4 ; i++)
						{
							out += "|";
						}

						slime.setCustomName(out);
					}
				}
			}
			//Release
			else
			{
				FireRocket(player);
				chargeIterator.remove();
			}
		}
	}

	public void FireRocket(Player player)
	{
		double charge = Math.min(3, _active.get(player).getTicksLived()/20d);

		if (_active.get(player) == null || !_active.get(player).isValid())
		{
			//Inform
			UtilPlayer.message(player, F.main("Skill", "You failed " + F.skill(GetName()) + "."));
			return;
		}

		Slime slime = _active.get(player);
		
		slime.setCustomName(null);
		slime.setCustomNameVisible(false);
		slime.setTicksLived(1);
		
		//Inform
		UtilPlayer.message(player, F.main("Skill", "You released " + F.skill(GetName()) + "."));

		slime.leaveVehicle();
		player.eject();

		UtilAction.velocity(slime, player.getLocation().getDirection(), 1 + charge/2d, false, 0, 0.2, 10, true);
		
		Manager.GetProjectile().AddThrow(slime, player, this, -1, true, true, true, 
				null, 0, 0, null, 0, UpdateType.FASTEST, 2d);
	}

	@EventHandler
	public void SlimeTarget(EntityTargetEvent event)
	{
		if (event.isCancelled())
			return;

		if (!_owner.containsKey(event.getEntity()))
			return;

		if (_owner.get(event.getEntity()).equals(event.getTarget()))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data)
	{
		if (target == null)
			return;
		
		if (!(data.GetThrown() instanceof Slime))
			return;
		
		Slime slime = (Slime)data.GetThrown();

		//Damage Event
		Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
				DamageCause.PROJECTILE, slime.getSize() * 4, true, true, false,
				UtilEnt.getName(data.GetThrower()), GetName());
	}
	
	@EventHandler
	public void Knockback(CustomDamageEvent event)
	{
		if (event.GetReason() == null || !event.GetReason().contains(GetName()))
			return;
		
		event.AddKnockback(GetName(), 2.5);
	}

	@Override
	public void Idle(ProjectileUser data)
	{
		
	}

	@Override
	public void Expire(ProjectileUser data)
	{
		
	}
	
	@EventHandler
	public void SlimeDamage(CustomDamageEvent event)
	{
		if (!(event.GetDamagerEntity(false) instanceof Slime))
			return;
		
		Slime slime = (Slime)event.GetDamagerEntity(false);
		
		
		//Attack Rate
		if (_lastAttack.containsKey(slime) && !UtilTime.elapsed(_lastAttack.get(slime), 500))
		{
			event.SetCancelled("Slime Attack Rate");
			return;
		}
		
		_lastAttack.put(slime, System.currentTimeMillis());
		
		//Get Owner
		Player owner = _owner.get(slime);
		//if (owner != null)
		//	event.SetDamager(owner);  This gives knockback from wrong direction :(
			
			
		if (owner != null && owner.equals(event.GetDamageeEntity()))
		{
			event.SetCancelled("Owner Damage");
			
			//Heal Owner
			if (slime.getVehicle() == null)
			{
				UtilPlayer.health(owner, 1);
			}
		}
		else
		{
			event.AddMod("Slime Damage", "Negate", -event.GetDamageInitial(), false);
			event.AddMod("Slime Damage", "Attack", 2 * slime.getSize(), true);
			event.AddKnockback("Slime Knockback", 2);
		}	 
	}
	
	@EventHandler
	public void SlimeClean(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.SEC)
			return;

		Iterator<Slime> slimeIterator = _owner.keySet().iterator();

		while (slimeIterator.hasNext())
		{
			Slime slime = slimeIterator.next();
			
			//Shrink
			if (slime.getVehicle() == null)
			{
				if (slime.getTicksLived() > 60)
				{
					slime.setTicksLived(1);
					
					Manager.GetBlood().Effects(slime.getLocation(), 6 + 6 * slime.getSize(), 0.2 + 0.1 * slime.getSize(), null, 1f, 1f, Material.SLIME_BALL, (byte)0, 15, false);
					
					if (slime.getSize() <= 1)
						slime.remove();
					else
						slime.setSize(slime.getSize()-1);			
				}
			}
			
			if (!slime.isValid())
				slimeIterator.remove();
		}
		
		slimeIterator = _lastAttack.keySet().iterator();

		while (slimeIterator.hasNext())
		{
			Slime slime = slimeIterator.next();

			if (!slime.isValid())
				slimeIterator.remove();
		}
	}
}
