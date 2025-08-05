package mineplex.minecraft.game.classcombat.Skill.Ranger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.UtilAction;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class HeavyArrows extends Skill
{
	public HeavyArrows(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Your arrows are extremely heavy,", 
				"moving 20% slower, also pushing",
				"you back upon firing them."
				});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void ShootBow(EntityShootBowEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player)event.getEntity();

		//Level
		int level = GetLevel(player);
		if (level == 0)				return;
 
		//Backboost
		double vel = (event.getProjectile().getVelocity().length() / 3);
		UtilAction.velocity(player, player.getLocation().getDirection().multiply(-1), vel, 
				false, 0, 0.2, 0.8, true);

		//Decrease Speed
		event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.8));
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
