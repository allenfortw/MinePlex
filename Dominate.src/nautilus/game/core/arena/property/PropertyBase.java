package nautilus.game.core.arena.property;

import nautilus.game.core.arena.IArena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class PropertyBase<ArenaType extends IArena> implements IProperty<ArenaType>
{
  protected String Name;
  
  public PropertyBase(String name)
  {
    this.Name = name;
  }
  
  public String GetName()
  {
    return this.Name;
  }
  
  protected Vector ParseVector(String vectorString)
  {
    Vector vector = new Vector();
    
    String[] parts = vectorString.split(" ");
    
    vector.setX(Double.parseDouble(parts[0]));
    vector.setY(Double.parseDouble(parts[1]));
    vector.setZ(Double.parseDouble(parts[2]));
    
    return vector;
  }
  
  protected Vector ParseVector(Player player)
  {
    return player.getLocation().toVector();
  }
}
