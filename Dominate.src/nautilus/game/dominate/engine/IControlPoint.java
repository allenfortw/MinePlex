package nautilus.game.dominate.engine;

import java.util.List;
import nautilus.game.dominate.player.IDominatePlayer;
import org.bukkit.Location;

public abstract interface IControlPoint
{
  public abstract String GetName();
  
  public abstract void UpdateLogic();
  
  public abstract boolean Captured();
  
  public abstract IDominateTeam GetOwnerTeam();
  
  public abstract void Deactivate();
  
  public abstract void UpdateVisual();
  
  public abstract int GetPoints();
  
  public abstract Location GetMiddlePoint();
  
  public abstract List<IDominatePlayer> GetCapturers();
}
