package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Barrage extends Skill
{
	private WeakHashMap<Player, Integer> _charge = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap<Player, Long>();

	private HashSet<Player> _firing = new HashSet<Player>();
	private HashSet<Projectile> _arrows = new HashSet<Projectile>();

	public Barrage(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Load an extra arrow into your bow",
				"while charging, every 0.2 seconds.",
				"",
				"Maximum of 20 additional arrows."
				});
	}
	
	@Override
	public String GetEnergyString()
	{
		return "Energy: 3 per Arrow";
	}

	@EventHandler
	public void DrawBow(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (!UtilGear.isMat(event.getItem(), Material.BOW))
			return;

		if (!UtilEvent.isAction(event, ActionType.R))
			return;

		if (!player.getInventory().contains(Material.ARROW))
			return;
		
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
			return;
		
		if (event.getClickedBlock() != null)
		{
			if (UtilBlock.usable(event.getClickedBlock()))
				return;
		}

		//Level
		int level = GetLevel(player);
		if (level == 0)			return;

		//Start Charge
		_charge.put(player, 0);
		_chargeLast.put(player, System.currentTimeMillis());
		_firing.remove(player);
	}

	@EventHandler
	public void Charge(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			//Not Charging
			if (!_charge.containsKey(cur))
				continue;
			
			if (_firing.contains(cur))
				continue;

			int level = GetLevel(cur);
			if (level == 0)	continue;

			//Max Charge
			if (_charge.get(cur) >= 20)
				continue;

			//Charge Interval
			if (_charge.get(cur) == 0)
			{
				if (!UtilTime.elapsed(_chargeLast.get(cur), 1200))
					continue;
			}
			else
			{
				if (!UtilTime.elapsed(_chargeLast.get(cur), 100))
					continue;
			}

			//No Longer Holding Bow
			if (!UtilGear.isMat(cur.getItemInHand(), Material.BOW))
			{
				_charge.remove(cur);
				_chargeLast.remove(cur);
				continue;
			}

			//Energy
			if (!Factory.Energy().Use(cur, GetName(), 3, true, false))
				continue;

			//Increase Charge
			_charge.put(cur, _charge.get(cur) + 1);
			_chargeLast.put(cur, System.currentTimeMillis());

			//Inform
			if (_charge.get(cur) % 5 == 0)
				UtilPlayer.message(cur, F.main(GetClassType().name(), "Barrage: " + F.elem("+" + _charge.get(cur) + " Arrows")));

			//Effect
			cur.playSound(cur.getLocation(), Sound.CLICK, 0.4f, 1f + (0.05f * _charge.get(cur)));
		}
	}

	@EventHandler
	public void FireBow(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getProjectile() instanceof Arrow))
			return;

		Player player = (Player)event.getEntity();

		if (!_charge.containsKey(player))
			return;

		//Start Barrage
		_firing.add(player);
		_chargeLast.put(player, System.currentTimeMillis());
	}

	@EventHandler
	public void Skill(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		HashSet<Player> remove = new HashSet<Player>();

		for (Player cur : _firing)
		{
			if (!_charge.containsKey(cur) || !_chargeLast.containsKey(cur))
			{
				remove.add(cur);
				continue;
			}

			if (!UtilGear.isBow(cur.getItemInHand()))
			{
				remove.add(cur);
				continue;
			}

			int arrows = _charge.get(cur);
			if (arrows <= 0)
			{
				remove.add(cur);
				continue;
			}

			//if (!UtilTime.elapsed(_chargeLast.get(cur), 100))
			//	continue;

			//_chargeLast.put(cur, System.currentTimeMillis());
			_charge.put(cur, arrows-1);

			//Fire Arrow
			Vector random = new Vector((Math.random()-0.5)/10, (Math.random()-0.5)/10, (Math.random()-0.5)/10);
			Projectile arrow = cur.launchProjectile(Arrow.class);
			arrow.setVelocity(cur.getLocation().getDirection().add(random).multiply(3));
			_arrows.add(arrow);
			cur.getWorld().playSound(cur.getLocation(), Sound.SHOOT_ARROW, 1f, 1f);
		}

		for (Player cur : remove)
		{
			_charge.remove(cur);
			_chargeLast.remove(cur);
			_firing.remove(cur);
		}
	}

	@EventHandler
	public void ProjectileHit(ProjectileHitEvent event)
	{
		if (_arrows.remove(event.getEntity()))
			event.getEntity().remove();
	}

	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		for (Iterator<Projectile> arrowIterator = _arrows.iterator(); arrowIterator.hasNext();) 
		{
			Projectile arrow = arrowIterator.next();
			
			if (arrow.isDead() || !arrow.isValid())
				arrowIterator.remove();
		}
	}
	
	@Override
	public void Reset(Player player) 
	{
		_charge.remove(player);
		_chargeLast.remove(player);
		_firing.remove(player);
	}
}
