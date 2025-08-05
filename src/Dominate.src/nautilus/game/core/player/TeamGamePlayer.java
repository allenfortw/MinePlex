package nautilus.game.core.player;

import nautilus.game.core.engine.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamGamePlayer<Team extends ITeam<? extends ITeamGamePlayer<Team>>>
  extends GamePlayer implements ITeamGamePlayer<Team>
{
  protected Team Team;
  
  public TeamGamePlayer(JavaPlugin plugin, Player player)
  {
    super(plugin, player);
  }
  

  public Team GetTeam()
  {
    return this.Team;
  }
  

  public void SetTeam(Team team)
  {
    this.Team = team;
  }
}
