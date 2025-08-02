package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Takedown extends SkillActive
{
	private HashMap<LivingEntity, Long> _live = new HashMap<LivingEntity, Long>();

	public Takedown(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Hurl yourself towards an opponent.",
				"If you collide with them, you " + C.cWhite + "both",
				"take 10 damage and receive Slow 4",
				"for 6 seconds."
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		if (UtilEnt.isGrounded(player))
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " while grounded."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		Vector vec = player.getLocation().getDirection();

		UtilAction.velocity(player, vec, 1.2, false, 0, 0.2, 0.4, false);

		//Record
		_live.put(player, System.currentTimeMillis());

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
	}

	@EventHandler
	public void End(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		//End
		for (Player player : GetUsers())
		{
			if (!UtilEnt.isGrounded(player))
				continue;

			if (!_live.containsKey(player))
				continue;

			int level = GetLevel(player);
			if (level == 0)		continue;

			if (!UtilTime.elapsed(_live.get(player), 1000))  
				continue;

			_live.remove(player);			
		}	

		//Collide
		for (Player player : GetUsers())
			if (_live.containsKey(player))
				for (Player other : player.getWorld().getPlayers())
					if (other.getGameMode() == GameMode.SURVIVAL)
						if (!other.equals(player))
							if (Factory.Relation().CanHurt(player, other))
								if (UtilMath.offset(player, other) < 2)
								{
									DoTakeDown(player, other);
									_live.remove(player);
									return;
								}

	}

	/**
	@EventHandler(priority = EventPriority.LOW)
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

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		if (UtilMath.offset(damager, damagee) > 3)
			return;

		if (_live.remove(damager) == null)
			return;

		event.SetCancelled("Takedown Cancel");

		DoTakeDown(damager, damagee);
	}
	**/

	public void DoTakeDown(Player damager, LivingEntity damagee)
	{
		int level = GetLevel(damager);
		int damage = 10;

		//Damage Event
		Factory.Damage().NewDamageEvent(damager, damagee, null, 
				DamageCause.CUSTOM, damage, false, true, false,
				damager.getName(), GetName() + " Recoil");	

		//Damage Event
		Factory.Damage().NewDamageEvent(damagee, damager, null, 
				DamageCause.CUSTOM, damage, false, true, false,
				damager.getName(), GetName());	

		//Conditions
		Factory.Condition().Factory().Slow(GetName(), damagee, damager, 6, 3, false, true, true, true);
		Factory.Condition().Factory().Slow(GetName(), damager, damager, 6, 3, false, true, true, true);

		//Inform
		UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
		UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
	}

	@Override
	public void Reset(Player player) 
	{
		_live.remove(player);
	}
}
