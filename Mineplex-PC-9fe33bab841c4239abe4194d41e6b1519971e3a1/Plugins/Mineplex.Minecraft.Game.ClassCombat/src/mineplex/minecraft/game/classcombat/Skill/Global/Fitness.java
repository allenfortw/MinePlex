package mineplex.minecraft.game.classcombat.Skill.Global;

import org.bukkit.entity.Player;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Fitness extends Skill
{
	public Fitness(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"You are extremely fit;",
				"Maximum Energy is increased by 60 (33%)."
				});
	}
	
	@Override
	public void OnPlayerAdd(Player player)
	{
		Factory.Energy().AddEnergyMaxMod(player, GetName(), 60);
	}
	
	@Override
	public void Reset(Player player) 
	{
		Factory.Energy().RemoveEnergyMaxMod(player, GetName());
	}
}
