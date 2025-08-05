package nautilus.game.dominate.stats;

import java.util.HashMap;

public class DominateGameStats
{
  public HashMap<nautilus.game.core.engine.TeamType, DominateTeamStats> PlayerStats;
  public long Duration;
  
  public DominateGameStats() {
    this.PlayerStats = new HashMap();
  }
}
