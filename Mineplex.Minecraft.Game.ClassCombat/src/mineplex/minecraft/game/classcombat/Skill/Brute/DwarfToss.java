package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;

public class DwarfToss extends SkillActive
{
	private long _chargeTime = 4000;
	private HashSet<Player>	_used = new HashSet<Player>();
	private NautHashMap<Player, LivingEntity> _holding = new NautHashMap<Player, LivingEntity>();
	private NautHashMap<Player, Long> _charge = new NautHashMap<Player, Long>();
	private HashSet<Player> _charged = new HashSet<Player>();

	public DwarfToss(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Hold Block to pick up target player.",
				"Release Block to throw them.",
				"",
				"You must hold the player for",
				"4 seconds for full throw power.",
				"",
				"Players you are holding cannot harm",
				"you, or be harmed by others."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 20 + 12 per Second";
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (_used.contains(player))
			return false;

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		UtilPlayer.message(player, F.main(GetClassType().name(), "You failed " + F.skill(GetName()) + "."));
	}

	@EventHandler
	public void Miss(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		_used.clear();
	}

	public boolean CanUse(Player player)
	{
		int level = GetLevel(player);
		if (level == 0)		return false;

		//Check Material
		if (player.getItemInHand() != null)
			if (!_itemSet.contains(player.getItemInHand().getType()))
				return false;

		//Check Allowed
		SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
		UtilServer.getServer().getPluginManager().callEvent(trigger);
		if (trigger.IsCancelled())
			return false;

		//Check Energy/Recharge
		if (!EnergyRechargeCheck(player, level))
			return false;

		//Allow
		return true;
	}
	
	@EventHandler
	public void PreventDismount(VehicleExitEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!(event.getExited() instanceof Player))
			return;
		
		if (!(event.getVehicle() instanceof Player))
			return;
		
