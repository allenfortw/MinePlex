package nautilus.minecraft.core.player;

import java.io.File;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class HomeManager 
{
	private HashMap<String, Home> _playerHomes;

	public HomeManager()
	{
		_playerHomes = new HashMap<String, Home>();
		LoadHomes();
	}
	
	public void SetHome(Player player)
	{
		String homeName = player.getName().toLowerCase();
		Home playerHome = new Home(homeName, player.getLocation());
		
		_playerHomes.put(homeName, playerHome);
		playerHome.Save();
	}
	
	public Home GetHome(Player player)
	{
		return _playerHomes.get(player.getName().toLowerCase());
	}
	
	public void LoadHomes()
	{
		File homeDir = new File("homes/");
		
		if (!homeDir.exists())
		{
			homeDir.mkdir();
		}
	        	  
		for (File f : homeDir.listFiles())
	    {
			String fileName = f.getName();		
			System.out.println(fileName);
			Home home = new Home(fileName.substring(0, fileName.indexOf('.')));
			
			home.Load();
			
			_playerHomes.put(home.GetName(), home);
	    }
	}
}
