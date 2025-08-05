package mineplex.core.disguise.disguises;

import java.util.Random;

public abstract class DisguiseLiving extends DisguiseBase
{
	private static Random _random = new Random();
	
	public DisguiseLiving(org.bukkit.entity.Entity entity)
	{
		super(entity);
	
		DataWatcher.a(6, Float.valueOf(1.0F));
	    DataWatcher.a(7, Integer.valueOf(0));
	    DataWatcher.a(8, Byte.valueOf((byte)0));
	    DataWatcher.a(9, Byte.valueOf((byte)0));
	}
	
	public void UpdateDataWatcher()
	{
		super.UpdateDataWatcher();

		DataWatcher.watch(6, Entity.getDataWatcher().getFloat(6));
	    DataWatcher.watch(7, Entity.getDataWatcher().getInt(7));
	    DataWatcher.watch(8, Entity.getDataWatcher().getByte(8));
	    DataWatcher.watch(9, Entity.getDataWatcher().getByte(9));
	}
	
    protected String getHurtSound()
    {
        return "damage.hit";
    }
	
    protected float getVolume()
    {
        return 1.0F;
    }

    protected float getPitch()
    {
        return (_random.nextFloat() - _random.nextFloat()) * 0.2F + 1.0F;
    }
}
