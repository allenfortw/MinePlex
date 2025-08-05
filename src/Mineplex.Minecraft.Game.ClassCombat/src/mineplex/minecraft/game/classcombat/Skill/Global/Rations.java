package mineplex.minecraft.game.classcombat.Skill.Global;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Rations extends Skill
{
	public Rations(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"After not moving for 3 seconds,",
				"you snack on rations, slowly",
				"replenishing your hunger."
				});
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			int level = GetLevel(cur);
			
			if (level > 0)
			{
				if (UtilTime.elapsed(Factory.Movement().Get(cur).LastMovement, 3000))
				{
					cur.setSaturation(0f);
					cur.setExhaustion(0f);
					UtilPlayer.hunger(cur, 1);	
				}
			}
		}
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
