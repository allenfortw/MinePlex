package nautilus.game.core.player;

import nautilus.game.core.engine.ITeam;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamGamePlayer<Team extends ITeam<? extends ITeamGamePlayer<Team>>> extends GamePlayer implements ITeamGamePlayer<Team>
{
    protected Team Team;

    public TeamGamePlayer(JavaPlugin plugin, Player player)
    {
        super(plugin, player);
    }

    @Override
    public Team GetTeam()
    {
        return Team;
    }

    @Override
    public void SetTeam(Team team)
    {
        Team = team;
    }
}