		if (_holding.containsKey((Player)event.getVehicle()) && _holding.get((Player)event.getVehicle()) == event.getExited())
			event.setCancelled(true);
	}

	@EventHandler
	public void Grab(PlayerInteractEntityEvent event)
	{
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		//Level
		int level = GetLevel(player);
		if (level == 0)			return;

		//Set Used
		_used.add(player);

		if (!CanUse(player))
			return;

		if (!(event.getRightClicked() instanceof LivingEntity))
			return;

		LivingEntity target = (LivingEntity)event.getRightClicked();

		if (target instanceof Player)
		{
			if (((Player)target).getGameMode() != GameMode.SURVIVAL)
			{
				UtilPlayer.message(player, F.main(GetClassType().name(), F.name(((Player)target).getName()) + " is not attackable."));
				return;
			}
		}

		//Distance
		if (UtilMath.offset(player.getLocation(), target.getLocation()) > 3)
		{
			UtilPlayer.message(player, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " is too far away."));
			return;
		}	

		//Hold Loop
		if (target instanceof Player && _holding.containsKey((Player)target))
			if (_holding.get((Player)target).equals(player))
				if (target instanceof Player)
				{
					UtilPlayer.message(player, F.main(GetClassType().name(), F.name(((Player)target).getName()) + " is already holding you."));
					return;
				}

		if (_holding.containsValue(target))
		{
			UtilPlayer.message(player, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " is already being held."));
			return;
		}

		//Obstruct Check
		if (target instanceof Player)
			for (int i = 0 ; i < 10 ; i++)			
			{
				Block block = player.getWorld().getBlockAt(player.getEyeLocation()
						.add((target.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector())).multiply(i/10d)));

				if (!UtilBlock.airFoliage(block))
				{
					UtilPlayer.message(player, F.main(GetClassType().name(), F.name(((Player)target).getName()) + " is obstructed by blocks."));
					return;
				}
			}

		//Condition Indicators
		Factory.Condition().SetIndicatorVisibility(player, false);

		//Action
		target.leaveVehicle();
		player.eject();
		player.setPassenger(target);
		_holding.put(player, target);
		_charge.put(player, System.currentTimeMillis());

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You picked up " + F.name(UtilEnt.getName(target)) + " with " + F.skill(GetName(level)) + "."));
		UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " grabbed you with " + F.skill(GetName(level)) + "."));

		//Event
		UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(player, GetName(), ClassType.Brute, target));

		//Effect
		target.playEffect(EntityEffect.HURT);
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageePassenger(CustomDamageEvent event) 
	{
		if (event.IsCancelled())
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		if (!_holding.containsValue(damagee))
			return;

		event.SetCancelled(GetName());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void DamagerPassenger(CustomDamageEvent event) 
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		LivingEntity damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		if (!_holding.containsKey(damagee))
			return;

		if (!_holding.get(damagee).equals(damager))
			return;

		//Inform
		UtilPlayer.message(damager, F.main(GetClassType().name(), "You cannot attack " + F.name(damagee.getName()) + "."));

		event.SetCancelled(GetName());
	}

	@EventHandler
	public void ThrowExpire(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		HashSet<Player> voidSet = new HashSet<Player>();
		HashSet<Player> throwSet = new HashSet<Player>();

		for (Player cur : _holding.keySet())
		{
			if (cur.getPassenger() == null)
			{
				voidSet.add(cur);
				continue;	
			}

			if (_holding.get(cur).getVehicle() == null)
			{
				voidSet.add(cur);
				continue;	
			}

			if (!_holding.get(cur).getVehicle().equals(cur))
			{
				voidSet.add(cur);
				continue;	
			}

			//Charged Tick
			if (!_charged.contains(cur))
				if (System.currentTimeMillis() - _charge.get(cur) > _chargeTime)
				{
					_charged.add(cur);
					cur.playEffect(cur.getLocation(), Effect.CLICK1, 0);
				}

			//Use Energy
			if (cur.isBlocking() && !UtilTime.elapsed(_charge.get(cur), 10000))
				Factory.Energy().ModifyEnergy(cur, -0.6);

			//Flag to Throw
			else
				throwSet.add(cur);
		}

		for (Player cur : voidSet)
		{
			LivingEntity target = _holding.remove(cur);
			_charge.remove(cur);
			_charged.remove(cur);
			int level = GetLevel(cur);

			UtilPlayer.message(cur, F.main(GetClassType().name(), F.name(UtilEnt.getName(target)) + " escaped your " + F.skill(GetName(level)) + "."));
		}

		for (Player cur : throwSet)
		{
			LivingEntity target = _holding.remove(cur);			
			long charge = _charge.remove(cur);
			int level = GetLevel(cur);
			_charged.remove(cur);

			//Throw
			cur.eject();
			double mult = 1.5;
			if (charge < _chargeTime)
				mult = mult * (charge/_chargeTime);
			UtilAction.velocity(target, cur.getLocation().getDirection(), mult, false, 0, 0.2, 1.2, true);

			//Condition Indicators
			Factory.Condition().SetIndicatorVisibility(cur, true);

			//Condition
			Factory.Condition().Factory().Falling(GetName(), target, cur, 10, false, true);

			//Inform
			UtilPlayer.message(cur, F.main(GetClassType().name(), "You threw " + F.name(UtilEnt.getName(target)) + " with " + F.skill(GetName(level)) + "."));
			UtilPlayer.message(target, F.main(GetClassType().name(), F.name(cur.getName()) + " threw you with " + F.skill(GetName(level)) + "."));

			//Effect
			target.playEffect(EntityEffect.HURT);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		if (_holding.containsKey(player))
		{
			player.eject();
		}

		for (Player cur : _holding.keySet())
		{
			if (_holding.get(cur).equals(player))
			{
				cur.eject();
				_holding.remove(cur);
				_charge.remove(cur);
			}
		}
		
		_holding.remove(player);
		_charge.remove(player);
		_charged.remove(player);
	}
}
