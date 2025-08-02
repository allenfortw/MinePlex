package nautilus.game.core.engine;

import java.util.List;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract interface IGameEngine<GameType extends IGame<ArenaType, PlayerType>, ArenaType extends IArena, PlayerType extends IGamePlayer>
{
  public abstract boolean IsPlayerInGame(Player paramPlayer);
  
  public abstract boolean IsPlayerInActiveGame(Player paramPlayer);
  
  public abstract boolean IsPlayerInActiveGame(String paramString);
  
  public abstract GameType GetGameForPlayer(Player paramPlayer);
  
  public abstract GameType GetGameForPlayer(String paramString);
  
  public abstract void RemovePlayerFromGame(Player paramPlayer);
  
  public abstract void RemovePlayerFromGame(Player paramPlayer, boolean paramBoolean);
  
  public abstract String GetGameType();
  
  public abstract List<GameType> GetGames();
  
  public abstract GameType ScheduleNewGame();
  
  public abstract boolean AddPlayerToGame(Player paramPlayer, boolean paramBoolean);
  
  public abstract List<GameType> GetActiveGames();
  
  public abstract void AddSpectatorToGame(GameType paramGameType, Player paramPlayer, Location paramLocation);
  
  public abstract void RemoveSpectatorFromGame(Player paramPlayer, boolean paramBoolean);
  
  public abstract boolean IsSpectatorInActiveGame(Player paramPlayer);
}
