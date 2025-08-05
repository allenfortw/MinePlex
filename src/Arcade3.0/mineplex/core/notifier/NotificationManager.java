package mineplex.core.notifier;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.updater.event.UpdateEvent;

public class NotificationManager extends MiniPlugin
{
	private boolean _enabled = true;
	
	public NotificationManager(JavaPlugin plugin, CoreClientManager client) 
	{
		super("Notification Manager", plugin);
	}
	
	@EventHandler
	public void notify(UpdateEvent event)
	{
		if (!_enabled)
			return;
		
//		if (event.getType() == UpdateType.MIN_08)
//			hugeSale();
		
//		if (event.getType() == UpdateType.MIN_16)
//			sale();
	}
}
