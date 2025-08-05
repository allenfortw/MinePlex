package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class WolfsPounce extends mineplex.minecraft.game.classcombat.Skill.Skill
{
	private NautHashMap<Player, Integer> _charge = new NautHashMap<Player, Integer>();
	private NautHashMap<Player, Long> _chargeLast = new NautHashMap<Player, Long>();
	private NautHashMap<Player, Long> _pounceTime = new NautHashMap<Player, Long>();
	private NautHashMap<Player, Integer> _pounceCharge = new NautHashMap<Player, Integer>();

	public WolfsPounce(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Hold Block to charge pounce.",
				"Release Block to pounce.",
				"",
				"Attacking while airborne tackles players,",
				"giving Slow 3 for up to 5 seconds."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 10 per 20% Velocity";
	}

	@EventHandler
	public void Pounce(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : UtilServer.getPlayers())
		{	
			//Level
			int level = GetLevel(cur);
			if (level == 0)			continue;
			
			if (cur.isBlocking())
			{
				if (!UtilEnt.isGrounded(cur))
					continue;

				if (cur.getLocation().getBlock().isLiquid())
					continue;

				//Not Charging
				if (!_charge.containsKey(cur))
				{
					if (!Factory.Energy().Use(cur, GetName(level), 10, false, false))
						continue;
					
					SkillTriggerEvent triggerEvent = new SkillTriggerEvent(cur, GetName(), GetClassType());
					UtilServer.getServer().getPluginManager().callEvent(triggerEvent);
					
					if (triggerEvent.IsCancelled())
						continue;
					
					if (!Recharge.Instance.use(cur, GetName(level), 500, false))
						continue;
					
					Factory.Energy().Use(cur, GetName(level), 10, true, false);
					
					//Start Charge 
					_charge.put(cur, 0);
					_chargeLast.put(cur, System.currentTimeMillis());

					//Leap
					UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem("20% Velocity")));
				}
				else
				{
					//Max Charge
					if (_charge.get(cur) >= 4)
						continue;

					//Charge Interval
					if (!UtilTime.elapsed(_chargeLast.get(cur), 1000 - (level * 180)))
						continue;

					if (!Factory.Energy().Use(cur, GetName(level), 10, true, false))
						continue;

					//Increase Charge
					_charge.put(cur, _charge.get(cur) + 1);
					_chargeLast.put(cur, System.currentTimeMillis());

					//Inform
					UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem((_charge.get(cur) * 20 + 20) + "% Velocity")));

					//Effect
					for (int i=_charge.get(cur) ; i>0 ; i--)
						cur.playEffect(cur.getLocation(), Effect.CLICK2, 0);
				}
			}
			//Release Charge
			else if (_charge.containsKey(cur))
			{
				//Action
				int charge = _charge.remove(cur);
				UtilAction.velocity(cur, 0.4 + (0.4*charge), 0.2, 0.6 + (0.1*charge), true);
				_chargeLast.remove(cur);
				_pounceCharge.put(cur, charge);
				_pounceTime.put(cur, System.currentTimeMillis());

				//Effect
				cur.getWorld().playSound(cur.getLocation(), Sound.WOLF_BARK, 1f, 1.2f + (level * 0.2f));
			}	
		}
	} 
	
	@EventHandler(priority = EventPriority.HIGH)
	public void Hit(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;
		
		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		int level = GetLevel(damager);
		if (level == 0)			return;
	
		if (!_pounceTime.containsKey(damager)|| UtilTime.elapsed(_pounceTime.get(damager), 250))
			return;
		
		int charge = _pounceCharge.get(damager);
		
		//Damage
		event.SetKnockback(false);
		event.AddMod(damager.getName(), GetName(), 0, true);
		
		//Condition
		Factory.Condition().Factory().Slow(GetName(), damagee, damager, 1 + charge, 2, false, true, true, true);

		//Effect
		damager.getWorld().playSound(damager.getLocation(), Sound.WOLF_BARK, 0.5f, 0.5f);
		
		_pounceTime.remove(damager);
	}
	
	@EventHandler
	public void Grounded(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		HashSet<Player> expired = new HashSet<Player>();
		
		for (Player cur : _pounceTime.keySet())
			if (UtilEnt.isGrounded(cur))
				if (UtilTime.elapsed(_pounceTime.get(cur), 250))
					expired.add(cur);
		
		for (Player cur : expired)
			_pounceTime.remove(cur);	
	}
	
	@Override
	public void Reset(Player player) 
	{
		_charge.remove(player);
		_chargeLast.remove(player);
		_pounceTime.remove(player);
		_pounceCharge.remove(player);
	}
}
