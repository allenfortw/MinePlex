package nautilus.game.dominate.arena;

import java.util.List;

import org.bukkit.util.Vector;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.Region;

public interface IDominateArena extends ITeamArena
{
    void AddControlPointArea(Region parseRegion);

    List<Region> GetControlPointAreas();

    void AddResupplyPowerUp(Vector parseVector);

    void AddPointPowerUp(Vector parseVector);

    List<Vector> GetResupplyPowerUpPoints();

    List<Vector> GetPointPowerUpPoints();
}
