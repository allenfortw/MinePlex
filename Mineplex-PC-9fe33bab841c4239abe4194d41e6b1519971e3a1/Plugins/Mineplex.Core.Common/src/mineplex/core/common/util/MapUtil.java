package mineplex.core.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.v1_6_R2.ChunkCoordIntPair;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.RegionFile;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MapUtil 
{
	public static void ReplaceOreInChunk(Chunk chunk, Material replacee, Material replacer)
	{
    	net.minecraft.server.v1_6_R2.Chunk c = ((CraftChunk)chunk).getHandle();
    	
		for(int x = 0; x < 16; x++)
		{			
			for(int z = 0; z < 16; z++)
			{
				for(int y = 0; y < 18; y++)
				{
					int bX = c.x << 4 | x & 0xF;
					int bY = y & 0xFF;
					int bZ = c.z << 4 | z & 0xF;					

					if(c.getTypeId(bX & 0xF, bY, bZ & 0xF) == replacee.getId())
					{
						c.b(bX & 0xF, bY, bZ & 0xF, replacer.getId());
					}
				}
			}
		}

		c.initLighting();
	}
	
	public static void QuickChangeBlockAt(Location location, Material setTo)
	{
		QuickChangeBlockAt(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), setTo);
	}
	
	public static void QuickChangeBlockAt(Location location, int id, byte data)
	{
		QuickChangeBlockAt(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), id, data);
	}
	
	public static void QuickChangeBlockAt(World world, int x, int y, int z, Material setTo)
	{		
		QuickChangeBlockAt(world, x, y, z, setTo, 0);
	}
	
    public static void QuickChangeBlockAt(World world, int x, int y, int z, Material setTo, int data)
    {
    	QuickChangeBlockAt(world, x, y, z, setTo.getId(), data);
    }
    
    public static void QuickChangeBlockAt(World world, int x, int y, int z, int id, int data)
    {
        Chunk chunk = world.getChunkAt(x >> 4, z >> 4);     
        net.minecraft.server.v1_6_R2.Chunk c = ((CraftChunk)chunk).getHandle();
        
        c.a(x & 0xF, y, z & 0xF, id, data);
        ((CraftWorld)world).getHandle().notify(x, y, z);
    }
	
	public static int GetHighestBlockInCircleAt(World world, int bx, int bz, int radius)
	{
        int count = 0;
        int totalHeight = 0;

        final double invRadiusX = 1 / radius;
        final double invRadiusZ = 1 / radius;

        final int ceilRadiusX = (int) Math.ceil(radius);
        final int ceilRadiusZ = (int) Math.ceil(radius);
        
        double nextXn = 0;
        forX: for (int x = 0; x <= ceilRadiusX; ++x) 
        {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextZn = 0;
            forZ: for (int z = 0; z <= ceilRadiusZ; ++z) 
            {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = xn*xn + zn*zn;
                if (distanceSq > 1) 
                {
                    if (z == 0) {
                        break forX;
                    }
                    break forZ;
                }               
                
                totalHeight += world.getHighestBlockAt(bx + x, bz + z).getY();
                count++;
            }
        }
        
        return totalHeight / count;
	}
	
	public static void ResendChunksForNearbyPlayers(Collection<net.minecraft.server.v1_6_R2.Chunk> chunks)
	{
        for (net.minecraft.server.v1_6_R2.Chunk c : chunks)
        {
            
        	for (Player player : Bukkit.getOnlinePlayers())
        	{
        		Vector pV = player.getLocation().toVector();
        		int xDist = Math.abs((pV.getBlockX() >> 4) - c.x);
        		int zDist = Math.abs((pV.getBlockZ() >> 4) - c.z);
        		
        		if (xDist + zDist <= 12)
        		{        		
        			SendChunkForPlayer(c, player);
        		}
        	}
        }
	}
	
	public static net.minecraft.server.v1_6_R2.Chunk ChunkBlockChange(Location location, int id, byte data)
	{
    	net.minecraft.server.v1_6_R2.Chunk c = ((CraftChunk)location.getChunk()).getHandle();

		c.a(location.getBlockX() & 0xF, location.getBlockY(), location.getBlockZ() & 0xF, id, data);

		return c;
	}
		
	public static void SendChunkForPlayer(net.minecraft.server.v1_6_R2.Chunk chunk, Player player)
	{	
		SendChunkForPlayer(chunk.x, chunk.z, player);
	}
	
	@SuppressWarnings("unchecked")
	public static void SendChunkForPlayer(int x, int z, Player player)
	{
		((CraftPlayer)player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(x, z));
	}
	
	public static void UnloadWorld(JavaPlugin plugin, World world)
	{
		world.setAutoSave(false);
		
		for (Entity entity : world.getEntities())
		{
			entity.remove();
		}
		
		CraftServer server = (CraftServer)plugin.getServer();
		CraftWorld craftWorld = (CraftWorld)world;
	
        Bukkit.getPluginManager().callEvent(new WorldUnloadEvent(((CraftWorld)world).getHandle().getWorld()));
		
		Iterator<net.minecraft.server.v1_6_R2.Chunk> chunkIterator = ((CraftWorld)world).getHandle().chunkProviderServer.chunks.values().iterator();
		
		while (chunkIterator.hasNext())
		{
			net.minecraft.server.v1_6_R2.Chunk chunk = chunkIterator.next(); 
			chunk.removeEntities();
		}
		
		((CraftWorld)world).getHandle().chunkProviderServer.chunks.clear();
		((CraftWorld)world).getHandle().chunkProviderServer.unloadQueue.clear();
		
		try
		{
			Field f = server.getClass().getDeclaredField("worlds");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, World> worlds = (Map<String, World>)f.get(server);
			worlds.remove(world.getName().toLowerCase());
			f.setAccessible(false);
		}
		catch (IllegalAccessException ex)
		{
			System.out.println("Error removing world from bukkit master list: " + ex.getMessage());
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println("Error removing world from bukkit master list: " + ex.getMessage());
		}

		MinecraftServer ms = null;
		
		try
		{
			Field f = server.getClass().getDeclaredField("console");
			f.setAccessible(true);
			ms = (MinecraftServer)f.get(server);
			f.setAccessible(false);
		}
		catch (IllegalAccessException ex)
		{
			System.out.println("Error getting minecraftserver variable: " + ex.getMessage());
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println("Error getting minecraftserver variable: " + ex.getMessage());
		}
		
		ms.worlds.remove(ms.worlds.indexOf(craftWorld.getHandle()));
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static boolean ClearWorldReferences(String worldName)
	{
		HashMap regionfiles = null;
		Field rafField = null;
		
		try
		{
			Field a = net.minecraft.server.v1_6_R2.RegionFileCache.class.getDeclaredField("a");
			a.setAccessible(true);
			regionfiles = (HashMap) a.get(null);
			rafField = net.minecraft.server.v1_6_R2.RegionFile.class.getDeclaredField("c");
			rafField.setAccessible(true);
		}
		catch (Throwable t)
		{
			System.out.println("Error binding to region file cache.");
			t.printStackTrace();
		}
		
		if (regionfiles == null) return false;
		if (rafField == null) return false;

		ArrayList<Object> removedKeys = new ArrayList<Object>();
		try
		{
			for (Object o : regionfiles.entrySet())
			{
				Map.Entry e = (Map.Entry) o;
				File f = (File) e.getKey();

				if (f.toString().startsWith("." + File.separator + worldName))
				{
					RegionFile file = (RegionFile) e.getValue();
					try
					{
						RandomAccessFile raf = (RandomAccessFile) rafField.get(file);
						raf.close();
						removedKeys.add(f);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println("Exception while removing world reference for '" + worldName + "'!");
			ex.printStackTrace();
		}
		
		for (Object key : removedKeys)
			regionfiles.remove(key);

		return true;
	}
}
