package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Needler extends SkillActive
{
	private HashMap<Player, Integer> _stored = new HashMap<Player, Integer>();
	private HashMap<Player, Long> _fired = new HashMap<Player, Long>();
	 
	private HashSet<Arrow> _arrows = new HashSet<Arrow>();
	
	private boolean _tick = false;
	
	public Needler(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy, int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType,
				cost, levels,
				energy, energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		return true;
	}
	
	@Override
	public void Skill(Player player, int level) 
	{

	}

	@EventHandler
	public void Update(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		_tick = !_tick;
		
		if (_tick)
			return;

		for (Player cur : GetUsers())
		{	
			if (!cur.isBlocking())
				continue;
				
			//Level
			int level = GetLevel(cur);
			if (level == 0)	
				continue;
			//Water
			if (cur.getLocation().getBlock().isLiquid())
			{
				UtilPlayer.message(cur, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in liquids."));
				continue;
			}
			
			//Use Charge
			if (!UseCharge(cur))
				continue;
			
			//Use Energy
			Factory.Energy().Use(cur, GetName(), 3 - (0.2 * level), true, false);

			Arrow arrow = cur.getWorld().spawnArrow(cur.getEyeLocation().add(cur.getLocation().getDirection()), 
					cur.getLocation().getDirection(), 1.6f + (level * 0.4f), 2);
			arrow.setShooter(cur);
			_arrows.add(arrow);
			
			//Set Fired
			_fired.put(cur, System.currentTimeMillis());
			
			//Sound
			cur.getWorld().playSound(cur.getLocation(), Sound.SPIDER_IDLE, 0.8f, 2f);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void Damage(CustomDamageEvent event)
	{
		if (event.GetCause() != DamageCause.PROJECTILE)
			return;

		Projectile projectile = event.GetProjectile();
		if (projectile == null)	return;

		if (!_arrows.remove(projectile))
			return;
		
		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)		return;
		
		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)		return;

		event.SetCancelled(GetName());
		
		//Damage Event
		Factory.Damage().NewDamageEvent(damagee, damager, null, 
				DamageCause.THORNS, 2, true, true, false,
				damager.getName(), GetName());	
		
		Factory.Condition().Factory().Poison(GetName(), damagee, damager, 2, 0, false, true);
	}
	
	public boolean UseCharge(Player player)
	{
		if (!_stored.containsKey(player))
			return false;
		
		int charges = _stored.get(player);
		
		if (charges <= 0)
			return false;
		
		_stored.put(player, charges-1);
		player.setLevel(charges-1);
		
		return true;
	}
	
	@EventHandler
	public void Recharge(UpdateEvent event)
	{
		for (Player cur : GetUsers())
			if (!_stored.containsKey(cur))
			{
				_stored.put(cur, 0);
			}
			else
			{
				//Dont recharge while firing
				if (_fired.containsKey(cur))
					if (!UtilTime.elapsed(_fired.get(cur), 1000 - (GetLevel(cur) * 50)))
						continue;
				
				int charges = _stored.get(cur);

				if (charges >= 3 + (1 * GetLevel(cur)))
					continue;

				if (!Recharge.Instance.use(cur, GetName() + " Recharge", 100 - (GetLevel(cur) * 10), false))
					continue;

				charges += 1;
				
				_stored.put(cur, charges);

				cur.setLevel(charges);
			}
	}
		
	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		for (Iterator<Arrow> arrowIterator = _arrows.iterator(); arrowIterator.hasNext();) 
		{
			Arrow arrow = arrowIterator.next();
			
			if (arrow.isDead() || !arrow.isValid() || arrow.getTicksLived() > 300)
			{
				arrowIterator.remove();
				arrow.remove();
			}
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_stored.remove(player);
		_fired.remove(player);
	}
}
