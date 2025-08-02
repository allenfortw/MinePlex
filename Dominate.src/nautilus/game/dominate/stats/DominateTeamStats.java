package nautilus.game.dominate.stats;

import java.util.HashMap;

public class DominateTeamStats { public String Name;
  public int Points;
  public HashMap<String, DominatePlayerStats> PlayerStats;
  
  public DominateTeamStats() { this.PlayerStats = new HashMap(); }
}
