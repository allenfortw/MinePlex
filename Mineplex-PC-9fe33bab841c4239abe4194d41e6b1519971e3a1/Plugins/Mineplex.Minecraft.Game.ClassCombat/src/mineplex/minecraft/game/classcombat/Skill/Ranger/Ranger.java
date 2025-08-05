package mineplex.minecraft.game.classcombat.Skill.Ranger;

import org.bukkit.entity.Player;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Ranger extends Skill
{
	public Ranger(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				
				});
	}
	
	@Override
	public void Reset(Player player)
	{
		
	}
}
