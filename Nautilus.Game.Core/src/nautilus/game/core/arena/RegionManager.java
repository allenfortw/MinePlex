package nautilus.game.core.arena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
	    _dataFolder = dataFolder;
		_regions = new HashMap<String, Region>();
	}
	
	public Region GetRegion(String name)
	{
		return _regions.get(name);
	}
	
	public Boolean Contains(String name)
	{
		return _regions.containsKey(name);
	}
	
	public List<Region> GetApplicableRegions(Vector v)
	{
		List<Region> applicableRegions = new ArrayList<Region>();
		
		for (Region region : _regions.values())
		{
			if (region.Contains(v))
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
	    
        File f = new File(_dataFolder + "/regions/" + region.GetName() + ".region");
        File dir = new File(_dataFolder + "/regions/");
        
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
		if (_regions.get(name) != null)
		{
			player.sendMessage(ChatColor.DARK_RED + "That game region already exists.  Use update command.");
			return;
		}
		
		//CreateRegion(new Region(this, name, pointOne, pointTwo));
		
		player.sendMessage(ChatColor.DARK_GREEN + "Successfully create game region - " + name + ".");
	}
	
	public void UpdateRegion(Player player, String name, Vector pointOne, Vector pointTwo)
	{
		if (_regions.get(name) == null)
		{
			player.sendMessage(ChatColor.DARK_RED + "That game region does not exist.  Use create command.");
			return;
		}
		
		_regions.remove(name);

		//CreateRegion(new Region(this, name, pointOne, pointTwo));
		
		player.sendMessage(ChatColor.DARK_GREEN + "Successfully updated game region - " + name + ".");
	}
}