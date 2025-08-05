package mineplex.core.disguise.disguises;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;

import net.minecraft.server.v1_6_R2.DataWatcher;
import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;

public abstract class DisguiseBase
{
	protected Entity Entity;
	protected DataWatcher DataWatcher;
	
	public DisguiseBase(org.bukkit.entity.Entity entity)
	{
		Entity = ((CraftEntity)entity).getHandle();
		DataWatcher = new DataWatcher();
		
		DataWatcher.a(0, Byte.valueOf((byte)0));
		DataWatcher.a(1, Short.valueOf((short)300));
	}
	
	public void UpdateDataWatcher()
	{
		DataWatcher.watch(0, Entity.getDataWatcher().getByte(0));
		DataWatcher.watch(1, Entity.getDataWatcher().getShort(1));
		
		if (this instanceof DisguiseEnderman)
		{
			DataWatcher.watch(0, Byte.valueOf((byte)(DataWatcher.getByte(0) & ~(1 << 0))));
		}
	}
	
	public abstract Packet GetSpawnPacket();

	public Packet GetMetaDataPacket()
	{
		UpdateDataWatcher();
		return new Packet40EntityMetadata(Entity.id, DataWatcher, true);
	}
	
	public void playHurtSound()
	{
		Entity.world.makeSound(Entity, getHurtSound(), getVolume(), getPitch());
	}
	
	public void playHurtSound(Location location)
	{
		Entity.world.makeSound(location.getX(), location.getY(), location.getZ(), getHurtSound(), getVolume(), getPitch());
	}
	
	public void UpdateEntity(Entity entity)
	{
		Entity = entity;
	}

	public Entity GetEntity()
	{
		return Entity;
	}

	public int GetEntityId()
	{
		return Entity.id;
	}
	
	protected abstract String getHurtSound();
	
	protected abstract float getVolume();
	
	protected abstract float getPitch();
}
