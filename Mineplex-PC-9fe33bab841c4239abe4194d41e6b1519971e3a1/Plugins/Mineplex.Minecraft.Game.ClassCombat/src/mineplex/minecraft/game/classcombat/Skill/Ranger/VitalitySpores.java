package mineplex.minecraft.game.classcombat.Skill.Ranger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class VitalitySpores extends Skill
{
	public VitalitySpores(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"After 10 seconds of not taking damage,",
				"forest spores surround you, giving",
				"you Regeneration 1 for 6 seconds.",
				"",
				"This remains until you take damage."
				});
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
		{ 
			int level = GetLevel(cur);
			if (level == 0)		continue;

			if (UtilTime.elapsed(Factory.Combat().Get(cur).LastDamaged, 10000))
			{
				Factory.Condition().Factory().Regen(GetName(), cur, cur, 6.9, 0, false, true, true);
				UtilPlayer.health(cur, 1);
			}		
		}
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
