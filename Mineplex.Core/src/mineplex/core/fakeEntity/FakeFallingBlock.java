package mineplex.core.fakeEntity;

import net.minecraft.server.v1_6_R2.EnumEntitySize;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet23VehicleSpawn;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeFallingBlock extends FakeEntity
{
	private int _materialId;
	private byte _data;
	
    public FakeFallingBlock(int materialId, byte data, Location location)
	{
		super(EntityType.FALLING_BLOCK, location);
		
		_materialId = materialId;
		_data = data;
	}
	
	public Packet Spawn(int id)
	{
	    Packet23VehicleSpawn packet = new Packet23VehicleSpawn(); 
	    packet.a = id;
	    packet.b = (int)EnumEntitySize.SIZE_2.a(GetLocation().getX());
	    packet.c = (int)MathHelper.floor(GetLocation().getY() * 32.0D);
	    packet.d = (int)EnumEntitySize.SIZE_2.a(GetLocation().getZ());
	    
        double var4 = 0;
        double var6 = .045;
        double var8 = 0;
        double var10 = 3.9D;

        if (var4 < -var10)
        {
            var4 = -var10;
        }

        if (var6 < -var10)
        {
            var6 = -var10;
        }

        if (var8 < -var10)
        {
            var8 = -var10;
        }

        if (var4 > var10)
        {
            var4 = var10;
        }

        if (var6 > var10)
        {
            var6 = var10;
        }

        if (var8 > var10)
        {
            var8 = var10;
        }

        packet.e = (int)(var4 * 8000.0D);
        packet.f = (int)(var6 * 8000.0D);
        packet.g = (int)(var8 * 8000.0D);
	    packet.h = 0;
	    packet.i = 0;
	    packet.j = 70;
	    packet.k = _materialId | _data << 16;
		
	    System.out.println("Creating fake falling block with entityId " + GetEntityId());
	    
		return packet;
	}
}
