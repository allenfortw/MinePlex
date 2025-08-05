package nautilus.minecraft.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_6_R2.DataWatcher;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;
import net.minecraft.server.v1_6_R2.Packet29DestroyEntity;
import net.minecraft.server.v1_6_R2.Packet5EntityEquipment;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PacketUtils 
{
    public static void killCarcass(Player p1) 
    {
        CraftPlayer p22 = (CraftPlayer) p1;
        Packet29DestroyEntity p29 = new Packet29DestroyEntity(p22.getEntityId());
        
        for (Player p2 : Bukkit.getServer().getOnlinePlayers()) 
        {
            if (p2.getName().equals(p1.getName())) 
                continue;

            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(p29);
            
            System.out.println("Killing carcass of '" + p1.getName() + "'");
        }
    }

    public static void undisguiseToAll(Player p1) 
    {
        // Make packets out of loop!
        CraftPlayer p22 = (CraftPlayer) p1;
        Packet29DestroyEntity p29 = new Packet29DestroyEntity(p22.getEntityId());
        Packet20NamedEntitySpawn p20 = new Packet20NamedEntitySpawn(p22.getHandle());      
        
        if (p1.getItemInHand() != null && p1.getItemInHand().getType() != Material.FISHING_ROD)
        	p20.h = p1.getItemInHand().getTypeId(); 
                
        List<Packet5EntityEquipment> p5 = new ArrayList<Packet5EntityEquipment>();
        net.minecraft.server.v1_6_R2.ItemStack[] armorContents = ((net.minecraft.server.v1_6_R2.Entity)p22.getHandle()).getEquipment();
        
        for (short i=0; i < armorContents.length; i++)
        {
        	net.minecraft.server.v1_6_R2.ItemStack armorSlot =  armorContents[i];
        	 
	        if (armorSlot != null) 
	        {
	        	p5.add(new Packet5EntityEquipment(p22.getEntityId(), i, armorSlot));
	        }
    	}
        
        for (Player p2 : Bukkit.getServer().getOnlinePlayers()) 
        {
            if (!p1.getWorld().equals(p2.getWorld())) 
            {
                continue;
            }
            if (p2 == p1) 
            {
                continue;
            }
            
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(p29);
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(p20);
            
            for (Packet5EntityEquipment equipPacket : p5)
            	((CraftPlayer) p2).getHandle().playerConnection.sendPacket(equipPacket);
        }
    }

    public static void disguiseToAll(Player p1, Byte id) 
    {
		DataWatcher metadata = new DataWatcher();
		metadata.a(0, (byte) 0);
		metadata.a(12, 0);
		if (id == EntityType.SHEEP.getTypeId() || id == EntityType.PIG.getTypeId() || id == EntityType.ENDERMAN.getTypeId()) {
			metadata.a(16, (byte) 0);
		} else if (id == EntityType.SLIME.getTypeId()) {
			metadata.a(16, (byte) 3);
		} else if (id == EntityType.VILLAGER.getTypeId()) {
			metadata.a(16, 0);
		}
		
		if (id == EntityType.CREEPER.getTypeId() || id == EntityType.ENDERMAN.getTypeId()) {
			metadata.a(17, (byte) 0);
		}
		if (id == EntityType.OCELOT.getTypeId()) {
			metadata.a(18, (byte) 0);
		}
		
        // Make packets out of loop!
        Packet24MobSpawn p24 = packetMaker(p1, id, metadata);
        
        for (Player p2 : Bukkit.getServer().getOnlinePlayers()) 
        {
        	disguiseToPlayer(p2, p1, id, metadata, p24);
        }
    }
    
    public static void disguiseToPlayer(Player p1, Player disguised, Byte id) 
    {
		DataWatcher metadata = new DataWatcher();
		metadata.a(0, (byte) 0);
		metadata.a(12, 0);
		if (id == EntityType.SHEEP.getTypeId() || id == EntityType.PIG.getTypeId() || id == EntityType.ENDERMAN.getTypeId()) {
			metadata.a(16, (byte) 0);
		} else if (id == EntityType.SLIME.getTypeId()) {
			metadata.a(16, (byte) 3);
		} else if (id == EntityType.VILLAGER.getTypeId()) {
			metadata.a(16, 0);
		}
		
		if (id == EntityType.CREEPER.getTypeId() || id == EntityType.ENDERMAN.getTypeId()) {
			metadata.a(17, (byte) 0);
		}
		if (id == EntityType.OCELOT.getTypeId()) {
			metadata.a(18, (byte) 0);
		}
		
        // Make packets out of loop!
        Packet24MobSpawn p24 = packetMaker(p1, id, metadata);
		
        disguiseToPlayer(p1, disguised, id, metadata, p24);
    }
    
    public static void disguiseToPlayer(Player p1, Player disguised, Byte id, DataWatcher metadata, Packet24MobSpawn p24) 
    {		        
        if (p1 == disguised) 
        {
            return;
        }
            
        ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(p24);
    }

    // Begin code for p2p disguising
    public static void disguisep2pToAll(Player p, String name) 
    {
    	if (name.length() > 16) name = name.substring(0,  16);
    	
        Packet20NamedEntitySpawn spawnPacket = packetMaker(p, name);
        Packet29DestroyEntity killPacket = new Packet29DestroyEntity(p.getEntityId()); 

        for (Player p1 : Bukkit.getServer().getOnlinePlayers()) 
        {
        	disguisep2pToPlayer(p1, p, killPacket, spawnPacket);
        }
    }
    
    public static void disguisep2pToPlayer(Player p1, Player disguised, String name) 
    {		        
    	if (name.length() > 16) name = name.substring(0,  16);
    	
        disguisep2pToPlayer(p1, disguised, new Packet29DestroyEntity(disguised.getEntityId()), packetMaker(disguised, name));
    }
    
    public static void disguisep2pToPlayer(Player p1, Player disguised, Packet29DestroyEntity killPacket, Packet20NamedEntitySpawn spawnPacket) 
    {		        
    	if (p1.getName().equals(disguised.getName())) 
            return;
        
        ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(killPacket);
        ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(spawnPacket);
        System.out.println("Disguising '" + disguised.getName() + "' as '" + spawnPacket.b + "' for '" + p1.getName() + "'");
    }

    public static void undisguisep2pToAll(Player p)
    {
        Packet20NamedEntitySpawn p20 = packetMaker(p, p.getName());
        Packet29DestroyEntity p29 = new Packet29DestroyEntity(p.getEntityId());
        
        for (Player p1 : Bukkit.getServer().getOnlinePlayers()) 
        {
        	if (p1.getName().equals(p.getName())) 
                continue;
            
            ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(p29);
            ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(p20);
            System.out.println("Revealing '" + p.getName() + "' for '" + p1.getName() + "'");
        }
    }

    public static Packet20NamedEntitySpawn packetMaker(Player p, String name) 
    {
        Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn(((CraftPlayer)p).getHandle());
        packet.b = name; 
        return packet;
    }

    public static Packet24MobSpawn packetMaker(Player p1, Byte id, DataWatcher metadata) 
    {       
    	Location loc = p1.getLocation();
		int x = MathHelper.floor(loc.getX() *32D);
		int y = MathHelper.floor(loc.getY() *32D);
		int z = MathHelper.floor(loc.getZ() *32D);
		
		Packet24MobSpawn packet = new Packet24MobSpawn();
		packet.a = p1.getEntityId();
		packet.b = id;
		packet.c = (int) x;
		packet.d = (int) y;
		packet.e = (int) z;
        packet.f = (byte) ((int) loc.getYaw() * 256.0F / 360.0F);
        packet.g = (byte) ((int) (loc.getPitch() * 256.0F / 360.0F));
		packet.h = packet.f;
		try {
			Field metadataField = packet.getClass().getDeclaredField("i");
			metadataField.setAccessible(true);
			metadataField.set(packet, metadata);
		} catch (Exception e) {
			System.out.println("unable to set the metadata for a disguise!");
			e.printStackTrace();
		}

		// Chicken fix
		if (id == EntityType.CHICKEN.getTypeId()) 
		{
			packet.g = (byte) (packet.g * -1);
		}
		
		return packet;
    }
}
