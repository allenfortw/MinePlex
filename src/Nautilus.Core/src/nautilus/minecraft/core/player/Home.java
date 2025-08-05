package nautilus.minecraft.core.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;

public class Home 
{
	private String _name;
	private Location _location;
	
	public Home(String name, Location location)
	{
		_name = name;
		_location = location;
	}
	
	public Home(String name)
	{
		_name = name;
	}
	
	public String GetName()
	{
		return _name;
	}
	
	public Location GetLocation()
	{
		return _location;
	}
	
	public void Save()
	{
		BufferedWriter fout = null;
		
		try
		{
			StringBuilder packedContents = new StringBuilder();
			
			packedContents.append("home:");
			packedContents.append(_location.getX() + ":" + _location.getY() + ":" + _location.getZ());
			packedContents.append(":" + _location.getYaw() + ":" + _location.getPitch());
			packedContents.append(":" + (_location.getWorld().getEnvironment() == Environment.NORMAL ? "world" : "nether") + "\n");
			
			fout = new BufferedWriter(new FileWriter("homes\\" + _name + ".txt"));
			
			fout.write(packedContents.toString());	
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.close();			
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void Load()
	{
		BufferedReader fileIn = null;
		
		try
		{
			fileIn = new BufferedReader(new FileReader("homes\\" + _name + ".txt"));
			
	        String line = fileIn.readLine();
	        
			line = line.substring(line.indexOf(':') + 1);
				
			_location = ParseLocation(line);
				
			fileIn.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (fileIn != null)
			{
				try
				{
					fileIn.close();			
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Location ParseLocation(String line)
	{
		try
		{
			String [] parts = line.split(":");
			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			double z = Double.parseDouble(parts[2]); 
			float rotX = 0;
			float rotY = 0;
			org.bukkit.World world;
			
			if (parts.length > 3)
			{
				rotX = Float.parseFloat(parts[3]);
				rotY = Float.parseFloat(parts[4]);
			}			
						
			List<World> worlds = Bukkit.getServer().getWorlds();
			World normalWorld = worlds.get(0);
			World netherWorld = worlds.get(0);
			
			for (World serverWorld : worlds)
			{
				if (serverWorld.getEnvironment() == Environment.NORMAL)
					normalWorld = serverWorld;
				else if (serverWorld.getEnvironment() == Environment.NETHER)
					netherWorld = serverWorld;
			}
			
			
			if(parts.length == 6 && parts[5].contains("nether"))
			{
				world = netherWorld;				
			}
			else
			{
				world = normalWorld;
			}
			
			return new Location(world, x, y, z, rotX, rotY);
		}
		catch(NumberFormatException ex)
		{
			System.out.println("Can't parse location for line:" + line);
			// Gulp, that was good.
			return null;			
		}
	}
}
