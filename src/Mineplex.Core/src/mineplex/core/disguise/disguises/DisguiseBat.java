package mineplex.core.disguise.disguises;

import net.minecraft.server.v1_6_R2.Packet;

public class DisguiseBat extends DisguiseAmbient
{
	public DisguiseBat(org.bukkit.entity.Entity entity)
	{
		super(entity);
	}

	@Override
	public Packet GetSpawnPacket()
	{
		return null;
	}
	
	public String getHurtSound()
	{
		return "mob.bat.hurt";
	}
	
    protected float getVolume()
    {
        return 0.1F;
    }
    
    protected float getPitch()
    {
        return super.getPitch() * 0.95F;
    }
}
