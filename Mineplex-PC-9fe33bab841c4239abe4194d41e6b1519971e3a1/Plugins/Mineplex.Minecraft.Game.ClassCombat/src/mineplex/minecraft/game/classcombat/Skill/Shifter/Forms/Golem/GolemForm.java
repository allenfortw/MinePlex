package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.UtilGear;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class GolemForm extends FormBase
{
	public GolemForm(SkillFactory skills, String name, ClassType classType,
			SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels, 
				EntityType.IRON_GOLEM, new String[] 
						{
							"Magnetic Pull",
							"Magnetic Repel"
						});
		
		SetDesc(new String[] 
				{
				ChatColor.WHITE + "Passives:",
				"* Slow II",
				"* Protection III",
				"* -4 Damage Dealt",
				"",
				ChatColor.WHITE + "Sword Attack: " + ChatColor.GREEN + "Iron Crush",
				"* No Knockback",
				"* Slow V for 0.5 seconds",
				"",
				"",
				ChatColor.WHITE + "Axe Attack: " + ChatColor.GREEN + "Iron Smash",
				"* Strong Knockback",
				"",
				"",
				ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Magnetic Pull",
				"Pull in enemies infront of you;",
				"* Range of 4 + 2pL",
				"* Radius of 2 + 0.5pL",
				"",
				"",
				ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Magnetic Repel",
				"Repel all nearby enemies;",
				"* Range of 4 + 2pL",
				"* Velocity of 1.2 + 0.2pL"
				});
	}
	
	@Override
	public void UnapplyMorph(Player player) 
	{
		Factory.Condition().EndCondition(player, null, GetName());
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetMorphedUsers())
		{
			Factory.Condition().Factory().Protection(GetName(), cur, cur, 1.9, 2, false, false);
			Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9, 1, false, false, false);
		}	
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void KnockbackTaken(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		if (!IsMorphed(damagee))
			return;

		event.SetKnockback(false);
		damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_METAL, 0.8f, 1.8f);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void KnockbackGiven(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!IsMorphed(damager))
			return;

		event.AddMod(damager.getName(), GetName(), -4, false);

		if (UtilGear.isSword(damager.getItemInHand()))		
		{
			damager.getWorld().playSound(damager.getLocation(), Sound.IRONGOLEM_HIT, 1f, 1.6f);
			Factory.Condition().Factory().Slow(GetName(), event.GetDamageeEntity(), damager, 0.5, 4, false, false, true);
			event.SetKnockback(false);
		}
		else if (UtilGear.isAxe(damager.getItemInHand()))	
		{
			damager.getWorld().playSound(damager.getLocation(), Sound.IRONGOLEM_HIT, 1f, 0.8f);
			event.AddKnockback(GetName(), 4d);
		}
	}
}
