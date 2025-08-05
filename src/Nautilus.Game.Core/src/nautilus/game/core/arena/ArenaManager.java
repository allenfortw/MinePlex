package nautilus.game.core.arena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mineplex.core.common.util.Callback;
import mineplex.core.common.util.FileUtil;
import mineplex.core.common.util.MapUtil;
import nautilus.minecraft.core.utils.ZipUtil;
import net.minecraft.server.v1_6_R2.ChunkPreLoadEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaManager<ArenaType extends IArena> implements Listener, Runnable
{
	private JavaPlugin _plugin;
	private String _gamemode;
	private IArenaParser<ArenaType> _arenaParser;
	private HashMap<ArenaType, String> _arenaWorldFolderMap;
	private List<String> _availableArenas;
	private HashMap<ArenaType, Callback<ArenaType>> _loadingArenas;
	private HashMap<ArenaType, Callback<ArenaType>> _delayArenaCallback;
	
	private Random _random;
	private int _arenaCount;
	
	private HashMap<String, ArenaType> _chunkWorldRegionMap;
	
	public ArenaManager(JavaPlugin plugin, String gamemode, IArenaParser<ArenaType> arenaParser)
	{
		_plugin = plugin;
		_gamemode = gamemode;
		_arenaParser = arenaParser;
		_availableArenas = new ArrayList<String>();
		_loadingArenas = new HashMap<ArenaType, Callback<ArenaType>>();
		_delayArenaCallback = new HashMap<ArenaType, Callback<ArenaType>>();
		_chunkWorldRegionMap = new HashMap<String, ArenaType>();
		_arenaWorldFolderMap = new HashMap<ArenaType, String>();
		_random = new Random();
		
		_plugin.getServer().getPluginManager().registerEvents(this, _plugin);
		_plugin.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, this, 1, 1);
		PopulateAvailableArenas();
	}

	@EventHandler
	public void OnChunkLoad(ChunkPreLoadEvent event)
	{
		String key = event.GetWorld().getName();
		
		if (_chunkWorldRegionMap.containsKey(key))
		{
			ArenaType arena = _chunkWorldRegionMap.get(key);
			
			if (!arena.IsChunkInArena(event.GetX(), event.GetZ()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	public void RemoveArena(ArenaType arena)
	{
	    if (_arenaWorldFolderMap.containsKey(arena))
	    {
	    	long arenaRemovalTime = System.currentTimeMillis();
	    	MapUtil.UnloadWorld(_plugin, arena.GetWorld());
	    	MapUtil.ClearWorldReferences(arena.GetWorld().getName());
	    	
	    	FileUtil.DeleteFolder(arena.GetWorld().getWorldFolder());
	    	
	    	_arenaWorldFolderMap.remove(arena);
	    	_chunkWorldRegionMap.remove(arena.GetWorld().getName());	    	
	    	
	    	System.out.println("Removed world '" + arena.GetWorld().getName() + "' in " + (System.currentTimeMillis() - arenaRemovalTime) + "ms");
	    	arena.Deactivate();
	    }
	    else
	    {
	    	System.out.println("not in arenaWorldFolderMap");
	    }
	}
	
	public void GetNextArena(Callback<ArenaType> callback)
	{
        String key = _availableArenas.get(_random.nextInt(_availableArenas.size()));
        
    	String directory = _plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile() + File.separator;
    	String name = directory + _arenaCount;

    	new File(name).mkdir();
    	new File(name + File.separator + "region").mkdir();
    	
    	// TODO Queue/Optimize unzip - currently 50ms on production server.
    	ZipUtil.UnzipToDirectory(key, name);
    	
    	ArenaType arena = null;
    	
		try 
		{
			FileReader configReader = new FileReader(new File(name + File.separator + "arena.config").getAbsolutePath());
			arena = _arenaParser.Parse(name, configReader);
			configReader.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
    	_arenaWorldFolderMap.put(arena, key);

    	_loadingArenas.put(arena, callback);
    	_arenaCount++;
	}
	
	private void PopulateAvailableArenas() 
	{
	    if(!_plugin.getDataFolder().exists()) 
	    {
	    	_plugin.getDataFolder().mkdir();
	    }
	    
		File dominateArenaDir = new File(_plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getParentFile() + File.separator + "Arenas" + File.separator + _gamemode);
		
	    if(!dominateArenaDir.getAbsoluteFile().getParentFile().exists()) 
	    {
	    	dominateArenaDir.getAbsoluteFile().getParentFile().mkdir();
	    }
		
	    if(!dominateArenaDir.exists()) 
	    {
	    	dominateArenaDir.mkdir();
	    }
	    
	    FilenameFilter statsFilter = new FilenameFilter() 
	    {
            public boolean accept(File paramFile, String paramString) 
            {
                if (paramString.endsWith("zip"))
                {
                    return true;
                }
                
                return false;
            }
	    };
	    
	    for (File f : dominateArenaDir.listFiles(statsFilter))
	    {
	    	_availableArenas.add(f.getAbsolutePath());
	    	System.out.println("Adding arena zip file : " + f);
	    }
	}
	
	public void run()
	{
		Iterator<ArenaType> arenaIterator = _delayArenaCallback.keySet().iterator();
		
		while (arenaIterator.hasNext())
		{
			ArenaType arena = arenaIterator.next();
			
			_delayArenaCallback.get(arena).run(arena);
			arenaIterator.remove();
		}
		
		long endTime = System.currentTimeMillis() + 25;
		long timeLeft;
		arenaIterator = _loadingArenas.keySet().iterator();
		
		while (arenaIterator.hasNext())
		{
			ArenaType arena = arenaIterator.next();
			
			timeLeft = endTime - System.currentTimeMillis();
			if (timeLeft > 0)
			{
				if (arena.LoadArena(timeLeft))
				{
					_chunkWorldRegionMap.put(arena.GetWorld().getName(), arena);
					_delayArenaCallback.put(arena, _loadingArenas.get(arena));					
					arenaIterator.remove();
				}
			}
			else
				break;
		}
	}

    public boolean HasAvailableArena()
    {
    	return _availableArenas.size() > 0;
    }
}
