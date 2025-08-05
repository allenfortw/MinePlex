package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

public class Stampede extends Skill
{
	private WeakHashMap<Player, Long> _sprintTime = new WeakHashMap<Player, Long>();
	private WeakHashMap<Player, Integer> _sprintStr = new WeakHashMap<Player, Integer>();

	public Stampede(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"You slowly build up speed as you",
				"sprint. You gain a level of Speed",
				"for every 3 seconds, up to a max",
				"of Speed 3.",
				"",
				"Attacking during stampede deals",
				"2 bonus damage per speed level,",
				"as well as large knockback."
				});
	}

	@EventHandler
	public void Skill(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTER)
			return;

		for (Player cur : GetUsers())
		{
			int level = GetLevel(cur);
			if (level == 0)		continue;

			//Active - Check for Disable
			if (_sprintTime.containsKey(cur))
			{
				//Stopped
				if (!cur.isSprinting())
				{
					_sprintTime.remove(cur);
					_sprintStr.remove(cur);
					cur.removePotionEffect(PotionEffectType.SPEED);
					continue;
				}

				long time = _sprintTime.get(cur);
				int str = _sprintStr.get(cur);

				//Apply Speed
				if (str > 0)
					Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9, str-1, false, true, true);

				//Upgrade Speed
				if (!UtilTime.elapsed(time, 3000))
					continue;

				_sprintTime.put(cur, System.currentTimeMillis());

				if (str < 3)
				{	
					_sprintStr.put(cur, str+1);

					//Effect
					cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_IDLE, 2f, 0.2f * str+1);
				}
				
				//Event
				UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(cur, GetName(), ClassType.Brute));
			}
			else if (cur.isSprinting())
			{
				//Start Timer
				if (!_sprintTime.containsKey(cur))
				{
					_sprintTime.put(cur, System.currentTimeMillis());
					_sprintStr.put(cur, 0);
				}
			}
		}
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

		if (!_sprintStr.containsKey(damager))
			return;

		if (_sprintStr.get(damager) == 0)
			return;

		int level = GetLevel(damager);
		if (level == 0)			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		//Remove
		_sprintTime.remove(damager);
		int str = _sprintStr.remove(damager);
		damager.removePotionEffect(PotionEffectType.SPEED);

		//Damage
		event.AddMod(damager.getName(), GetName(), str * 2, true);
		event.SetKnockback(false);

		//Velocity
		if (UtilEnt.isGrounded(damagee))
			UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 3, true, 0, 0.8, 1, true);
		else
			UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 1.5, true, 0, 0.8, 1, true);

		//Inform
		UtilPlayer.message(damager, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
		UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));

		//Effect
		damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_WOOD, 1f, 0.4f * str); 
		
		//Event
		UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(damager, GetName(), ClassType.Brute, damagee));
	}

	@Override
	public void Reset(Player player) 
	{
		_sprintTime.remove(player);
		_sprintStr.remove(player);
	}
}
