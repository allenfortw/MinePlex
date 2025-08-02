package nautilus.game.capturethepig.arena;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import nautilus.game.capturethepig.arena.property.*;
import nautilus.game.core.arena.IArenaParser;
import nautilus.game.core.arena.property.*;

public class CaptureThePigArenaParser implements IArenaParser<ICaptureThePigArena> 
{
    private HashMap<String, IProperty<ICaptureThePigArena>> _properties;
    
    public CaptureThePigArenaParser()
    {
        _properties = new HashMap<String, IProperty<ICaptureThePigArena>>();
        
        AddProperty(new MapName<ICaptureThePigArena>());
        AddProperty(new BorderProperty<ICaptureThePigArena>());
        AddProperty(new Center<ICaptureThePigArena>());
        AddProperty(new Offset<ICaptureThePigArena>());
        AddProperty(new RedSpawnPoints<ICaptureThePigArena>());
        AddProperty(new RedSpawnRoom<ICaptureThePigArena>());
        AddProperty(new BlueSpawnPoints<ICaptureThePigArena>());
        AddProperty(new BlueSpawnRoom<ICaptureThePigArena>());
        
        AddProperty(new RedPigPen());
        AddProperty(new BluePigPen());
        AddProperty(new PigSpawnLocation());
    }
    
	@Override
	public ICaptureThePigArena Parse(String worldPath, FileReader fileReader) 
	{
        try
        {
        	ICaptureThePigArena arena = new CaptureThePigArena(worldPath.substring(worldPath.lastIndexOf(File.separator) + 1, worldPath.length()));
            
            BufferedReader input =  new BufferedReader(fileReader);
            String line = input.readLine();
                    
            while(line != null)
            {               
                if(line.startsWith("#"))
                {
                    line = input.readLine();
                    continue;
                }
                
                String[] parts = line.split(":");
                
                if(parts.length < 2)
                {
                    line = input.readLine();
                    continue;
                }
                
                String key = parts[0];
                String value = parts[1].toLowerCase().trim();                           
                
                if (_properties.containsKey(key.toLowerCase()))
                {
                    _properties.get(key.toLowerCase()).Parse(arena, value);
                }
                else
                {
                    System.out.println("Invalid property found: " + key);
                }
                
                line = input.readLine();
            }
            
            return arena;
        }
        catch(Exception ex)
        {
            System.out.println("An exception was thrown:" + ex.getMessage());
        }
        
        return null;
	}
	
    private void AddProperty(IProperty<ICaptureThePigArena> property)
    {
        _properties.put(property.GetName(), property);
    }
}
