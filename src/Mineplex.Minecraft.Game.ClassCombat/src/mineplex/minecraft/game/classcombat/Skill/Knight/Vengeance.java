package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Vengeance extends Skill
{
	private WeakHashMap<Player, HashMap<String, Integer>> _vengeance = new WeakHashMap<Player, HashMap<String, Integer>>();

	public Vengeance(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"When you attack someone, your damage",
				"is increased by 1 for each time the",
				"enemy hurt you since you last hit them,",
				"up to a maximum of 3 bonus damage."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void RegisterDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		int level = GetLevel(damagee);
		if (level == 0)			return;

		//Damager
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!_vengeance.containsKey(damagee))
			_vengeance.put(damagee, new HashMap<String, Integer>());

		//Register Hit
		if (!_vengeance.get(damagee).containsKey(damager.getName()))
			_vengeance.get(damagee).put(damager.getName(), 0);
		else
		{
			_vengeance.get(damagee).put(damager.getName(), Math.min(level * 2, _vengeance.get(damagee).get(damager.getName()) + 1));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void IncreaseDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;
 
		//Damager
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		int level = GetLevel(damager);
		if (level == 0)			return;

		if (!_vengeance.containsKey(damager))
			return;

		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		if (!_vengeance.get(damager).containsKey(damagee.getName()))
			return;
		
		if (_vengeance.get(damager).get(damagee.getName()) == 0)
			return;
		
		//Increase
		event.AddMod(damager.getName(), GetName(), _vengeance.get(damager).remove(damagee.getName()), true);
	}

	@Override
	public void Reset(Player player) 
	{
		_vengeance.remove(player);
	}
}
