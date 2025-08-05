package mineplex.core.packethandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet28EntityVelocity;
import net.minecraft.server.v1_6_R2.Packet31RelEntityMove;
import net.minecraft.server.v1_6_R2.Packet33RelEntityMoveLook;
import net.minecraft.server.v1_6_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;

import org.bukkit.entity.Player;

public class PacketArrayList extends ArrayList<Packet>
{
	private static final long serialVersionUID = 1L;
	private Player _owner;
    private PacketHandler _handler;
    private Field _packet40Metadata;

    public PacketArrayList(Player owner, PacketHandler handler) 
    {
        _owner = owner;
        _handler = handler;
        
        try 
        {
			_packet40Metadata = Packet40EntityMetadata.class.getDeclaredField("b");
		} 
        catch (NoSuchFieldException e) 
        {
			e.printStackTrace();
		} 
        catch (SecurityException e) 
        {
			e.printStackTrace();
		}
        
        _packet40Metadata.setAccessible(true);
    }
    
    @Override
    public boolean add(Packet o) 
    {
        /*
        else if (o instanceof Packet201PlayerInfo) 
        {
        	if (!_handler.IsTracked((Packet201PlayerInfo)o))
        		return false;
            //if (( && _packetHandler.IsPlayerTracked(((Packet201PlayerInfo)o).a) )
            //{
            //    return false;
            //}
        }
        else if (o instanceof Packet40EntityMetadata)
        {
        	List<WatchableObject> objects = null;
			try 
			{
				objects = (List<WatchableObject>)_packet40Metadata.get(((Packet40EntityMetadata)o));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
			System.out.println("Packet40EntityMetadata for " + ((Packet40EntityMetadata)o).a);
			
			if (objects != null)
			{
            	for (WatchableObject watched : objects)
            	{
            		System.out.println("a(" + watched.a() + ", " + watched.b() + ") " + GetDataType(watched.c()));
            	}	
			}
			
			System.out.println("End Packet40EntityMetadata");
        }
        */
        if (o instanceof Packet34EntityTeleport)
        {
        	Packet34EntityTeleport packet = (Packet34EntityTeleport)o;
        	
        	//System.out.println("Packet34EntityTeleport (" + packet.b + ", " + packet.c + ", " + packet.d + ")");
        	
        	if (_handler.IsForwarding(_owner) && _handler.IsForwarded(_owner, packet.a))
        	{
        		return super.add(new Packet34EntityTeleport(_handler.GetForwardId(_owner, packet.a), packet.b, packet.c, packet.d, packet.e, packet.f));
        	}
        	else if (_handler.IsBlocked(_owner, packet.a))
        		return false;
        }
        else if (o instanceof Packet28EntityVelocity)
        {
        	Packet28EntityVelocity packet = (Packet28EntityVelocity)o;
        	
        	//System.out.println("Packet28EntityVelocity (" + packet.b / 8000.0D + ", " + packet.c / 8000.0D + ", " + packet.d / 8000.0D + ") for " + packet.a + " to " + _owner.getName());
        	
        	if (_handler.IsForwarding(_owner) && _handler.IsForwarded(_owner, packet.a))
        	{
        		// Occasional velocity sent for player in MK jacks up karts so don't process this.
            	return false;
        	}
        	else if (_handler.IsBlocked(_owner, packet.a))
        		return false;
        }
        else if (o instanceof Packet31RelEntityMove)
        {
        	Packet31RelEntityMove packet = (Packet31RelEntityMove)o;
        	
        	//System.out.println("Packet31RelEntityMove (" + packet.b + ", " + packet.c + ", " + packet.d + ")");
        	
        	if (_handler.IsForwarding(_owner) && _handler.IsForwarded(_owner, packet.a))
        	{
        		return super.add(new Packet31RelEntityMove(_handler.GetForwardId(_owner, packet.a), packet.b, packet.c, packet.d));
        	}
        	else if (_handler.IsBlocked(_owner, packet.a))
        		return false;
        }
        else if (o instanceof Packet33RelEntityMoveLook)
        {
        	Packet33RelEntityMoveLook packet = (Packet33RelEntityMoveLook)o;
        	
        	//System.out.println("Packet33RelEntityMoveLook (" + packet.b + ", " + packet.c + ", " + packet.d + ")");
        	
        	if (_handler.IsForwarding(_owner) && _handler.IsForwarded(_owner, packet.a))
        	{
        		return super.add(new Packet33RelEntityMoveLook(_handler.GetForwardId(_owner, packet.a), packet.b, packet.c, packet.d, packet.e, packet.f));
        	}
        	else if (_handler.IsBlocked(_owner, packet.a))
        		return false;
        }
        /*
        else if (!(o instanceof Packet206SetScoreboardObjective) && !(o instanceof Packet207SetScoreboardScore) && !(o instanceof Packet208SetScoreboardDisplayObjective) && !(o instanceof Packet56MapChunkBulk) && !(o instanceof Packet0KeepAlive) && !(o instanceof Packet4UpdateTime) && !(o instanceof Packet43SetExperience) && !(o instanceof Packet35EntityHeadRotation) && !(o instanceof Packet32EntityLook))
        {
        	System.out.println(o.getClass());
        }
        */
        
        if (_handler.FireRunnables(o, _owner, this))
        {
        	return forceAdd(o);
        }
        
        return true;
    }
    
    public boolean forceAdd(Packet packet)
    {
    	return super.add(packet);
    }
    
    public void Deactivate()
    {
		_owner = null;
        _packet40Metadata = null;
    }
}
