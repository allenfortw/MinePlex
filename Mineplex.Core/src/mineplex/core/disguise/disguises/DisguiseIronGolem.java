package mineplex.core.disguise.disguises;

public class DisguiseIronGolem extends DisguiseGolem
{
	public DisguiseIronGolem(org.bukkit.entity.Entity entity)
	{
		super(entity);

		DataWatcher.a(16, Byte.valueOf((byte)0));
	}

	@Override
	protected int GetEntityTypeId()
	{
		return 99;
	}
	
	public boolean bW() 
	{
	    return (DataWatcher.getByte(16) & 0x1) != 0;
	}

	public void setPlayerCreated(boolean flag) 
	{
		byte b0 = DataWatcher.getByte(16);
		
		if (flag)
			DataWatcher.watch(16, Byte.valueOf((byte)(b0 | 0x1)));
		else
			DataWatcher.watch(16, Byte.valueOf((byte)(b0 & 0xFFFFFFFE)));
	}
	
    protected String getHurtSound()
    {
        return "mob.irongolem.hit";
    }
}
