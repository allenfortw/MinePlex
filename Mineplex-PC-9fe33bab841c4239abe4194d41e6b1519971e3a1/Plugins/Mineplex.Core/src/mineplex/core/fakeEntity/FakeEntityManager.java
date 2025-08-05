package mineplex.core.fakeEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_6_R2.Packet;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.packethandler.PacketHandler;

public class FakeEntityManager extends MiniPlugin
{
	public static FakeEntityManager Instance;
	
	private PacketHandler _packetHandler;
	
	private NautHashMap<String, List<FakeEntity>> _playerFakeEntityMap;
	
	public FakeEntityManager(JavaPlugin plugin)
	{
		super("Fake Entity Manager", plugin);
		
		_playerFakeEntityMap = new NautHashMap<String, List<FakeEntity>>();
	}
	
	public static void Initialize(JavaPlugin plugin)
	{
		Instance = new FakeEntityManager(plugin);
	}

	public void AddFakeEntity(FakeEntity entity, String name)
	{
		if (!_playerFakeEntityMap.containsKey(name))
		{
			_playerFakeEntityMap.put(name, new ArrayList<FakeEntity>());
		}
		
		_playerFakeEntityMap.get(name).add(entity);
	}
	
	public void ClearFakes(String name)
	{
		_playerFakeEntityMap.remove(name);
	}
	
	public void ClearFakeFor(FakeEntity entity, String name)
	{
		if (!_playerFakeEntityMap.containsKey(name))
		{
			_playerFakeEntityMap.put(name, new ArrayList<FakeEntity>());
		}
		
		_playerFakeEntityMap.get(name).remove(entity);
	}
	
	public List<FakeEntity> GetFakesFor(String name)
	{
		if (!_playerFakeEntityMap.containsKey(name))
		{
			_playerFakeEntityMap.put(name, new ArrayList<FakeEntity>());
		}
		
		return _playerFakeEntityMap.get(name);
	}

	public void SetPacketHandler(PacketHandler packetHandler)
	{
		_packetHandler = packetHandler;
	}

	public void RemoveForward(Player viewer)
	{
		_packetHandler.RemoveForward(viewer);
	}
	
	public void ForwardMovement(Player viewer, Player traveller, int entityId)
	{
		_packetHandler.ForwardMovement(viewer, traveller.getEntityId(), entityId);
	}

	public void BlockMovement(Player otherPlayer, int entityId)
	{
		_packetHandler.BlockMovement(otherPlayer, entityId);
	}

	public void FakePassenger(Player viewer, int entityId, Packet attachPacket)
	{
		_packetHandler.FakePassenger(viewer, entityId, attachPacket);
	}

	public void RemoveFakePassenger(Player viewer, int entityId)
	{
		_packetHandler.RemoveFakePassenger(viewer, entityId);
	}
	
	public void FakeVehicle(Player viewer, int entityId, Packet attachPacket)
	{
		_packetHandler.FakeVehicle(viewer, entityId, attachPacket);
	}

	public void RemoveFakeVehicle(Player viewer, int entityId)
	{
		_packetHandler.RemoveFakeVehicle(viewer, entityId);
	}

	public void SendPacketTo(Packet packet, Player player)
	{
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
}
