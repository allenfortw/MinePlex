package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class MagneticRepel extends SkillActive
{
	public MagneticRepel(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy, int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType,
				cost, levels,
				energy, energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Repel
		for (Entity other : player.getWorld().getEntities())
		{					
			if (!(other instanceof LivingEntity))
				continue;
			
			if (player.equals(other))
				continue;
			
			double offset = UtilMath.offset(player, other);
			double maxOffset = 6 + (level * 2);
			
			if (offset > maxOffset)		
				continue;
			
			if (other instanceof Player)
			{
				if (!Factory.Relation().CanHurt(player, (Player)other))		
					continue;
			}
			
			double power = 0.5 + (0.5 *((maxOffset - offset) / maxOffset));
			
			Vector vel = UtilAlg.getTrajectory(player, other);
			vel.setY(Math.min(0.3, vel.getY()));
			vel.normalize();
			
			UtilAction.velocity(other, vel,
					power * (2 + (level * 0.5)), false, 0, 0.8, 0.8, true);
		}

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
		
		//Sound
		for (int i=0 ; i<3 ; i++)
			player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 2f, 0.6f);
		
		player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 2f, 2f);
	}

	@Override
	public void Reset(Player player) 
	{
		
	}
}
