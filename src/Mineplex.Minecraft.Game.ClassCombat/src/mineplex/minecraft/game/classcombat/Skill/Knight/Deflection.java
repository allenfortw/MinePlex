package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Deflection extends Skill
{
	private HashMap<Player, Integer> _charges = new HashMap<Player, Integer>();

	public Deflection(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Prepare to deflect incoming attacks.",
				"You gain 1 Charge every 3 seconds.",
				"You can store a maximum of 5 Charges.",
				"",
				"When you are attacked, the damage is",
				"reduced by the number of your Charges,",
				"and your Charges reset to 0.",
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void DecreaseDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		if (!_charges.containsKey(damagee))
			return;

		event.AddMod(damagee.getName(), GetName(), -_charges.remove(damagee), false);
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
			
			charge = Math.min(5, charge);
			
			_charges.put(cur, charge);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_charges.remove(player);
	}
}
