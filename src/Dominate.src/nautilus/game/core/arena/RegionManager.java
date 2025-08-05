package nautilus.game.core.arena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RegionManager
{
  private String _dataFolder;
  private Map<String, Region> _regions;
  
  public RegionManager(String dataFolder)
  {
    this._dataFolder = dataFolder;
    this._regions = new HashMap();
  }
  
  public Region GetRegion(String name)
  {
    return (Region)this._regions.get(name);
  }
  
  public Boolean Contains(String name)
  {
    return Boolean.valueOf(this._regions.containsKey(name));
  }
  
  public List<Region> GetApplicableRegions(Vector v)
  {
    List<Region> applicableRegions = new ArrayList();
    
    for (Region region : this._regions.values())
    {
      if (region.Contains(v).booleanValue())
      {
        applicableRegions.add(region);
      }
    }
    
    return applicableRegions;
  }
  
  void SaveRegion(Region region)
  {
    FileOutputStream fOS = null;
    ObjectOutputStream obOut = null;
    
    File f = new File(this._dataFolder + "/regions/" + region.GetName() + ".region");
    File dir = new File(this._dataFolder + "/regions/");
    
    if (!dir.exists())
    {
      dir.mkdir();
    }
    
    try
    {
      if (f.exists())
      {
        f.delete();
      }
      
      if (!f.exists())
      {
        f.createNewFile();
      }
      
      fOS = new FileOutputStream(f);
      obOut = new ObjectOutputStream(fOS);
      obOut.writeObject(region);
      obOut.close();
    }
    catch (Exception e)
    {
      System.out.println("[ERROR] Failed to saving region - " + region.GetName() + " : " + e.getMessage());
    }
  }
  
  public void CreateNewRegion(Player player, String name, Vector pointOne, Vector pointTwo)
  {
    if (this._regions.get(name) != null)
    {
      player.sendMessage(ChatColor.DARK_RED + "That game region already exists.  Use update command.");
      return;
    }
    


    player.sendMessage(ChatColor.DARK_GREEN + "Successfully create game region - " + name + ".");
  }
  
  public void UpdateRegion(Player player, String name, Vector pointOne, Vector pointTwo)
  {
    if (this._regions.get(name) == null)
    {
      player.sendMessage(ChatColor.DARK_RED + "That game region does not exist.  Use create command.");
      return;
    }
    
    this._regions.remove(name);
    


    player.sendMessage(ChatColor.DARK_GREEN + "Successfully updated game region - " + name + ".");
  }
}
