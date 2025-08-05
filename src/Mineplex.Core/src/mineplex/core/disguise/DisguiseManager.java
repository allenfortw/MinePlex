package mineplex.core.disguise;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.server.v1_6_R2.ChunkAddEntityEvent;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;
import net.minecraft.server.v1_6_R2.Packet28EntityVelocity;
import net.minecraft.server.v1_6_R2.Packet29DestroyEntity;
import net.minecraft.server.v1_6_R2.Packet31RelEntityMove;
import net.minecraft.server.v1_6_R2.Packet33RelEntityMoveLook;
import net.minecraft.server.v1_6_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_6_R2.Packet5EntityEquipment;
import net.minecraft.server.v1_6_R2.Packet62NamedSoundEffect;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilMath;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguiseInsentient;
import mineplex.core.disguise.disguises.DisguisePlayer;
import mineplex.core.packethandler.IPacketRunnable;
import mineplex.core.packethandler.PacketArrayList;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

public class DisguiseManager extends MiniPlugin implements IPacketRunnable
{	
	private NautHashMap<Integer, DisguiseBase> _spawnPacketMap = new NautHashMap<Integer, DisguiseBase>();
	private NautHashMap<Integer, Packet28EntityVelocity> _movePacketMap = new NautHashMap<Integer, Packet28EntityVelocity>();
	private NautHashMap<Integer, Packet28EntityVelocity> _moveTempMap = new NautHashMap<Integer, Packet28EntityVelocity>();
	private HashSet<Integer> _goingUp = new HashSet<Integer>();
	private NautHashMap<String, DisguiseBase> _entityDisguiseMap = new NautHashMap<String, DisguiseBase>();
	private NautHashMap<String, EntityType> _addTempList = new NautHashMap<String, EntityType>();
	private HashSet<String> _delTempList = new HashSet<String>();

	private Field _soundB;
	private Field _soundC;
	private Field _soundD;

	public DisguiseManager(JavaPlugin plugin, PacketHandler packetHandler)
	{
		super("Disguise Manager", plugin);

		packetHandler.AddPacketRunnable(this);

		try
		{
			_soundB = Packet62NamedSoundEffect.class.getDeclaredField("b");
			_soundB.setAccessible(true);
			_soundC = Packet62NamedSoundEffect.class.getDeclaredField("c");
			_soundC.setAccessible(true);
			_soundD = Packet62NamedSoundEffect.class.getDeclaredField("d");
			_soundD.setAccessible(true);
		} 
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
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

	public boolean isDisguised(LivingEntity entity)
	{
		return _spawnPacketMap.containsKey(entity.getEntityId());
	}

	public DisguiseBase getDisguise(LivingEntity entity)
	{
		return _spawnPacketMap.get(entity.getEntityId());
	}

	public void disguise(DisguiseBase disguise)
	{
		_spawnPacketMap.put(disguise.GetEntityId(), disguise);

		reApplyDisguise(disguise);
	}

	public void undisguise(LivingEntity entity)
	{
		if (!_spawnPacketMap.containsKey(entity.getEntityId()))
			return;

		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (entity == player)
				continue;

			EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
			entityPlayer.playerConnection.sendPacket(new Packet29DestroyEntity(entity.getEntityId()));

			if (entity instanceof Player)
			{
				player.showPlayer((Player)entity);
			}
			else
			{
				entityPlayer.playerConnection.sendPacket(new Packet24MobSpawn(((CraftLivingEntity)entity).getHandle()));
			}
		}

		_spawnPacketMap.remove(entity.getEntityId());
		_movePacketMap.remove(entity.getEntityId());
		_moveTempMap.remove(entity.getEntityId());
	}

	public void reApplyDisguise(final DisguiseBase disguise)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (disguise.GetEntity() == ((CraftPlayer)player).getHandle())
				continue;

			EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();

