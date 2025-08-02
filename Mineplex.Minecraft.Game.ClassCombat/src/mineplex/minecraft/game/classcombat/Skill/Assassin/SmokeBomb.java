package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SmokeBomb extends Skill
{
	public SmokeBomb(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Drop Axe/Sword to Use.",
				"",
				"Create a blast of smoke, turning",
				"you Invisible for 8 seconds. You ",
				"also receive Vulnerability 4 for",
				"6 seconds.",
				"",
				"You leave a trail of smoke while",
				"you are Invisible."
				});
	}

	@Override
	public String GetRechargeString()
	{
		return "Recharge: 1 Minute";
	}
	
	@EventHandler
	public void Use(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		int level = GetLevel(player);
		if (level == 0)		return;
		
		if (!UtilGear.isWeapon(event.getItemDrop().getItemStack()))
			return;

		event.setCancelled(true);

		if (!Recharge.Instance.use(player, GetName(), GetName(level), 120000 - (level * 30000), true))
			return;

		//Action
		Factory.Condition().Factory().Cloak(GetName(), player, player, 8, false, true);
		Factory.Condition().Factory().Vulnerable(GetName(), player, player, 6, 3, false, true, true);

		//Effect
		//Factory.Explosion().SetDamage(false);
		//player.getWorld().createExplosion(player.getLocation(), 5f, false);
		//Factory.Explosion().SetDamage(true);

		for (int i=0 ; i<3 ; i++)
		{
			player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 2f, 0.5f);
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 80);
		}
			
		
		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void EndDamagee(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		if (GetLevel(damagee) == 0)
			return;

		//End
		Factory.Condition().EndCondition(damagee, null, GetName());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void EndDamager(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		if (GetLevel(damager) == 0)
			return;

		//End
		Factory.Condition().EndCondition(damager, null, GetName());
	}
	
	@EventHandler
	public void EndInteract(PlayerInteractEvent event)
	{
		if (GetLevel(event.getPlayer()) == 0)
			return;

		Factory.Condition().EndCondition(event.getPlayer(), null, GetName());
	}

	@EventHandler
	public void Smoke(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
		{
			Condition cond = Factory.Condition().GetActiveCondition(cur, ConditionType.CLOAK);
			if (cond == null)		continue;

			if (!cond.GetReason().equals(GetName()))
				continue;

			//Smoke
			cur.getWorld().playEffect(cur.getLocation(), Effect.SMOKE, 4);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		//Remove Condition
		Factory.Condition().EndCondition(player, null, GetName());
	}
}
