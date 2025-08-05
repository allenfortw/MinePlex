package nautilus.game.core.arena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.FileUtil;
import mineplex.core.common.util.MapUtil;
import nautilus.minecraft.core.utils.ZipUtil;
import net.minecraft.server.v1_6_R3.ChunkPreLoadEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
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
    this._plugin = plugin;
    this._gamemode = gamemode;
    this._arenaParser = arenaParser;
    this._availableArenas = new java.util.ArrayList();
    this._loadingArenas = new HashMap();
    this._delayArenaCallback = new HashMap();
    this._chunkWorldRegionMap = new HashMap();
    this._arenaWorldFolderMap = new HashMap();
    this._random = new Random();
    
    this._plugin.getServer().getPluginManager().registerEvents(this, this._plugin);
    this._plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this._plugin, this, 1L, 1L);
    PopulateAvailableArenas();
  }
  
  @EventHandler
  public void OnChunkLoad(ChunkPreLoadEvent event)
  {
    String key = event.GetWorld().getName();
    
    if (this._chunkWorldRegionMap.containsKey(key))
    {
      ArenaType arena = (IArena)this._chunkWorldRegionMap.get(key);
      
      if (!arena.IsChunkInArena(event.GetX(), event.GetZ()))
      {
        event.setCancelled(true);
      }
    }
  }
  
  public void RemoveArena(ArenaType arena)
  {
    if (this._arenaWorldFolderMap.containsKey(arena))
    {
      long arenaRemovalTime = System.currentTimeMillis();
      MapUtil.UnloadWorld(this._plugin, arena.GetWorld());
      MapUtil.ClearWorldReferences(arena.GetWorld().getName());
      
      FileUtil.DeleteFolder(arena.GetWorld().getWorldFolder());
      
      this._arenaWorldFolderMap.remove(arena);
      this._chunkWorldRegionMap.remove(arena.GetWorld().getName());
      
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
    String key = (String)this._availableArenas.get(this._random.nextInt(this._availableArenas.size()));
    
    String directory = this._plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile() + File.separator;
    String name = directory + this._arenaCount;
    
    new File(name).mkdir();
    

    ZipUtil.UnzipToDirectory(key, name);
    
    ArenaType arena = null;
    
    try
    {
      FileReader configReader = new FileReader(new File(name + File.separator + "arena.config").getAbsolutePath());
      arena = this._arenaParser.Parse(name, configReader);
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
    
    this._arenaWorldFolderMap.put(arena, key);
    
    this._loadingArenas.put(arena, callback);
    this._arenaCount += 1;
  }
  
  private void PopulateAvailableArenas()
  {
    if (!this._plugin.getDataFolder().exists())
    {
      this._plugin.getDataFolder().mkdir();
    }
    
    File dominateArenaDir = new File(this._plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getParentFile() + File.separator + "Arenas" + File.separator + this._gamemode);
    
    if (!dominateArenaDir.getAbsoluteFile().getParentFile().exists())
    {
      dominateArenaDir.getAbsoluteFile().getParentFile().mkdir();
    }
    
    if (!dominateArenaDir.exists())
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
      this._availableArenas.add(f.getAbsolutePath());
      System.out.println("Adding arena zip file : " + f);
    }
  }
  
  public void run()
  {
    Iterator<ArenaType> arenaIterator = this._delayArenaCallback.keySet().iterator();
    
    while (arenaIterator.hasNext())
    {
      ArenaType arena = (IArena)arenaIterator.next();
      
      ((Callback)this._delayArenaCallback.get(arena)).run(arena);
      arenaIterator.remove();
    }
    
    long endTime = System.currentTimeMillis() + 25L;
    
    arenaIterator = this._loadingArenas.keySet().iterator();
    
    while (arenaIterator.hasNext())
    {
      ArenaType arena = (IArena)arenaIterator.next();
      
      long timeLeft = endTime - System.currentTimeMillis();
      if (timeLeft <= 0L)
        break;
      if (arena.LoadArena(timeLeft))
      {
        this._chunkWorldRegionMap.put(arena.GetWorld().getName(), arena);
        this._delayArenaCallback.put(arena, (Callback)this._loadingArenas.get(arena));
        arenaIterator.remove();
      }
    }
  }
  



  public boolean HasAvailableArena()
  {
    return this._availableArenas.size() > 0;
  }
}
