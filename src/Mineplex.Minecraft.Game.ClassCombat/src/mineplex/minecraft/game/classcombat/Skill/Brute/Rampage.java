package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashSet;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Rampage extends SkillActive
{
	private WeakHashMap<Player, Long> _rampageStart = new WeakHashMap<Player, Long>();
	private WeakHashMap<Player, Long> _rampageCharge = new WeakHashMap<Player, Long>();
	private WeakHashMap<Player, Integer> _rampageBonus = new WeakHashMap<Player, Integer>();

	public Rampage(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
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
				"Go into a rampage",
				"* Slow I for 6 + 2pL seconds",
				"* +1 Damage per 2 seconds you've been in Rampage",
				"Rampage ends;",
				"* If you don't attack/get attacked for 2 + 1pL seconds",
				"* After 6 + 2pL seconds"
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		_rampageStart.put(player, System.currentTimeMillis());
		_rampageCharge.put(player, System.currentTimeMillis());
		_rampageBonus.put(player, 0);

		//Slow
		Factory.Condition().Factory().Slow(GetName(), player, player, 6 + (level * 2), 0, false, true, false, true);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));

		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 0.5f);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null) 	return;

		if (!_rampageBonus.containsKey(damager))
			return;

		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)	return;

		//Damage
		event.AddMod(damager.getName(), GetName(), _rampageBonus.get(damager), true);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		HashSet<Player>	remove = new HashSet<Player>();

		for (Player cur : _rampageStart.keySet())
		{
			int level = GetLevel(cur);
			if (level == 0)
			{
				remove.add(cur);
				continue;
			}
			
			//Not Damaged
			if (UtilTime.elapsed(Factory.Combat().Get(cur).LastCombat, 2000 + (1000 * level)))
			{
				if (_rampageStart.get(cur) != null && UtilTime.elapsed(_rampageStart.get(cur), 2000 + (1000 * level)))
				{
					remove.add(cur);
					continue;
				}
			}

			//Rampage Over
			if (UtilTime.elapsed(_rampageStart.get(cur), 6000 + (level * 2000)))
			{
				remove.add(cur);
				continue;
			}
			
			//Level Up
			if (UtilTime.elapsed(_rampageCharge.get(cur), 2000))
			{
				_rampageCharge.put(cur, System.currentTimeMillis());
				_rampageBonus.put(cur, _rampageBonus.get(cur) + 1);
				UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem("+" + _rampageBonus.get(cur) + " Damage")));

				//Effect
				cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 0.5f, 0.5f + (_rampageBonus.get(cur) * 0.1f));
			}	
		}

		for (Player cur : remove)
		{
			_rampageStart.remove(cur);
			_rampageCharge.remove(cur);
			_rampageBonus.remove(cur);

			//Inform
			UtilPlayer.message(cur, F.main(GetClassType().name(), "Your " + F.skill(GetName()) + " has ended."));
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_rampageStart.remove(player);
		_rampageCharge.remove(player);
		_rampageBonus.remove(player);
	}
}
