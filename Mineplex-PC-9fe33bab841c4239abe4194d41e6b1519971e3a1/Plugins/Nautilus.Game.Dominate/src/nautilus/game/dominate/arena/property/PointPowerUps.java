package nautilus.game.dominate.arena.property;

import org.bukkit.entity.Player;

import nautilus.game.core.arena.property.PropertyBase;
import nautilus.game.dominate.arena.IDominateArena;

public class PointPowerUps extends PropertyBase<IDominateArena>
{
    public PointPowerUps()
    {
        super("pointpowerups");
    }

    public boolean Parse(IDominateArena arena, String value) 
    {
        for (String vector : value.split(","))
        {
            arena.AddPointPowerUp(ParseVector(vector.trim()));
        }
        
        return true;
    }
    
    public boolean Parse(IDominateArena arena, Player player) 
    {
        arena.AddPointPowerUp(ParseVector(player));
        
        return true;
    }
}