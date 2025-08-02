package mineplex.minecraft.game.classcombat.Skill.Ranger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class BarbedArrows extends Skill
{
	public BarbedArrows(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your arrows are barbed, and give",
				"opponents Slow 1 for 4 seconds.",
				"If opponent is sprinting, they",
				"receive Slow 3 instead.",
				"",
				"Slow scales with arrow velocity."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.PROJECTILE)
			return;

		Projectile projectile = event.GetProjectile();
		if (projectile == null)	return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		//Level
		int level = GetLevel(damager);
		if (level == 0)				return;

		Player damageePlayer = event.GetDamageePlayer();

		//Action
		int str = 0;
		if (damageePlayer != null)
			if (damageePlayer.isSprinting())
				str = 3;

		//Damage
		event.AddMod(damager.getName(), GetName(), 0, false);

		//Condition
		Factory.Condition().Factory().Slow(GetName(), damagee, damager, (projectile.getVelocity().length() / 3) * 4, str, false, true, true, true);
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
