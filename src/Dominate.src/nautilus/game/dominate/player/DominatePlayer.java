package nautilus.game.dominate.player;

import nautilus.game.core.player.TeamGamePlayer;
import nautilus.game.dominate.engine.IDominateTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DominatePlayer
  extends TeamGamePlayer<IDominateTeam> implements IDominatePlayer
{
  public DominatePlayer(JavaPlugin plugin, Player player)
  {
    super(plugin, player);
    
    SetLives(1);
  }
}
