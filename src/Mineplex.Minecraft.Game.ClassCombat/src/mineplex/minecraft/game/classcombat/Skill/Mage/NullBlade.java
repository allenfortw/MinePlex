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

public class NullBlade extends Skill
{
	public NullBlade(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your sword sucks 6 energy from",
				"opponents with every attack."
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Drain(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		//Damager
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!UtilGear.isSword(damager.getItemInHand()))
			return;

		int level = GetLevel(damager);
		if (level == 0)			return;

		//Damagee
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		//Energy
		Factory.Energy().ModifyEnergy(damagee, -6);
		Factory.Energy().ModifyEnergy(damager, 6);

		//Damage
		event.AddMod(damager.getName(), GetName(), 0, true);

		//Effect
		damager.getWorld().playSound(damager.getLocation(), Sound.BREATH, 0.6f, 0.6f);
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
