package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Disengage extends SkillActive
{
	private HashMap<Player, Long> _prepare = new HashMap<Player, Long>();

	public Disengage(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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

		SetDesc(new String[] 
				{
				"Push Block, then block an attack",
				"within 1 second to disengage.",
				"",
				"If successful, you leap backwards",
				"and your attacker receives Slow 4",
				"for 6 seconds.",
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Prepare
		_prepare.put(player, System.currentTimeMillis() + 1000);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared to " + F.skill(GetName()) + "."));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		//Blocking
		if (!damagee.isBlocking())
			return;

		if (!_prepare.containsKey(damagee))
			return;

		//Damager
		LivingEntity damager = event.GetDamagerEntity(false);
		if (damager == null)	return;

		//Level
		int level = GetLevel(damagee);
		if (level == 0)			return;

		//Cancel
		event.SetCancelled(GetName());

		//Remove
		_prepare.remove(damagee);

		//Velocity
		if (UtilEnt.isGrounded(damagee))
			UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 3, true, 0, 0.8, 1, true);
		else
			UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 1.5, true, 0, 0.8, 1, true);

		//Condition
		Factory.Condition().Factory().Slow(GetName(), damager, damagee, 6, 3, false, true, true, true);

		//Effect
		damagee.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_METAL, 0.5f, 1.6f);

		//Inform
		UtilPlayer.message(damagee, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
		UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(), F.name(damagee.getName()) + " used " + F.skill(GetName(level)) + "."));

	}

	@EventHandler
	public void Expire(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			if (!_prepare.containsKey(cur))
				continue;

			if (System.currentTimeMillis() > _prepare.get(cur))
			{
				//Remove
				_prepare.remove(cur);

				//Inform
				UtilPlayer.message(cur, F.main(GetClassType().name(), "You failed to " + F.skill(GetName()) + "."));
			}
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_prepare.remove(player);
	}
}
