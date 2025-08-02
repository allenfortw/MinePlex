package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashSet;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Fortitude extends Skill
{
	private WeakHashMap<Player, Integer> _health = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Long> _last = new WeakHashMap<Player, Long>();

	public Fortitude(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"After taking damage, you slowly",
				"regenerate up to 5 health, at a",
				"rate of 1 health per 1.5 seconds.",
				"",
				"This does not stack, and is reset",
				"if you are hit again."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void RegisterLast(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		int damage = (int) (event.GetDamage()/2);
		if (damage <= 0)		return;

		//Damager
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		int level = GetLevel(damagee);
		if (level  == 0)		return;
		
		_health.put(damagee, Math.min(5, damage));
		_last.put(damagee, System.currentTimeMillis());
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTER)
			return;

		HashSet<Player>	remove = new HashSet<Player>();

		for (Player cur : _health.keySet())
		{
			int level = GetLevel(cur);
			if (level == 0)		continue;

			if (UtilTime.elapsed(_last.get(cur), 1500))
			{
				_health.put(cur, _health.get(cur) - 1);
				_last.put(cur, System.currentTimeMillis());

				if (_health.get(cur) <= 0)
					remove.add(cur);

				//Heal
				UtilPlayer.health(cur, 1);
			}
		}

		for (Player cur : remove)
		{
			_health.remove(cur);
			_last.remove(cur);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_health.remove(player);
		_last.remove(player);
	}
}
