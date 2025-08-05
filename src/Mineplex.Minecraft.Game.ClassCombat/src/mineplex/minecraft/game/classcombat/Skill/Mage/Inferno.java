package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Inferno extends SkillActive
{
	private HashSet<Player> _active = new HashSet<Player>();
	
	public Inferno(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Hold Block to use Inferno;",
				"You spray fire at high speed,",
				"igniting anything it hits."
				});
	}
	
	@Override
	public String GetEnergyString()
	{
		return "Energy: 24 per Second";
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
		_active.add(player);
	}

	@EventHandler
	public void Update(UpdateEvent event)  
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			if (!_active.contains(cur))
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
			
			//Energy
			if (!Factory.Energy().Use(cur, GetName(), 1.2, true, false))
			{
				_active.remove(cur);
				continue;
			} 

			//Fire
			Item fire = cur.getWorld().dropItem(cur.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.FIRE));
			Factory.Fire().Add(fire, cur, 0.7, 0, 0.5, 1, GetName());

			fire.teleport(cur.getEyeLocation());
			double x = 0.07 - (UtilMath.r(14)/100d);
			double y = 0.07 - (UtilMath.r(14)/100d);
			double z = 0.07 - (UtilMath.r(14)/100d);
			fire.setVelocity(cur.getLocation().getDirection().add(new Vector(x,y,z)).multiply(1.6));

			//Effect
			cur.getWorld().playSound(cur.getLocation(), Sound.GHAST_FIREBALL, 0.1f, 1f);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
