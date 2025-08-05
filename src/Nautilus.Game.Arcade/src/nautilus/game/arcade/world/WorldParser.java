package nautilus.game.arcade.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

public class WorldParser
{
	public void Parse(Player caller, String[] args) 
	{
		HashSet<Integer> dataId = new HashSet<Integer>();
	
		if (args != null)
			for (String arg : args)
			{
				try
				{
					dataId.add(Integer.parseInt(arg));
				}
				catch (Exception e)
				{
					caller.sendMessage("Invalid Data ID: " + arg);
				}
			}
		
		HashMap<String, ArrayList<Location>> TeamLocs = new HashMap<String, ArrayList<Location>>();
		HashMap<String, ArrayList<Location>> DataLocs = new HashMap<String, ArrayList<Location>>();
		HashMap<String, ArrayList<Location>> CustomLocs = new HashMap<String, ArrayList<Location>>();
		
		Location cornerA = null;
		Location cornerB = null;

		int processed = 0;

		caller.sendMessage("Scanning for Blocks...");
		for (int x=-500 ; x < 500 ; x++)
			for (int z=-500 ; z < 500 ; z++)
				for (int y=0 ; y < 256 ; y++)
				{
					processed++;
					if (processed % 20000000 == 0)
						caller.sendMessage("Processed: " + processed);

					Block block = caller.getWorld().getBlockAt(caller.getLocation().getBlockX()+x, caller.getLocation().getBlockY()+y, caller.getLocation().getBlockZ()+z);

					//ID DATA
					if (dataId.contains(block.getTypeId()))
					{
						String key = ""+block.getTypeId();
						
						if (!CustomLocs.containsKey(key))
							CustomLocs.put(key, new ArrayList<Location>());
						
						CustomLocs.get(key).add(block.getLocation());
						continue;
					}
					
					//Sign Post > Spawns
					if (block.getTypeId() == 147)
					{
						Block wool = block.getRelative(BlockFace.DOWN);
						if (wool == null)
							continue;

						if (wool.getType() == Material.WOOL)
						{
							if (wool.getData() == 14)	//RED > Spawn
							{
								if (!TeamLocs.containsKey("Red"))
									TeamLocs.put("Red", new ArrayList<Location>());
								
								TeamLocs.get("Red").add(wool.getLocation());
								
								//Remove Blocks
								block.setTypeId(0);
								wool.setTypeId(0);
							}
							
							if (wool.getData() == 4)	//RED > Spawn
							{
								if (!TeamLocs.containsKey("Yellow"))
									TeamLocs.put("Yellow", new ArrayList<Location>());
								
								TeamLocs.get("Yellow").add(wool.getLocation());
								
								//Remove Blocks
								block.setTypeId(0);
								wool.setTypeId(0);
							}
							
							if (wool.getData() == 13)	//RED > Spawn
							{
								if (!TeamLocs.containsKey("Green"))
									TeamLocs.put("Green", new ArrayList<Location>());
								
								TeamLocs.get("Green").add(wool.getLocation());
								
								//Remove Blocks
								block.setTypeId(0);
								wool.setTypeId(0);
							}
							
							if (wool.getData() == 11)	//RED > Spawn
							{
								if (!TeamLocs.containsKey("Blue"))
									TeamLocs.put("Blue", new ArrayList<Location>());
								
								TeamLocs.get("Blue").add(wool.getLocation());
								
								//Remove Blocks
								block.setTypeId(0);
								wool.setTypeId(0);
							}
							
							if (wool.getData() == 0)	//WHITE > Corner
							{
								if (cornerA == null)		cornerA = wool.getLocation();
								else if (cornerB == null)	cornerB = wool.getLocation();
								else						caller.sendMessage("More than 2 Corner Locations found!");
								
								//Remove Blocks
								block.setTypeId(0);
								wool.setTypeId(0);
							}
						}
					}

					if (block.getTypeId() != 148)
						continue;

					Block wool = block.getRelative(BlockFace.DOWN);
					if (wool == null)
						continue;

					if (wool.getType() != Material.WOOL)
						continue;
					
					Wool woolData = new Wool(wool.getType(), wool.getData());
					
					String dataType = woolData.getColor().name();

					if (!DataLocs.containsKey(dataType))
						DataLocs.put(dataType, new ArrayList<Location>());
					
					DataLocs.get(dataType).add(wool.getLocation());
					
					//Remove Blocks
					block.setTypeId(0);
					wool.setTypeId(0);
				}

		if (cornerA == null || cornerB == null)
		{
			caller.sendMessage("Missing Corner Locations!");
			return;
		}

		//Save
		try
		{
			FileWriter fstream = new FileWriter(caller.getWorld().getName() + File.separator + "WorldConfig.dat");
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("MAP_NAME:");
			out.write("\n");
			out.write("MAP_AUTHOR:");
			out.write("\n");
			out.write("\n");
			out.write("MIN_X:"+Math.min(cornerA.getBlockX(), cornerB.getBlockX()));
			out.write("\n");
			out.write("MAX_X:"+Math.max(cornerA.getBlockX(), cornerB.getBlockX()));
			out.write("\n");
			out.write("MIN_Z:"+Math.min(cornerA.getBlockZ(), cornerB.getBlockZ()));
			out.write("\n");
			out.write("MAX_Z:"+Math.max(cornerA.getBlockZ(), cornerB.getBlockZ()));
			
			//Teams
			for (String team : TeamLocs.keySet())
			{
				out.write("\n");
				out.write("\n");
				out.write("TEAM_NAME:" + team);
				out.write("\n");
				out.write("TEAM_SPAWNS:" + LocationsToString(TeamLocs.get(team)));		
			}
			
			//Data
			for (String data : DataLocs.keySet())
			{
				out.write("\n");
				out.write("\n");
				out.write("DATA_NAME:" + data);
				out.write("\n");
				out.write("DATA_LOCS:" + LocationsToString(DataLocs.get(data)));
			}

			//Custom
			for (String data : CustomLocs.keySet())
			{
				out.write("\n");
				out.write("\n");
				out.write("CUSTOM_NAME:" + data);
				out.write("\n");
				out.write("CUSTOM_LOCS:" + LocationsToString(CustomLocs.get(data)));
			}
			
			out.close();
		}
		catch (Exception e)
		{
			caller.sendMessage("Error: File Write Error");
		}


		caller.sendMessage("World Data Saved.");
	}	

	public String LocationsToString(ArrayList<Location> locs)
	{
		String out = "";

		for (Location loc : locs)
			out += loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ":";

		return out;
	}
	
	public String LocationSignsToString(HashMap<Location, String> locs)
	{
		String out = "";

		for (Location loc : locs.keySet())
			out += locs.get(loc) + "@" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ":";

		return out;
	}
}

