package me.chiss.Core.MemoryFix;

import java.util.Iterator;

import net.minecraft.server.v1_6_R2.IInventory;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class MemoryFix
{
	private JavaPlugin _plugin;
	
	public MemoryFix(JavaPlugin plugin)
	{
		_plugin = plugin;
		
		_plugin.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable()
		{
			public void run()
			{
				for (World world : Bukkit.getWorlds())
				{
					for (Object tileEntity : ((CraftWorld)world).getHandle().tileEntityList)
					{
						if (tileEntity instanceof IInventory)
						{
							Iterator<HumanEntity> entityIterator = ((IInventory)tileEntity).getViewers().iterator();
							
							while (entityIterator.hasNext())
							{
								HumanEntity entity = entityIterator.next();
								
								if (entity instanceof CraftPlayer && !((CraftPlayer)entity).isOnline())
								{
									entityIterator.remove();
								}
							}
						}
					}
				}
			}
		}, 100L, 100L);
	}
}
