package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;

public class Stealth extends Skill
{
	private HashSet<Player> _active = new HashSet<Player>();

	public Stealth(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Drop Axe/Sword to Toggle",
				"",
				"Move stealthily, becoming completely",
				"Invisible. However, you also receive",
				"Slow 3.",
				"",
				"Stealth ends if you an enemy comes",
				"within 3 Blocks of you, or you attack.",
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 16 per Second";
	}

	@EventHandler
	public void Crouch(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		if (GetLevel(player) == 0)
			return;

		if (!UtilGear.isWeapon(event.getItemDrop().getItemStack()))
			return;

		event.setCancelled(true);

		//Check Allowed
		SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
		UtilServer.getServer().getPluginManager().callEvent(trigger);
		if (trigger.IsCancelled())
			return;

		if (!_active.remove(player))
		{
			if (player.hasPotionEffect(PotionEffectType.SLOW))
			{
				UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while Slowed."));
				return;
			}

			if (!UtilTime.elapsed(Factory.Combat().Get(player).LastCombat, 4000))
			{
				UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while in Combat."));
				return;
			}

			Add(player);
		}
		else
		{
			Remove(player, player);
		}
	}

	public void Add(Player player)
	{
		_active.add(player);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "Stealth: " + F.oo("Enabled", true)));

		//Conditions
		Factory.Condition().Factory().Cloak(GetName(), player, player, 120000, false, true);
		Factory.Condition().Factory().Slow(GetName(), player, player, 120000, 2, false, false, false, true);
		Factory.Condition().Factory().Vulnerable(GetName(), player, player, 120000, 3, false, true, true);

		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.BREATH, 0.5f, 0.5f);
	}

	public void Remove(Player player, LivingEntity source)
	{
		_active.remove(player);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "Stealth: " + F.oo("Disabled", false)));

		//Conditions
		Factory.Condition().EndCondition(player, null, GetName());
	}

	@EventHandler
	public void EndProx(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
		{
			int level = GetLevel(cur);
			if (level == 0)		continue;

			//Proximity Decloak
			if (_active.contains(cur)) 	
				for (Player other : cur.getWorld().getPlayers())
				{
					if (other.equals(cur))
						continue;

					if (UtilMath.offset(cur, other) > 3)
						continue;

					if (!Factory.Relation().CanHurt(cur, other))
						continue;

					Remove(cur, other);
					break;
				}
		}
	}

	@EventHandler 
	public void EndInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (!_active.contains(player))
			return;

		Remove(player, player);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void EndDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee != null)	return;
		{
			if (_active.contains(damagee))
			{
				Remove(damagee, event.GetDamagerEntity(true));
			}
		}

		Player damager = event.GetDamagerPlayer(true);
		if (damager != null)	return;
		{
			if (_active.contains(damager))
			{
				Remove(damager, event.GetDamagerEntity(true));
			}
		}
	}

	@EventHandler
	public void Energy(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			if (!_active.contains(cur))
				continue;

			//Level
			if (GetLevel(cur) == 0)
			{
				Remove(cur, null);	
				continue;
			}

			//Silence
			if (Factory.Condition().IsSilenced(cur, null))
			{
				Remove(cur, null);
				continue;
			}

			if (!Factory.Energy().Use(cur, GetName(), 0.75, true, false))
			{
				Remove(cur, null);
				continue;
			}
		}	
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
		Factory.Condition().EndCondition(player, ConditionType.CLOAK, GetName());
	}
}
