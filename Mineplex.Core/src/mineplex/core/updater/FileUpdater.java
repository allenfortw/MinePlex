package mineplex.core.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.portal.Portal;
import mineplex.core.updater.event.RestartServerEvent;
import mineplex.core.updater.event.UpdateEvent;

public class FileUpdater extends MiniPlugin
{
	private Portal _portal;
	private NautHashMap<String, String> _jarMd5Map = new NautHashMap<String, String>();
	
	private boolean _needUpdate;
	private boolean _enabled = true;
	
	public FileUpdater(JavaPlugin plugin, Portal portal)
	{
		super("File Updater", plugin);
		
		_portal = portal;
		
		GetPluginMd5s();
		
		if (new File("IgnoreUpdates.dat").exists())
			_enabled = false;
	}
	
	@EventHandler
	public void tryToRestart(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOWER)
			return;
		
		if (!_needUpdate || !_enabled)
			return;
		
		RestartServerEvent restartEvent = new RestartServerEvent();
		
		GetPluginManager().callEvent(restartEvent);
		
		if (!restartEvent.isCancelled())
		{
			GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
			{
				public void run()
				{
					_portal.SendAllPlayers("Lobby");
				}
			}, 60L);
			
			GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
			{
				public void run()
				{
					GetPlugin().getServer().shutdown();
				}
			}, 100L);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void reflectMotd(ServerListPingEvent event)
	{
		if (_needUpdate)
		{
			event.setMotd("Restarting soon");
		}
	}
	
	@EventHandler
	public void CheckForNewFiles(UpdateEvent event)
	{
		if (event.getType() != UpdateType.MIN_01)
			return;
		
		if (_needUpdate || !_enabled)
			return;
		
		boolean windows = System.getProperty("os.name").startsWith("Windows");
		
		File updateDir = new File((windows ? "C:" : File.separator + "home" + File.separator + "mineplex") + File.separator + "update");
		
		updateDir.mkdirs();
	    
	    FilenameFilter statsFilter = new FilenameFilter() 
	    {
            public boolean accept(File paramFile, String paramString) 
            {
                if (paramString.endsWith("jar"))
                {
                    return true;
                }
                
                return false;
            }
	    };
	    
	    for (File f : updateDir.listFiles(statsFilter))
	    {
	    	FileInputStream fis = null;
	    	
	    	try
	    	{
	    		if (_jarMd5Map.containsKey(f.getName()))
	    		{
	    			fis = new FileInputStream(f);
	    			String md5 = DigestUtils.md5Hex(fis);
	    			
	    			if (!md5.equals(_jarMd5Map.get(f.getName())))
	    			{
	    				System.out.println(f.getName() + " old jar : " + _jarMd5Map.get(f.getName()));
	    				System.out.println(f.getName() + " new jar : " + md5);
	    				_needUpdate = true;
	    			}
	    		}
	    	}
	    	catch (Exception ex)
	    	{
	    		System.out.println(F.main(GetName(), "Error parsing jar md5's"));
	    		ex.printStackTrace();
	    	}
	    	finally
	    	{
	    		if (fis != null)
	    		{
					try
					{
						fis.close();
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}		
	    		}
	    	}
	    }
	}
	
	private void GetPluginMd5s()
	{
		File pluginDir = new File("plugins");
		
		pluginDir.mkdirs();
	    
	    FilenameFilter statsFilter = new FilenameFilter() 
	    {
            public boolean accept(File paramFile, String paramString) 
            {
                if (paramString.endsWith("jar"))
                {
                    return true;
                }
                
                return false;
            }
	    };
	    
	    for (File f : pluginDir.listFiles(statsFilter))
	    {
	    	FileInputStream fis = null;
	    	
	    	try
	    	{
	    		fis = new FileInputStream(f);
	    		_jarMd5Map.put(f.getName(), DigestUtils.md5Hex(fis));
	    	}
	    	catch (Exception ex)
	    	{
	    		System.out.println(F.main(GetName(), "Error parsing jar md5's"));
	    		ex.printStackTrace();
	    	}
	    	finally
	    	{
	    		if (fis != null)
	    		{
					try
					{
						fis.close();
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}		
	    		}
	    	}
	    }
	}
}
