package nautilus.game.dominate.engine;

import java.util.List;

import nautilus.game.core.game.ITeamGame;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.IDominatePlayer;

public interface IDominateGame extends ITeamGame<IDominateArena, IDominatePlayer, IDominateTeam>
{
    List<IControlPoint> GetControlPoints();
}