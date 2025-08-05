package mineplex.minecraft.game.classcombat.Skill.Mage;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.core.common.util.UtilGear;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class MagmaBlade extends Skill
{
	public MagmaBlade(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your sword scorches opponents,",
				"dealing an additional 2 damage",
				"to players who are on fire."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;
		
		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		if (damagee.getFireTicks() <= 0)
			return;

		//Damager
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!UtilGear.isSword(damager.getItemInHand()))
			return;

		int level = GetLevel(damager);
		if (level == 0)			return;

		//Damage
		event.AddMod(damager.getName(), GetName(), 2, true);

		//Effect
		damager.getWorld().playSound(damager.getLocation(), Sound.FIZZ, 0.8f, 0f);
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
