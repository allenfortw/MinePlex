package nautilus.game.dominate.stats;

import java.util.HashMap;

public class DominateTeamStats
{
    public DominateTeamStats()
    {
        PlayerStats = new HashMap<String, DominatePlayerStats>();
    }
    
    public String Name;
    public int Points;
    
    public HashMap<String, DominatePlayerStats> PlayerStats;
}
