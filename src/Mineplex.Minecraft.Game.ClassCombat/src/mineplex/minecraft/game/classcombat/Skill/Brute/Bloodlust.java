package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;

public class Bloodlust extends Skill
{
	private WeakHashMap<Player, Long> _time = new WeakHashMap<Player, Long>();
	private WeakHashMap<Player, Integer> _str = new WeakHashMap<Player, Integer>();

	public Bloodlust(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"When an enemy dies within 12 blocks,",
				"you go into a Bloodlust, receiving",
				"Speed 1 and Strength 1 for 8 seconds.",
				"",
				"Bloodlust can stack up to 3 times,",
				"boosting the level of Speed and Strength."
				});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerDeath(CombatDeathEvent event)
	{
		if (!(event.GetEvent().getEntity() instanceof Player))
			return;
		
		for (Player cur : UtilServer.getPlayers())
		{
			if (Expire(cur))
				continue;

			if (!Factory.Relation().CanHurt(cur, (Player)event.GetEvent().getEntity()))
				continue;

			//Level
			int level = GetLevel(cur);
			if (level == 0)			continue;

			//Offset
			double distance = 12;
			if (UtilMath.offset(event.GetEvent().getEntity().getLocation(), cur.getLocation()) > distance)
				continue;

			//Strength
			int str = 0;
			if (_str.containsKey(cur))
				str = _str.get(cur) + 1;
			str = Math.min(str, 3);
			_str.put(cur, str);

			//Time
			double dur = 8; 
			_time.put(cur, (System.currentTimeMillis() + (long)(dur*1000)));

			//Condition
			Factory.Condition().Factory().Speed(GetName(), cur, event.GetEvent().getEntity(), dur, str, false, true, true);
			Factory.Condition().Factory().Strength(GetName(), cur, event.GetEvent().getEntity(), dur, str, false, true, true);

			//Inform
			UtilPlayer.message(cur, F.main(GetClassType().name(), "You entered " + F.skill(GetName(level)) + " at " + F.elem("Level " + (str+1)) + "."));

			//Effect
			cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 0.6f);
		}
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
			Expire(cur);
	}

	public boolean Expire(Player player)
	{
		if (!_time.containsKey(player))
			return false;

		if (System.currentTimeMillis() > _time.get(player))
		{
			int str = _str.remove(player);
			UtilPlayer.message(player, F.main(GetClassType().name(), "Your " + F.skill(GetName(GetLevel(player))) + 
					" has ended at " + F.elem("Level " + (str+1)) + "."));
			_time.remove(player);

			return true;
		}

		return false;
	}

	@Override
	public void Reset(Player player) 
	{
		_time.remove(player);
		_str.remove(player);
	}
}
