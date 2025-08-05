package mineplex.core.disguise.disguises;

import java.lang.reflect.Field;

import net.minecraft.server.v1_6_R2.EnumEntitySize;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;

public class DisguiseSlime extends DisguiseInsentient
{
	private static Field _spawnDataWatcherField;
	private static Field _spawnListField;

	public DisguiseSlime(org.bukkit.entity.Entity entity)
	{
		super(entity);

		DataWatcher.a(16, new Byte((byte)1));
		
		SetSpawnDataWatcherField();
		SetSpawnListField();
	}
	
	public void SetSize(int i)
	{
		DataWatcher.watch(16, new Byte((byte)i));
	}
	
	public int GetSize()
	{
		return DataWatcher.getByte(16);
	}

	public Packet GetSpawnPacket()
	{
		Packet24MobSpawn packet = new Packet24MobSpawn();
		packet.a = Entity.id;
		packet.b = (byte) 55;
		packet.c = (int)EnumEntitySize.SIZE_2.a(Entity.locX);
		packet.d = (int)MathHelper.floor(Entity.locY * 32.0D);
		packet.e = (int)EnumEntitySize.SIZE_2.a(Entity.locZ);
		packet.i = (byte) ((int) (Entity.yaw * 256.0F / 360.0F));
		packet.j = (byte) ((int) (Entity.pitch * 256.0F / 360.0F));
		packet.k = (byte) ((int) (Entity.yaw * 256.0F / 360.0F));

        double var2 = 3.9D;
        double var4 = 0;
        double var6 = 0;
        double var8 = 0;

        if (var4 < -var2)
        {
            var4 = -var2;
        }

        if (var6 < -var2)
        {
            var6 = -var2;
        }

        if (var8 < -var2)
        {
            var8 = -var2;
        }

        if (var4 > var2)
        {
            var4 = var2;
        }

        if (var6 > var2)
        {
            var6 = var2;
        }

        if (var8 > var2)
        {
            var8 = var2;
        }

        packet.f = (int)(var4 * 8000.0D);
        packet.g = (int)(var6 * 8000.0D);
        packet.h = (int)(var8 * 8000.0D);
		
		try
		{
			_spawnDataWatcherField.set(packet, DataWatcher);
		} 
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			_spawnListField.set(packet, DataWatcher.b());
		} 
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		return packet;
	}
	
	private void SetSpawnListField()
	{
		if (_spawnListField == null)
		{
			try
			{
				_spawnListField = Packet24MobSpawn.class.getDeclaredField("u");
				_spawnListField.setAccessible(true);
			} 
			catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			} 
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void SetSpawnDataWatcherField()
	{
		if (_spawnDataWatcherField == null)
		{
			try
			{
				_spawnDataWatcherField = Packet24MobSpawn.class.getDeclaredField("t");
				_spawnDataWatcherField.setAccessible(true);
			} 
			catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			} 
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}
	
    protected String getHurtSound()
    {
        return "mob.slime." + (GetSize() > 1 ? "big" : "small");
    }
    
    protected float getVolume()
    {
        return 0.4F * (float)GetSize();
    }
}
