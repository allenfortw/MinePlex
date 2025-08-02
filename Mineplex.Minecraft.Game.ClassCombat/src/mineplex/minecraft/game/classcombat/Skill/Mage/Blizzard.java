package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Blizzard extends SkillActive
{
	private HashSet<Player> _active = new HashSet<Player>();
	private WeakHashMap<Projectile, Player> _snowball = new WeakHashMap<Projectile, Player>();

	public Blizzard(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Hold Block to release a Blizzard.",
				"Target the ground to create snow,",
				"or target air to create a torrent",
				"of snowballs, to push players away."
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
	public void Snow(UpdateEvent event) 
	{
		if (event.getType() != UpdateType.FASTEST)
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
			if (!Factory.Energy().Use(cur, GetName(), 4, true, true))
			{
				_active.remove(cur);
				continue;
			}

			//Target
			HashSet<Byte> ignore = new HashSet<Byte>();
			ignore.add((byte) 0);
			ignore.add((byte) 78);
			ignore.add((byte) 80);

			Block target = cur.getTargetBlock(ignore, 7);

			//Snow
			if (target == null || target.getType() == Material.AIR || UtilMath.offset(target.getLocation(), cur.getLocation()) > 5)
				for (int i=0 ; i<4 ; i++)
				{
					Projectile snow = cur.launchProjectile(Snowball.class);
					double x = 0.2 - (UtilMath.r(40)/100d);
					double y = UtilMath.r(20)/100d;
					double z = 0.2 - (UtilMath.r(40)/100d);
					snow.setVelocity(cur.getLocation().getDirection().add(new Vector(x,y,z)).multiply(2));
					_snowball.put(snow, cur);
				}

			if (target == null || target.getType() == Material.AIR)				
				continue;

			if (UtilMath.offset(target.getLocation(), cur.getLocation()) > 7)
				continue;

			HashMap<Block, Double> blocks = UtilBlock.getInRadius(target.getLocation(), 2d, 1);
			for (Block block : blocks.keySet())
			{
				Factory.BlockRestore().Snow(block, (byte)(1 + (int)(2*blocks.get(block))), (byte)7, 2500, 250, 3);
			}

			//Effect
			target.getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, 80);
			cur.getWorld().playSound(cur.getLocation(), Sound.STEP_SNOW, 0.1f, 0.5f);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void Snowball(CustomDamageEvent event)
	{		
		if (event.GetCause() != DamageCause.PROJECTILE)
			return;

		Projectile proj = event.GetProjectile();
		if (proj == null)		return;

		if (!(proj instanceof Snowball))
			return;
		
		if (!_snowball.containsKey(proj))
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		event.SetCancelled(GetName());
		damagee.setVelocity(proj.getVelocity().multiply(0.1).add(new Vector(0, 0.15, 0)));
	}

	@EventHandler
	public void SnowballForm(ProjectileHitEvent event)
	{
		if (!(event.getEntity() instanceof Snowball))
			return;
		
		if (_snowball.remove(event.getEntity()) == null)
			return;

		Factory.BlockRestore().Snow(event.getEntity().getLocation().getBlock(), (byte)1, (byte)7, 2000, 250, 0);
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
