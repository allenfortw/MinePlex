package mineplex.core.disguise.disguises;

public abstract class DisguiseHuman extends DisguiseLiving
{
	public DisguiseHuman(org.bukkit.entity.Entity entity)
	{
		super(entity);
		
	    DataWatcher.a(16, Byte.valueOf((byte)0));
	    DataWatcher.a(17, Float.valueOf(0.0F));
	    DataWatcher.a(18, Integer.valueOf(0));
	}
}
