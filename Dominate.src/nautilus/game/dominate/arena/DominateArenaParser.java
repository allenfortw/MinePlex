package nautilus.game.dominate.arena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import nautilus.game.core.arena.property.BlueShopPoints;
import nautilus.game.core.arena.property.BlueSpawnPoints;
import nautilus.game.core.arena.property.BlueSpawnRoom;
import nautilus.game.core.arena.property.BorderProperty;
import nautilus.game.core.arena.property.Center;
import nautilus.game.core.arena.property.IProperty;
import nautilus.game.core.arena.property.MapName;
import nautilus.game.core.arena.property.Offset;
import nautilus.game.core.arena.property.RedShopPoints;
import nautilus.game.core.arena.property.RedSpawnPoints;
import nautilus.game.core.arena.property.RedSpawnRoom;
import nautilus.game.dominate.arena.property.ControlPointAreas;
import nautilus.game.dominate.arena.property.PointPowerUps;
import nautilus.game.dominate.arena.property.ResupplyPowerUps;

public class DominateArenaParser implements IDominateArenaParser
{
  private HashMap<String, IProperty<IDominateArena>> _properties;
  
  public DominateArenaParser()
  {
    this._properties = new HashMap();
    
    AddProperty(new MapName());
    AddProperty(new BorderProperty());
    AddProperty(new Center());
    AddProperty(new Offset());
    AddProperty(new RedSpawnPoints());
    AddProperty(new RedSpawnRoom());
    AddProperty(new RedShopPoints());
    AddProperty(new BlueSpawnPoints());
    AddProperty(new BlueSpawnRoom());
    AddProperty(new BlueShopPoints());
    
    AddProperty(new ControlPointAreas());
    AddProperty(new PointPowerUps());
    AddProperty(new ResupplyPowerUps());
  }
  

  public IDominateArena Parse(String worldPath, FileReader fileReader)
  {
    try
    {
      IDominateArena arena = new DominateArena(worldPath.substring(worldPath.lastIndexOf(File.separator) + 1, worldPath.length()));
      
      BufferedReader input = new BufferedReader(fileReader);
      String line = input.readLine();
      
      while (line != null)
      {
        if (line.startsWith("#"))
        {
          line = input.readLine();
        }
        else
        {
          String[] parts = line.split(":");
          
          if (parts.length < 2)
          {
            line = input.readLine();
          }
          else
          {
            String key = parts[0];
            String value = parts[1].trim();
            
            if (this._properties.containsKey(key.toLowerCase()))
            {
              ((IProperty)this._properties.get(key.toLowerCase())).Parse(arena, value);
            }
            else
            {
              System.out.println("Invalid property found: " + key);
            }
            
            line = input.readLine();
          }
        } }
      return arena;
    }
    catch (Exception ex)
    {
      System.out.println("An exception was thrown:" + ex.getMessage());
    }
    
    return null;
  }
  
  private void AddProperty(IProperty<IDominateArena> property)
  {
    this._properties.put(property.GetName(), property);
  }
}
