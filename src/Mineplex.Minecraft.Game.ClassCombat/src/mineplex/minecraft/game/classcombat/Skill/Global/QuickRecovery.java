package mineplex.minecraft.game.classcombat.Skill.Global;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;

public class QuickRecovery extends Skill
{
	public QuickRecovery(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"You are adept at recovering from Slow.",
				"Slow durations on you are 50% shorter",
				"and level is decreased by 1."
				});
	}
	
	@EventHandler
	public void Resist(ConditionApplyEvent event)
	{
		if (event.GetCondition().GetType() != ConditionType.SLOW)
			return;

		event.GetCondition().ModifyTicks((int) (event.GetCondition().GetTicksTotal() * -0.5));
		event.GetCondition().ModifyMult(-1);
	}
	
	@Override
	public void Reset(Player player) 
	{

	}
}
