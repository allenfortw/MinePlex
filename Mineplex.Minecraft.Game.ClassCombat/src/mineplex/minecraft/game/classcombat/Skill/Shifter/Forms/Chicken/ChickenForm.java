package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ChickenForm extends FormBase
{
	public ChickenForm(SkillFactory skills, String name, ClassType classType,
			SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels, 
				EntityType.CHICKEN, new String[] 
						{
							"Flap"
						});

		SetDesc(new String[] 
				{
				ChatColor.WHITE + "Passives:",
				"* Slow I",
				"* +10 Damage taken from Arrows",
				"",
				"",
				ChatColor.WHITE + "Attack: " + ChatColor.GREEN + "None",
				"",
				"",
				ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Fly",
				"Push Block to Flap;",
				"* Velocity of 0.5 + 0.05pL",
				"",
				"Hold Block to Glide;",
				"* Min speed of 0.3 + 0.03pL",
				"* Max speed of 0.7 + 0.07pL",
				"",
				"Glide through enemies to pick them up.",
				"Stop gliding to drop them.",
				"",
				"",
				ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Spin Web",
				"Spin a temporary web;",
				"* Lasts 5 + 1pL seconds",
				});
	}

	@Override
	public void UnapplyMorph(Player player) 
	{
		Flap flap = (Flap)Factory.GetSkill("Flap");
		flap.GetGrab().Release(player);

		Factory.Condition().EndCondition(player, null, GetName());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void Attack(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!IsMorphed(damager))
			return;

		event.SetCancelled(GetName());
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetMorphedUsers())
			Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9, 0, false, false, false);
	}
}
