package mineplex.core.updater;

import mineplex.core.updater.event.UpdateEvent;

import org.bukkit.plugin.java.JavaPlugin;

public class Updater implements Runnable 
{
	private JavaPlugin _plugin;
	
	public Updater(JavaPlugin plugin)
	{
		_plugin = plugin;
		_plugin.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, this, 0L, 1L);
	}
	
	@Override
	public void run() 
	{
		for (UpdateType updateType : UpdateType.values())
		{
			if (updateType.Elapsed())
			{
				//long startTime = System.currentTimeMillis();
				_plugin.getServer().getPluginManager().callEvent(new UpdateEvent(updateType));
				//System.out.println("UpdateType " + updateType.name() + " : " + (System.currentTimeMillis() - startTime) + "ms");
			}
		}
	}
}
