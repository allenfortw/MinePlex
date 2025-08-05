package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.FormBase;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SpiderForm extends FormBase
{
	public SpiderForm(SkillFactory skills, String name, ClassType classType,
			SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels, 
				EntityType.SPIDER, new String[] 
						{
							"Venomous Spines",
							"Spin Web",
							"Pounce"
						});
		
		SetDesc(new String[] 
				{
				ChatColor.WHITE + "Passives:",
				"* Slow II",
				"",
				"",
				ChatColor.WHITE + "Attack: " + ChatColor.GREEN + "Pounce",
				"Pounce with 0.7 + 0.1pL Velocity",
				"",
				"",
				ChatColor.WHITE + "Sword Skill: " + ChatColor.GREEN + "Needler",
				"Spit out a flurry of needles;",
				"* Capacity of 3 + 1pL",
				"",
				"",
				ChatColor.WHITE + "Axe Skill: " + ChatColor.GREEN + "Spin Web",
				"Spin a temporary web;",
				"* Lasts 5 + 1pL seconds"
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
			Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9, 0, false, false, false);
		}
	}
}
