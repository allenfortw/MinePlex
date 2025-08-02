package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Pounce extends SkillActive 
{
	public Pounce(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
		return true;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void EndDamager(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		int level = GetLevel(damager);
		if (level == 0)			return;

		event.SetCancelled(GetName());
		
		Skill(damager, level);
	}

	@Override
	public void Skill(Player player, int level) 
	{
		if (player.getLocation().getBlock().isLiquid())
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in liquids."));
			return;
		}
		
		if (UtilTime.elapsed(Factory.Movement().Get(player).LastGrounded, 1000))
		{
			UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while airborne."));
		}
		
		//Action
		UtilAction.velocity(player, 0.7 + (0.1 * level), 0.2, 0.8, true);
		
		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.SPIDER_DEATH, 0.5f, 2f);
	}

	@Override
	public void Reset(Player player) 
	{

	}
}
