package mineplex.core.common.util;

import java.io.File;

import net.minecraft.server.v1_6_R2.ConvertProgressUpdater;
import net.minecraft.server.v1_6_R2.Convertable;
import net.minecraft.server.v1_6_R2.EntityTracker;
import net.minecraft.server.v1_6_R2.EnumGamemode;
import net.minecraft.server.v1_6_R2.IWorldAccess;
import net.minecraft.server.v1_6_R2.ServerNBTManager;
import net.minecraft.server.v1_6_R2.WorldLoaderServer;
import net.minecraft.server.v1_6_R2.WorldManager;
import net.minecraft.server.v1_6_R2.WorldServer;
import net.minecraft.server.v1_6_R2.WorldSettings;
import net.minecraft.server.v1_6_R2.WorldType;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

public class WorldUtil 
{
	public static World LoadWorld(WorldCreator creator)
	{
		CraftServer server = (CraftServer)Bukkit.getServer();
        if (creator == null) 
        {
            throw new IllegalArgumentException("Creator may not be null");
        }

        String name = creator.name();
        System.out.println("Loading world '" + name + "'");
        ChunkGenerator generator = creator.generator();
        File folder = new File(server.getWorldContainer(), name);
        World world = server.getWorld(name);
        WorldType type = WorldType.getType(creator.type().getName());
        boolean generateStructures = creator.generateStructures();

        if (world != null) 
        {
            return world;
        }

        if ((folder.exists()) && (!folder.isDirectory())) 
        {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        if (generator == null) 
        {
            generator = server.getGenerator(name);
        }

        Convertable converter = new WorldLoaderServer(server.getWorldContainer());
        if (converter.isConvertable(name)) 
        {
        	server.getLogger().info("Converting world '" + name + "'");
            converter.convert(name, new ConvertProgressUpdater(server.getServer()));
        }

        int dimension = 10 + server.getServer().worlds.size();
        boolean used = false;
        do 
        {
            for (WorldServer worldServer : server.getServer().worlds) 
            {
                used = worldServer.dimension == dimension;
                if (used) 
                {
                    dimension++;
                    break;
                }
            }
        } while(used);
        boolean hardcore = false;

        WorldServer internal = new WorldServer(server.getServer(), new ServerNBTManager(server.getWorldContainer(), name, true), name, dimension, new WorldSettings(creator.seed(), EnumGamemode.a(server.getDefaultGameMode().getValue()), generateStructures, hardcore, type), server.getServer().methodProfiler, server.getServer().getLogger(), creator.environment(), generator);
        
        boolean containsWorld = false;
        for (World otherWorld : server.getWorlds()) 
        {
        	if (otherWorld.getName().equalsIgnoreCase(name.toLowerCase()))
        	{
        		containsWorld = true;
        		break;
        	}
        }
        
        if (!containsWorld)
        	return null;

        internal.worldMaps = server.getServer().worlds.get(0).worldMaps;
        internal.tracker = new EntityTracker(internal); // CraftBukkit
        internal.addIWorldAccess((IWorldAccess) new WorldManager(server.getServer(), internal));
        internal.difficulty = 1;
        internal.setSpawnFlags(true, true);
        server.getServer().worlds.add(internal);

        if (generator != null) 
        {
            internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
        }

        server.getPluginManager().callEvent(new WorldInitEvent(internal.getWorld()));
        server.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
        
        return internal.getWorld();
	}
}
