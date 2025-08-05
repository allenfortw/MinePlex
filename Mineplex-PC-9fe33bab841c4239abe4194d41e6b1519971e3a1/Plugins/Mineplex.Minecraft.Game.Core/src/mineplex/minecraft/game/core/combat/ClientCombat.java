package mineplex.minecraft.game.core.combat;

import java.util.LinkedList;
import java.util.WeakHashMap;

import org.bukkit.entity.LivingEntity;

public class ClientCombat
{
	private LinkedList<CombatLog> _kills = new LinkedList<CombatLog>();
	private LinkedList<CombatLog> _assists = new LinkedList<CombatLog>();
	private LinkedList<CombatLog> _deaths = new LinkedList<CombatLog>();

	private WeakHashMap<LivingEntity, Long> _lastHurt = new WeakHashMap<LivingEntity, Long>();
	private long _lastHurtByOther = 0;

	public LinkedList<CombatLog> GetKills() 
	{
		return _kills;
	}

	public LinkedList<CombatLog> GetAssists() 
	{
		return _assists;
	}

	public LinkedList<CombatLog> GetDeaths() 
	{
		return _deaths;
	}

	public boolean CanBeHurtBy(LivingEntity damager)
	{
		if (damager != null)
			return true;
		
		if (System.currentTimeMillis() - _lastHurtByOther > 250)
		{
			_lastHurtByOther = System.currentTimeMillis();
			return true;
		}

		return false;
	}

	public boolean CanHurt(LivingEntity damagee) 
	{
		if (damagee == null)
			return true;

		if (!_lastHurt.containsKey(damagee))
		{
			_lastHurt.put(damagee, System.currentTimeMillis());
			return true;
		}

		if (System.currentTimeMillis() - _lastHurt.get(damagee) > 400)
		{
			_lastHurt.put(damagee, System.currentTimeMillis());
			return true;
		}

		return false;
	}
}
