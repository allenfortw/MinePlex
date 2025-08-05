package nautilus.game.core.arena.property;

import org.bukkit.Location;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.Region;

public class BlueSpawnRoom<ArenaType extends ITeamArena> extends RegionPropertyBase<ArenaType>
{
    public BlueSpawnRoom()
    {
        super("bluespawnroom");
    }

    @Override
    public boolean Parse(ArenaType arena, String value)
    {
        arena.SetBlueSpawnRoom(ParseRegion(value));
        return true;
    }
    
    public boolean Parse(ArenaType arena, Location start, Location stop) 
    {
        arena.SetBlueSpawnRoom(new Region("bluespawnroom", start.toVector(), stop.toVector()));
        
        return true;
    }
}
