package mineplex.minecraft.game.core.combat;

public class CombatDamage
{
	private String _name;
	private double _dmg;

	public CombatDamage(String name, double dmg)
	{
		_name = name;
		_dmg = dmg;
	}

	public String GetName()
	{
		return _name;
	}

	public double GetDamage()
	{
		return _dmg;
	}
}
