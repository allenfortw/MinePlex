package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Wolf;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class WolfForm extends FormBase
{
	public WolfForm(SkillFactory skills, String name, ClassType classType,
			SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels, 
				EntityType.WOLF, new String[] 
						{
				//skills.GetSkill("Magnetic Pull"),
				//skills.GetSkill("Magnetic Repel")
						});
		
		SetDesc(new String[] 
				{
				"Dire Wolf Form (Harass / Ganking);",
				"* Speed I",
				"* Protection I",
				"* Regeneration I",
				"",
				"",
				"Axe Skill: Howl",
				"Nearby allies receive;",
				"* Speed III for 3 + 1pL seconds",
				"",
				"",
				"Sword Skill: Bite",
				"Hold Block to bite target;",
				"* Slow IV for 2 seconds",	
				"* You are pulled along with target"
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
			Factory.Condition().Factory().Protection(GetName(), cur, cur, 1.9, 0, false, false);
			Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9, 0, false, false);
			Factory.Condition().Factory().Regen(GetName(), cur, cur, 1.9, 0, false, false);
		}	
	}
}
