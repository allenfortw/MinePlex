package nautilus.game.core.scoreboard;

import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.notifier.TeamPlayerNotifier;
import nautilus.game.core.player.ITeamGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TeamGameScoreHandler<NotifierType extends TeamPlayerNotifier<GameType, ArenaType, PlayerTeamType, PlayerType>, GameType extends ITeamGame<ArenaType, PlayerType, PlayerTeamType>, ArenaType extends ITeamArena, PlayerTeamType extends nautilus.game.core.engine.ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>> implements ITeamScoreHandler<PlayerType, PlayerTeamType>, Listener
{
  protected NotifierType Notifier;
  
  public TeamGameScoreHandler(JavaPlugin plugin, NotifierType notifier)
  {
    this.Notifier = notifier;
    
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public void Stop()
  {
    HandlerList.unregisterAll(this);
  }
  
  public void RewardForDeath(PlayerType player)
  {
    player.AddPoints(-5);
    this.Notifier.BroadcastMessageToPlayer("Your score was reduced by " + ChatColor.YELLOW + 5 + ChatColor.GRAY + " for dying!", player.GetPlayer());
  }
  
  public void RewardForTeamKill(PlayerType killer, PlayerType victim)
  {
    killer.AddPoints(-25);
    
    this.Notifier.BroadcastMessageToPlayer("You team killed " + victim.getName() + " and reduced your score by " + ChatColor.YELLOW + -25 + ChatColor.GRAY + "!", killer.GetPlayer());
  }
  
  public void RewardForKill(PlayerType killer, PlayerType victim, int assists)
  {
    int deathValue = 15;
    int deathPoints = deathValue + GetKillModifierValue(killer, victim, assists);
    
    killer.AddPoints(deathPoints - assists * 2);
    
    this.Notifier.BroadcastMessageToPlayer("You killed " + victim.getName() + " for an additional +" + ChatColor.YELLOW + (deathPoints - assists * 2) + ChatColor.GRAY + " to your score!", killer.GetPlayer());
  }
  
  public void RewardForAssist(PlayerType assistant, PlayerType victim)
  {
    assistant.AddPoints(2);
    this.Notifier.BroadcastMessageToPlayer("You helped kill " + victim.getName() + " for an additional +" + ChatColor.YELLOW + 2 + ChatColor.GRAY + " to your score!", assistant.GetPlayer());
  }
  
  protected abstract int GetKillModifierValue(PlayerType paramPlayerType1, PlayerType paramPlayerType2, int paramInt);
}
