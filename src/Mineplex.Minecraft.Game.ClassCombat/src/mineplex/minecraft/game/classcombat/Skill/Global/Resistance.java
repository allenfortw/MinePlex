package mineplex.minecraft.game.classcombat.Skill.Global;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;

public class Resistance extends Skill
{
	public Resistance(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your body and mind is exceptionally resistant.",
				"Durations on you are 50% shorter for;",
				"Fire, Shock, Confusion, Poison, Blindness."
				});
	}

	@EventHandler
	public void Resist(ConditionApplyEvent event)
	{
		if (event.GetCondition().GetType() != ConditionType.BURNING &&
			event.GetCondition().GetType() != ConditionType.SHOCK &&
			event.GetCondition().GetType() != ConditionType.CONFUSION &&
			event.GetCondition().GetType() != ConditionType.POISON &&
			event.GetCondition().GetType() != ConditionType.BLINDNESS)
			return;

		event.GetCondition().ModifyTicks((int) (event.GetCondition().GetTicksTotal() * 0.5));
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
