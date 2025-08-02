package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.UtilGear;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Swordsmanship extends Skill
{
	private HashMap<Player, Integer> _charges = new HashMap<Player, Integer>();
	
	public Swordsmanship(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Prepare a powerful sword attack;",
				"You gain 1 Charge every 3 seconds.",
				"You can store a maximum of 3 Charges.",
				"",
				"When you attacked, your damage is",
				"increased by the number of your Charges,",
				"and your Charges reset to 0.",
				"",
				"This only applies for Swords."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void IncreaseDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		//Damagee
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!_charges.containsKey(damager))
			return;
		
		if (!UtilGear.isSword(damager.getItemInHand()))
			return;

		event.AddMod(damager.getName(), GetName(), _charges.remove(damager), false);
	}
	
	@EventHandler
	public void Charge(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		for (Player cur : GetUsers())
		{
			if (!Recharge.Instance.use(cur, GetName(), 3000, false))
				continue;
			
			int charge = 1;
			if (_charges.containsKey(cur))
				charge += _charges.get(cur);
			
			charge = Math.min(3, charge);
			
			_charges.put(cur, charge);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_charges.remove(player);
	}
}
