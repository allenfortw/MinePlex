package mineplex.core.packethandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.NautHashMap;
import net.minecraft.server.v1_6_R2.NetworkManager;
import net.minecraft.server.v1_6_R2.Packet;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings( { "rawtypes", "unchecked" } )
public class PacketHandler extends MiniPlugin
{
    private Field syncField;
    private Field highField;

    private NautHashMap<String, NautHashMap<Integer, Integer>> _forwardMap;
    private NautHashMap<String, HashSet<Integer>> _blockMap;
    private NautHashMap<String, NautHashMap<Integer, Packet>> _fakeVehicleMap;
    private NautHashMap<String, NautHashMap<Integer, Packet>> _fakePassengerMap;
    private ArrayList<IPacketRunnable> _packetRunnables;

    public PacketHandler(JavaPlugin plugin)
    {
    	super("PacketHandler", plugin);
    	
        _forwardMap = new NautHashMap<String, NautHashMap<Integer, Integer>>();
        _blockMap = new NautHashMap<String, HashSet<Integer>>();
        _fakeVehicleMap = new NautHashMap<String, NautHashMap<Integer, Packet>>();
        _fakePassengerMap = new NautHashMap<String, NautHashMap<Integer, Packet>>();
        _packetRunnables = new ArrayList<IPacketRunnable>();
        
        try 
        {
            this.syncField = NetworkManager.class.getDeclaredField("h");
            this.syncField.setAccessible(true);
            this.highField = NetworkManager.class.getDeclaredField("highPriorityQueue");
            this.highField.setAccessible(true);
        } 
        catch (final Exception e) 
        {
            System.out.println("Error initializing " + GetName() + " NetworkManager fields...");
        }
    }

	public String GetDataType(int c)
	{
		switch (c)
		{
			case 0:
				return "Byte";
			case 1:
				return "Short";
			case 2:
				return "Integer";
			case 3:
				return "Float";
			case 4:
				return "String";
			case 5:
				return "ItemStack";
			case 6:
				return "ChunkCoordinates";
		}

		return "Say what?";
	}

	public void AddPacketRunnable(IPacketRunnable runnable)
	{
		_packetRunnables.add(runnable);
	}
	
	public void RemovePacketRunnable(IPacketRunnable runnable)
	{
		_packetRunnables.remove(runnable);
	}
	
	public Packet GetFakeAttached(Player owner, int a)
	{
		return _fakeVehicleMap.get(owner.getName()).get(a);
	}

	public boolean IsFakeAttached(Player owner, int a)
	{
		return _fakeVehicleMap.containsKey(owner.getName()) && _fakeVehicleMap.get(owner.getName()).containsKey(a);
	}
	public Packet GetFakePassenger(Player owner, int a)
	{
		return _fakePassengerMap.get(owner.getName()).get(a);
	}

	public boolean IsFakePassenger(Player owner, int a)
	{
		return _fakePassengerMap.containsKey(owner.getName()) && _fakePassengerMap.get(owner.getName()).containsKey(a);
	}

	public boolean IsBlocked(Player owner, int a)
	{
		return _blockMap.containsKey(owner.getName()) && _blockMap.get(owner.getName()).contains(a);
	}

	public int GetForwardId(Player owner, int a)
	{
		return _forwardMap.get(owner.getName()).get(a);
	}

	public boolean IsForwarded(Player owner, int a)
	{
		return _forwardMap.get(owner.getName()).containsKey(a);
	}

