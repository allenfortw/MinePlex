package mineplex.minecraft.game.core.combat;

import java.util.HashMap;
import java.util.LinkedList;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CombatComponent
{	
	private boolean _player = false;

	private LinkedList<CombatDamage> _damage;

	protected String EntityName;
	protected long LastDamage = 0;
	
	public CombatComponent(String name, LivingEntity ent)
	{
		EntityName = name;

		if (ent != null)
		{
			if (ent instanceof Player)
			{
				_player = true;
			}
		}
	}

	public void AddDamage(String source, double dmg)
	{
		if (source == null)
			source = "-";

		GetDamage().addFirst(new CombatDamage(source, dmg));
		LastDamage = System.currentTimeMillis();
	}

	public String GetName()
	{
		if (EntityName.equals("Null"))
			return "World";

		return EntityName;
	}

	public LinkedList<CombatDamage> GetDamage()
	{
		if (_damage == null)
			_damage = new LinkedList<CombatDamage>();

		return _damage;
	}

	public long GetLastDamage()
	{
		return LastDamage;
	}

	public int GetTotalDamage()
	{
		int total = 0;
		for (CombatDamage cur : GetDamage())
			total += cur.GetDamage();
		return total;
	}

	public String GetBestWeapon()
	{
		HashMap<String, Integer> cumulative = new HashMap<String, Integer>();
		String weapon = null;
		int best = 0;
		for (CombatDamage cur : _damage)
		{
			int dmg = 0;
			if (cumulative.containsKey(cur.GetName()))
				dmg = cumulative.get(cur.GetName());

			cumulative.put(cur.GetName(), dmg);

			if (dmg >= best)
				weapon = cur.GetName();
		}

		return weapon;
	}

	public String Display(long _deathTime)
	{
		// Time
		String time = "";
		if (_deathTime == 0)
			time = UtilTime.convertString(System.currentTimeMillis()
					- LastDamage, 1, TimeUnit.FIT)
					+ " Ago";
		else
			time = UtilTime.convertString(_deathTime - LastDamage, 1,
					TimeUnit.FIT) + " Prior";

		return F.name(EntityName) + " ["
				+ F.elem(GetTotalDamage() + "dmg") + "" + "] ["
				+ F.elem(GetBestWeapon()) + "]  [" + F.time(time) + "]";
	}

	public boolean IsPlayer()
	{
		return _player;
	}

	public String GetLastDamageSource()
	{
		return _damage.getFirst().GetName();
	}
}
