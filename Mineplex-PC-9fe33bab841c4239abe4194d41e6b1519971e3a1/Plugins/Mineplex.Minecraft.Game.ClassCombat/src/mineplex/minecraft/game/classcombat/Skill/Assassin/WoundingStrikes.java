package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class WoundingStrikes extends Skill
{
	private NautHashMap<LivingEntity, NautHashMap<Player, ArrayList<Integer>>> _active = new NautHashMap<LivingEntity, NautHashMap<Player, ArrayList<Integer>>>();
	private NautHashMap<LivingEntity, NautHashMap<Player, Long>> _last = new NautHashMap<LivingEntity, NautHashMap<Player, Long>>();
	
	
	public WoundingStrikes(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your axe attacks deal 4 less damage, but",
				"do an addition 1 damage per second for",
				"6 seconds. Damage over time can stack",
				"up to 3 times."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		int level = GetLevel(damager);
		if (level == 0)		return;
		
		if (!UtilGear.isAxe(damager.getItemInHand()))
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		//Damage
		event.AddMod(damager.getName(), GetName(), -4, true);
		
		//Add Wound
		int ticks = 6;
		
		if (!_active.containsKey(damagee))
			_active.put(damagee, new NautHashMap<Player, ArrayList<Integer>>());
		
		if (!_active.get(damagee).containsKey(damager))
			_active.get(damagee).put(damager, new ArrayList<Integer>());
		
		_active.get(damagee).get(damager).add(ticks);
		
		while (_active.get(damagee).get(damager).size() > 3)
			_active.get(damagee).get(damager).remove(0);
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		Iterator<Entry<LivingEntity, NautHashMap<Player, ArrayList<Integer>>>> damageeIterator = _active.entrySet().iterator();
		
		while (damageeIterator.hasNext())
		{
			Entry<LivingEntity, NautHashMap<Player, ArrayList<Integer>>> damageeEntrySet = damageeIterator.next();
			LivingEntity damagee = damageeEntrySet.getKey();
			NautHashMap<Player, ArrayList<Integer>> damagers = damageeEntrySet.getValue();
			
			//First
			if (!_last.containsKey(damagee))
				_last.put(damagee, new NautHashMap<Player, Long>());
			
			Iterator<Entry<Player, ArrayList<Integer>>> damagerIterator = damagers.entrySet().iterator();
			
			while (damagerIterator.hasNext())
			{
				Entry<Player, ArrayList<Integer>> damagerEntrySet = damagerIterator.next();
				Player damager = damagerEntrySet.getKey();
				ArrayList<Integer> damagerTicks = damagerEntrySet.getValue();
				
				//First
				if (!_last.get(damagee).containsKey(damager))
				{
					_last.get(damagee).put(damager, System.currentTimeMillis());
					continue;
				}
				
				//Delay
				if (!UtilTime.elapsed(_last.get(damagee).get(damager), 1000))
					continue;
				
				_last.get(damagee).put(damager, System.currentTimeMillis());
				
				//Expire
				while (!damagerTicks.isEmpty() && damagerTicks.get(0) <= 0)
				{
					damagerTicks.remove(0);
				}
				
				//Decrement
				for (int i=0 ; i < damagerTicks.size() ; i++)
				{
					int ticks = damagerTicks.get(i);
					damagerTicks.set(i, ticks-1);
				}

				//Damage
				int stacks = damagerTicks.size();
				
				if (stacks == 0)
				{
					damagerIterator.remove();
					
					if (damagers.isEmpty())
					{
						damageeIterator.remove();
					}
				}
				else
				{
					Factory.Damage().NewDamageEvent(damagee, damager, null, DamageCause.CUSTOM, stacks, false, true, false, null, null);
				}
			}
		}
	}

	@Override
	public void Reset(Player player)
	{
		_active.remove(player);
		_last.remove(player);
	}
}
