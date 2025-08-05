package nautilus.game.core.notifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerNotifier<GameType extends IGame<ArenaType, PlayerType>, ArenaType extends IArena, PlayerType extends IGamePlayer> implements IPlayerNotifier, Listener
{
  protected JavaPlugin Plugin;
  protected String ChatCategoryName;
  
  public PlayerNotifier(JavaPlugin plugin, String chatCategoryName)
  {
    this.Plugin = plugin;
    this.ChatCategoryName = chatCategoryName;
    
    plugin.getServer().getPluginManager().registerEvents(this, this.Plugin);
  }
  
  public void NotifyPlayerJoinGame(GameType game, PlayerType player)
  {
    BroadcastMessageToGamePlayers(game, player.getName() + " joined the game.");
  }
  
  public void BroadcastMessageToPlayers(String message, Collection<? extends IGamePlayer> playersToSpam)
  {
    for (IGamePlayer player : playersToSpam)
    {
      UtilPlayer.message(player.GetPlayer(), F.main(this.ChatCategoryName, message));
    }
  }
  
  public void BroadcastMessageToPlayer(String message, Player player)
  {
    UtilPlayer.message(player, F.main(this.ChatCategoryName, message));
  }
  
  public void BroadcastMessageToGamePlayers(GameType game, String message)
  {
    List<PlayerType> playersToSpam = new ArrayList(game.GetPlayers());
    playersToSpam.addAll(game.GetSpectators());
    
    BroadcastMessageToPlayers(message, playersToSpam);
  }
  
  public void BroadcastMessageToOtherGamePlayers(GameType game, String message, Collection<PlayerType> players)
  {
    List<PlayerType> playersToSpam = new ArrayList(game.GetPlayers());
    
    playersToSpam.addAll(game.GetSpectators());
    
    for (PlayerType gamePlayer : players)
    {
      playersToSpam.remove(gamePlayer);
    }
    
    BroadcastMessageToPlayers(message, playersToSpam);
  }
  
  public void BroadcastMessageToOtherGamePlayers(GameType game, String message, PlayerType player)
  {
    List<PlayerType> playersToSpam = new ArrayList(game.GetPlayers());
    playersToSpam.remove(player);
    
    playersToSpam.addAll(game.GetSpectators());
    
    BroadcastMessageToPlayers(message, playersToSpam);
  }
}
