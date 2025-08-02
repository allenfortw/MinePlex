package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
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

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class Overcharge extends Skill
{
	private HashMap<Entity, Integer> _arrows = new HashMap<Entity, Integer>();
	private WeakHashMap<Player, Integer> _charge = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap<Player, Long>();

	public Overcharge(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Draw back harder on your bow, giving",
				"2 bonus damage per 0.8 seconds",
				"",
				"Maximum of 8 bonus damage"
				});
	}

	@EventHandler
	public void Interact(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (!UtilGear.isMat(event.getItem(), Material.BOW))
			return;

		if (!UtilEvent.isAction(event, ActionType.R))
			return;

		if (!player.getInventory().contains(Material.ARROW))
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
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			//Not Charging
			if (!_charge.containsKey(cur))
				continue;

			//Max Charge
			if (_charge.get(cur) >= 4)
				continue;

			//Charge Interval
			if (_charge.get(cur) == 0)
			{
				if (!UtilTime.elapsed(_chargeLast.get(cur), 1200))
					continue;
			}
			else
			{
				if (!UtilTime.elapsed(_chargeLast.get(cur), 800))
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
			if (!Factory.Energy().Use(cur, GetName(), 10, true, false))
				continue;

			//Increase Charge
			_charge.put(cur, _charge.get(cur) + 1);
			_chargeLast.put(cur, System.currentTimeMillis());

			//Inform
			UtilPlayer.message(cur, F.main(GetClassType().name(), "Overcharge: " + F.elem("+" + (_charge.get(cur) * 2) + " Damage")));

			//Effect
			for (int i=_charge.get(cur) ; i>0 ; i--)
				cur.playEffect(cur.getLocation(), Effect.CLICK2, 0);
		}
	}

	@EventHandler
	public void ShootBow(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getProjectile() instanceof Arrow))
			return;

		Player player = (Player)event.getEntity();

		if (!_charge.containsKey(player))
			return;

		//Add Velocity
		_arrows.put(event.getProjectile(), _charge.get(player));
		
		//Clear Charge
		_charge.remove(player);
		_chargeLast.remove(player);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void ArrowHit(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.PROJECTILE)
			return;

		Projectile projectile = event.GetProjectile();
		if (projectile == null)	return;

		if (!_arrows.containsKey(projectile))
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		//Level
		int level = GetLevel(damager);
		if (level == 0)			return;

		int damage = _arrows.remove(projectile) * 2;
		
		//Damage
		event.AddMod(damager.getName(), GetName(), damage, true);

		//Effect
		damagee.getWorld().playSound(damagee.getLocation(), Sound.HURT_FLESH, 1f, 0.5f);
	}
	
	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		HashSet<Entity> remove = new HashSet<Entity>();

		for (Entity cur : _arrows.keySet())
			if (cur.isDead() || !cur.isValid())
				remove.add(cur);

		for (Entity cur : remove)
			_arrows.remove(cur);
	}

	@Override
	public void Reset(Player player) 
	{
		_charge.remove(player);
		_chargeLast.remove(player);
	}
}