	public boolean IsForwarding(Player owner)
	{
		return _forwardMap.containsKey(owner.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        in(event.getPlayer());
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) 
    {
        out(event.getPlayer());
    }
    
    public void in(Player player) 
    {
        try 
        {
            this.nom(this.getManager(player), Collections.synchronizedList(new PacketArrayList(player, this)));
            
        } 
        catch (final Exception e) 
        {
            // new TagAPIException("[TagAPI] Failed to inject into networkmanager for " + player.getName(), e).printStackTrace();
        }
    }

    public void out(Player player) 
    {
        try 
        {
            this.nom(this.getManager(player), Collections.synchronizedList(new ArrayList()), true);
        } 
        catch (final Exception e) 
        {
            this._plugin.getLogger().log(Level.WARNING, "Failed to restore " + player.getName() + ". Could be a problem.", e);
        }
    }

    public void shutdown() 
    {
        for (final Player player : this._plugin.getServer().getOnlinePlayers()) 
        {
            if (player != null) 
            {
                this.out(player);
            }
        }
    }

    private NetworkManager getManager(Player player) 
    {
        return (NetworkManager) ((CraftPlayer) player).getHandle().playerConnection.networkManager;
    }

    private void nom(NetworkManager nm, List list) throws IllegalArgumentException, IllegalAccessException 
    {
        this.nom(nm, list, false);
    }

    private void nom(NetworkManager nm, List list, boolean onlyIfOldIsHacked) throws IllegalArgumentException, IllegalAccessException 
    {
        final List old = (List) this.highField.get(nm);
        boolean copy = true;
        
        if (onlyIfOldIsHacked) 
        {
            if (!(old instanceof PacketArrayList)) 
            {
                return;
            }
            else
            {
            	copy = false;
            	((PacketArrayList)old).Deactivate();
            }
        }
        
        synchronized (this.syncField.get(nm)) 
        {
        	if (copy)
        	{
	            for (final Object object : old) 
	            {
	                list.add(object);
	            }
        	}
        	
            this.highField.set(nm, list);
        }
    }

	public void ForwardMovement(Player viewer, int travellerId, int entityId)
	{
		if (!_forwardMap.containsKey(viewer.getName()))
		{
			_forwardMap.put(viewer.getName(), new NautHashMap<Integer, Integer>());
		}
		
		_forwardMap.get(viewer.getName()).put(travellerId, entityId);
	}

	public void BlockMovement(Player otherPlayer, int entityId)
	{
		if (!_blockMap.containsKey(otherPlayer.getName()))
		{
			_blockMap.put(otherPlayer.getName(), new HashSet<Integer>());
		}
		
		_blockMap.get(otherPlayer.getName()).add(entityId);
	}
	
	public void FakeVehicle(Player viewer, int entityId, Packet packet)
	{
		if (!_fakeVehicleMap.containsKey(viewer.getName()))
		{
			_fakeVehicleMap.put(viewer.getName(), new NautHashMap<Integer, Packet>());
		}
		
		_fakeVehicleMap.get(viewer.getName()).put(entityId, packet);
	}

	public void RemoveFakeVehicle(Player viewer, int entityId)
	{
		if (_fakeVehicleMap.containsKey(viewer.getName()))
		{
			_fakeVehicleMap.get(viewer.getName()).remove(entityId);
		}
	}
	
	public void FakePassenger(Player viewer, int entityId, Packet packet)
	{
		if (!_fakePassengerMap.containsKey(viewer.getName()))
		{
			_fakePassengerMap.put(viewer.getName(), new NautHashMap<Integer, Packet>());
		}
		
		_fakePassengerMap.get(viewer.getName()).put(entityId, packet);
	}

	public void RemoveFakePassenger(Player viewer, int entityId)
	{
		if (_fakePassengerMap.containsKey(viewer.getName()))
		{
			_fakePassengerMap.get(viewer.getName()).remove(entityId);
		}
	}
	
	public void RemoveForward(Player viewer)
	{
		_forwardMap.remove(viewer.getName());
	}

	public boolean FireRunnables(Packet o, Player owner, PacketArrayList packetList)
	{
		boolean addOriginal = true;
		
        for (IPacketRunnable packetRunnable : _packetRunnables)
        {
        	if (!packetRunnable.run(o, owner, packetList))
        		addOriginal = false;
        }
        
        return addOriginal;
	}
}