			entityPlayer.playerConnection.sendPacket(new Packet29DestroyEntity(disguise.GetEntityId()));
		}

		List<Packet> tempArmor = new ArrayList<Packet>();
		
		if (disguise instanceof DisguiseInsentient && disguise.GetEntity() instanceof LivingEntity)
		{
			if (((DisguiseInsentient)disguise).armorVisible())
			{
				for (Packet armorPacket : ((DisguiseInsentient)disguise).getArmorPackets())
					tempArmor.add(armorPacket);
			}
		}
		
		final List<Packet> armorPackets = tempArmor;
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
		{
			public void run()
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					if (disguise.GetEntity() == ((CraftPlayer)player).getHandle())
						continue;

					EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
					entityPlayer.playerConnection.sendPacket(disguise.GetSpawnPacket());
					
					for (Packet packet : armorPackets)
					{
						entityPlayer.playerConnection.sendPacket(packet);
					}
				}
			}
		});
	}

	public void updateDisguise(DisguiseBase disguise)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (disguise.GetEntity() == ((CraftPlayer)player).getHandle())
				continue;

			EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();

			entityPlayer.playerConnection.sendPacket(disguise.GetMetaDataPacket());
		}
	}

	@EventHandler
	public void ChunkUnload(ChunkUnloadEvent event)
	{
		for (Entity entity : event.getChunk().getEntities())
		{
			Iterator<Entry<Integer, DisguiseBase>> spawnPacketMapIterator = _spawnPacketMap.entrySet().iterator();
			while (spawnPacketMapIterator.hasNext())
			{
				Entry<Integer, DisguiseBase> entry = spawnPacketMapIterator.next();

				if (entity.getEntityId() == entry.getValue().GetEntityId())
				{
					_entityDisguiseMap.put(entity.getUniqueId().toString(), entry.getValue());
					spawnPacketMapIterator.remove();
				}
			}
		}
	}

	@EventHandler
	public void ChunkAddEntity(ChunkAddEntityEvent event)
	{
		DisguiseBase disguise = _entityDisguiseMap.get(event.GetEntity().getUniqueId().toString());

		if (disguise != null)
		{
			disguise.UpdateEntity(((CraftLivingEntity)event.GetEntity()).getHandle());
			_spawnPacketMap.put(event.GetEntity().getEntityId(), disguise);
			_entityDisguiseMap.remove(event.GetEntity().getUniqueId().toString());
		}
	}

	@EventHandler
	public void TeleportDisguises(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Player player : Bukkit.getOnlinePlayers())
		{
			for (Player otherPlayer : Bukkit.getOnlinePlayers())
			{
				if (player == otherPlayer)
					continue;

				if (otherPlayer.getLocation().subtract(0, .5, 0).getBlock().getTypeId() != 0)
					((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet34EntityTeleport(((CraftPlayer)otherPlayer).getHandle()));
			}
		}
	}

	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		undisguise(event.getPlayer());
	}

	@Override
	public boolean run(Packet packet, Player owner, final PacketArrayList packetList)
	{
		if (packet instanceof Packet20NamedEntitySpawn)
		{
			int entityId = ((Packet20NamedEntitySpawn)packet).a;

			if (_spawnPacketMap.containsKey(entityId))
			{
				packetList.forceAdd(_spawnPacketMap.get(entityId).GetSpawnPacket());
				return false;
			}
		}
		else if (packet instanceof Packet24MobSpawn)
		{
			int entityId = ((Packet24MobSpawn)packet).a;

			if (_spawnPacketMap.containsKey(entityId))
			{
				packetList.forceAdd(_spawnPacketMap.get(entityId).GetSpawnPacket());
				return false;
			}
		}
		else if (packet instanceof Packet40EntityMetadata)
		{
			int entityId = ((Packet40EntityMetadata)packet).a;

			if (_spawnPacketMap.containsKey(entityId) && owner.getEntityId() != entityId)
			{
				packetList.forceAdd(_spawnPacketMap.get(entityId).GetMetaDataPacket());
				return false;
			}
		}
		else if (packet instanceof Packet5EntityEquipment)
		{
			int entityId = ((Packet5EntityEquipment)packet).a;

			if (_spawnPacketMap.containsKey(entityId) && _spawnPacketMap.get(entityId) instanceof DisguiseInsentient)
			{
				if (!((DisguiseInsentient)_spawnPacketMap.get(entityId)).armorVisible() && ((Packet5EntityEquipment)packet).b != 0)
				{			
					return false;
				}
			}
		}
		else if (packet instanceof Packet28EntityVelocity)
		{
			Packet28EntityVelocity velocityPacket = (Packet28EntityVelocity)packet;

			// Only for viewers
			if (velocityPacket.a == owner.getEntityId())
			{
				if (velocityPacket.c > 0)
					_goingUp.add(velocityPacket.a);
			}
			else if (velocityPacket.b == 0 && velocityPacket.c == 0 && velocityPacket.d == 0)
			{
				return true;
			}
			else if (_spawnPacketMap.containsKey(velocityPacket.a))
			{
				return false;
			}
		}
		else if (packet instanceof Packet31RelEntityMove)
		{
			final Packet31RelEntityMove movePacket = (Packet31RelEntityMove)packet;

			// Only for viewers
			if (movePacket.a == owner.getEntityId())
				return true;

			if (_goingUp.contains(movePacket.a) && movePacket.c != 0 && movePacket.c < 20)
			{
				_goingUp.remove(movePacket.a);
				_movePacketMap.remove(movePacket.a);
			}

			if (!_spawnPacketMap.containsKey(movePacket.a))
				return true;

			final Packet28EntityVelocity velocityPacket = new Packet28EntityVelocity();
			velocityPacket.a = movePacket.a;
			velocityPacket.b = movePacket.b * 100;
			velocityPacket.c = movePacket.c * 100;
			velocityPacket.d = movePacket.d * 100;

			if (_movePacketMap.containsKey(movePacket.a))
			{
				Packet28EntityVelocity lastVelocityPacket = _movePacketMap.get(movePacket.a);

				velocityPacket.b = (int) (.8 * lastVelocityPacket.b);
				velocityPacket.c = (int) (.8 * lastVelocityPacket.c);
				velocityPacket.d = (int) (.8 * lastVelocityPacket.d);
			}

			_movePacketMap.put(movePacket.a, velocityPacket);

			packetList.forceAdd(velocityPacket);

			if (_goingUp.contains(movePacket.a) && movePacket.c != 0 && movePacket.c > 20)
			{
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
				{
					public void run()
					{
						packetList.forceAdd(velocityPacket);
					}
				});
			}
		}
		else if (packet instanceof Packet33RelEntityMoveLook)
		{
			final Packet33RelEntityMoveLook movePacket = (Packet33RelEntityMoveLook)packet;

			// Only for viewers
			if (movePacket.a == owner.getEntityId())
				return true;

			if (_goingUp.contains(movePacket.a) && movePacket.c != 0 && movePacket.c <= 20)
			{
				_goingUp.remove(movePacket.a);
				_movePacketMap.remove(movePacket.a);
			}

			if (!_spawnPacketMap.containsKey(movePacket.a))
				return true;

			final Packet28EntityVelocity velocityPacket = new Packet28EntityVelocity();
			velocityPacket.a = movePacket.a;
			velocityPacket.b = movePacket.b * 100;
			velocityPacket.c = movePacket.c * 100;
			velocityPacket.d = movePacket.d * 100;

			if (_movePacketMap.containsKey(movePacket.a))
			{
				Packet28EntityVelocity lastVelocityPacket = _movePacketMap.get(movePacket.a);

				velocityPacket.b = (int) (.8 * lastVelocityPacket.b);
				velocityPacket.c = (int) (.8 * lastVelocityPacket.c);
				velocityPacket.d = (int) (.8 * lastVelocityPacket.d);
			}

			_movePacketMap.put(movePacket.a, velocityPacket);

			packetList.forceAdd(velocityPacket);

			if (_goingUp.contains(movePacket.a) && movePacket.c != 0 && movePacket.c > 20)
			{
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
				{
					public void run()
					{
						packetList.forceAdd(velocityPacket);
					}
				});
			}
		}
		else if (packet instanceof Packet62NamedSoundEffect)
		{
			try
			{
				int x = (int) _soundB.get((Packet62NamedSoundEffect)packet) / 8;
				int y = (int) _soundC.get((Packet62NamedSoundEffect)packet) / 8;
				int z = (int) _soundD.get((Packet62NamedSoundEffect)packet) / 8;

				for (DisguiseBase disguise : _spawnPacketMap.values())
				{
					if (!(disguise instanceof DisguisePlayer))
						continue;

					if (UtilMath.offset(new Vector(disguise.GetEntity().locX, disguise.GetEntity().locY, disguise.GetEntity().locZ), new Vector(x, y, z)) <= 2)
					{
						return false;
					}
				}
			} 
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return true;
	}

	public void PrepAddDisguise(Player caller, EntityType entityType)
	{
		_addTempList.put(caller.getName(), entityType);
	}

	public void PrepRemoveDisguise(Player caller)
	{
		_delTempList.add(caller.getName());
	}
}
