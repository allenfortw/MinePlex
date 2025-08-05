package nautilus.game.dominate.engine;

import java.util.List;

import nautilus.game.dominate.player.IDominatePlayer;

import org.bukkit.Location;

public interface IControlPoint
{
    String GetName();

    void UpdateLogic();

    boolean Captured();
    IDominateTeam GetOwnerTeam();
    
    void Deactivate();

    void UpdateVisual();

    int GetPoints();

    Location GetMiddlePoint();

    List<IDominatePlayer> GetCapturers();
}
