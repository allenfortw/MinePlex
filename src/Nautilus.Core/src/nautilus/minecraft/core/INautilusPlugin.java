package nautilus.minecraft.core;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public interface INautilusPlugin
{
	JavaPlugin GetPlugin();
	
	String GetWebServerAddress();
	
	Server GetRealServer();
	
	PluginManager GetPluginManager();
}
