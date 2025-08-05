package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.WeakHashMap;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class Shadowmeld extends Skill
{
	private WeakHashMap<Player, Long> _crouchTime = new WeakHashMap<Player, Long>();

	private HashSet<Player> _active = new HashSet<Player>();

	public Shadowmeld(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Crouch for 2 seconds to meld into",
				"the shadows, turning invisible,",
				"and receive Vulnerability II.",
				"",
				"Shadowmeld ends if you stop crouching,",
				"interact or another player comes within",
				"4 Blocks of you."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 8 per Second";
	}

	@EventHandler
	public void Skill(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
		{
			int level = GetLevel(cur);
			if (level == 0)		continue;

			//Proximity Decloak
			if (_active.contains(cur) || _crouchTime.containsKey(cur)) 	
				for (Player other : cur.getWorld().getEntitiesByClass(Player.class))
				{
					if (other.equals(cur))
						continue;

					if (UtilMath.offset(cur, other) > 4)
						continue;

					End(cur);
					break;
				}

			//Active - Check for Disable
			if (_active.contains(cur))
			{
				if (!cur.isSneaking())
					End(cur);
				else
					Factory.Condition().Factory().Cloak(GetName(), cur, cur, 1.9, false, true);
			}
			else if (cur.isSneaking())
			{
				//Start Timer
				if (!_crouchTime.containsKey(cur))
				{
					_crouchTime.put(cur, System.currentTimeMillis());
				}
				//Check Timer
				else if (UtilTime.elapsed(_crouchTime.get(cur), 2000))
				{
					_crouchTime.remove(cur);
					_active.add(cur);
					Factory.Condition().Factory().Cloak(GetName(), cur, cur, 1.9, false, true);
				}
			}
		}
	}

	@EventHandler
	public void Energy(UpdateEvent event)
	{
		if (event.getType() == UpdateType.TICK)
			for (Player cur : _active)
				if (!Factory.Energy().Use(cur, GetName(), -0.4, true, true))
					End(cur);	
		
		if (event.getType() == UpdateType.FAST)
			for (Player cur : _active)
				Factory.Condition().Factory().Vulnerable(GetName(), cur, cur, 2.9, 1, false, true, true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void EndDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		End(damagee);
	}

	@EventHandler
	public void EndInteract(PlayerInteractEvent event)
	{
		End(event.getPlayer());
	}

	public void End(Player player)
	{
		if (_active.remove(player))
			Factory.Condition().EndCondition(player, ConditionType.CLOAK, GetName());

		_crouchTime.remove(player);
	}

	@Override
	public void Reset(Player player) 
	{
		End(player);
	}
}
