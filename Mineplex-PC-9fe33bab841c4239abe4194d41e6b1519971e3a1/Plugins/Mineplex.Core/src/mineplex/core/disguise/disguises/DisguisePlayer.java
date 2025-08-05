package mineplex.core.disguise.disguises;

import java.lang.reflect.Field;

import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet20NamedEntitySpawn;

public class DisguisePlayer extends DisguiseHuman
{
	private static Field _spawnDataWatcherField;
	
	private String _name;
	
	public DisguisePlayer(org.bukkit.entity.Entity entity, String name)
	{
		super(entity);
		
		if (name.length() > 16)
		{
			name = name.substring(0, 16);
		}
		
		_name = name;
		
		SetSpawnDataWatcherField();
	}

	@Override
	public Packet GetSpawnPacket()
	{
        Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
        packet.a = Entity.id;
        packet.b = _name;
        packet.c = MathHelper.floor(Entity.locX * 32.0D);
        packet.d = MathHelper.floor(Entity.locY * 32.0D);
        packet.e = MathHelper.floor(Entity.locZ * 32.0D);
        packet.f = (byte) ((int) (Entity.yaw * 256.0F / 360.0F));
        packet.g = (byte) ((int) (Entity.pitch * 256.0F / 360.0F));
		
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
		
        return packet;
	}
	
	private void SetSpawnDataWatcherField()
	{
		if (_spawnDataWatcherField == null)
		{
			try
			{
				_spawnDataWatcherField = Packet20NamedEntitySpawn.class.getDeclaredField("i");
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
}
