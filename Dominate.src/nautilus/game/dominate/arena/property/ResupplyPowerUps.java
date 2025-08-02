package nautilus.game.dominate.arena.property;

import nautilus.game.core.arena.property.PropertyBase;
import nautilus.game.dominate.arena.IDominateArena;
import org.bukkit.entity.Player;

public class ResupplyPowerUps
  extends PropertyBase<IDominateArena>
{
  public ResupplyPowerUps()
  {
    super("resupplypowerups");
  }
  
  public boolean Parse(IDominateArena arena, String value)
  {
    for (String vector : value.split(","))
    {
      arena.AddResupplyPowerUp(ParseVector(vector.trim()));
    }
    
    return true;
  }
  
  public boolean Parse(IDominateArena arena, Player player)
  {
    arena.AddResupplyPowerUp(ParseVector(player));
    
    return true;
  }
}
