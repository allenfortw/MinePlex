package nautilus.game.core.scoreboard;

import java.util.Comparator;
import nautilus.game.core.player.IGamePlayer;

public class PlayerPointSorter<PlayerType extends IGamePlayer>
  implements Comparator<PlayerType>
{
  public int compare(PlayerType a, PlayerType b)
  {
    if (a.GetPoints() != b.GetPoints()) {
      return b.GetPoints() - a.GetPoints();
    }
    return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
  }
}
