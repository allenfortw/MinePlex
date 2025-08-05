package mineplex.core.disguise.disguises;

public class DisguiseSnowman extends DisguiseGolem
{
	public DisguiseSnowman(org.bukkit.entity.Entity entity)
	{
		super(entity);
	}

	@Override
	protected int GetEntityTypeId()
	{
		return 97;
	}
}
