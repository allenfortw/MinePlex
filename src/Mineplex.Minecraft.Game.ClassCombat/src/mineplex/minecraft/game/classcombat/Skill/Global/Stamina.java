package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.entity.Player;

public class Stamina extends Skill
{
	public Stamina(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"You have exceptional Stamina;",
				"Swinging weapons uses 75% less Energy."
				});
	}

	@Override
	public void OnPlayerAdd(Player player)
	{
		Factory.Energy().AddEnergySwingMod(player, GetName(), -3);
	}
	
	@Override
	public void Reset(Player player) 
	{
		Factory.Energy().RemoveEnergySwingMod(player, GetName());
	}
}
