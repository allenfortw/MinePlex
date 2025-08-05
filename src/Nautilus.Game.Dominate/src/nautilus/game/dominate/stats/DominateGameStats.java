package nautilus.game.dominate.stats;

import java.util.HashMap;

import nautilus.game.core.engine.TeamType;

public class DominateGameStats
{
    public DominateGameStats()
    {
    	PlayerStats = new HashMap<TeamType, DominateTeamStats>();
    }

    public HashMap<TeamType, DominateTeamStats> PlayerStats;
    
    public long Duration;
}
