package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Propel extends SkillActive
{
	private HashMap<Player, Long> _active = new HashMap<Player, Long>();

	public Propel(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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

		SetDesc(new String[] 
				{
				""
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (!player.getLocation().getBlock().isLiquid())
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " out of water."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Velocity
		UtilAction.velocity(player, 0.6 + (0.2 * level), 0.2, 2, false);
		
		//Store
		_active.put(player, System.currentTimeMillis());

		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.SPLASH2, 1.5f, 1.5f);
	}
	
	@EventHandler
	public void Reuse(UpdateEvent event) 
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			if (!_active.containsKey(cur))
				continue;

			if (!cur.isBlocking())
			{
				_active.remove(cur);
				continue;
			}

			//Level
			int level = GetLevel(cur);
			if (level == 0)			
			{
				_active.remove(cur);
				continue;
			}

			//Time
			if (!UtilTime.elapsed(_active.get(cur), 400))
				continue;
			
			if (!cur.getLocation().getBlock().isLiquid())
				continue;

			//Thrust
			UtilAction.velocity(cur, 0.3 + (0.1 * level), 0.1, 2, false);
			
			//Store
			_active.put(cur, System.currentTimeMillis());
			
			//Sound
			cur.getWorld().playSound(cur.getLocation(), Sound.SPLASH2, 0.5f, 1f);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
