package nautilus.game.core.game;

import java.util.Collection;
import java.util.List;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.player.IGamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract interface IGame<ArenaType extends IArena, PlayerType extends IGamePlayer>
{
  public abstract boolean IsPlayerInGame(Player paramPlayer);
  
  public abstract PlayerType GetPlayer(Player paramPlayer);
  
  public abstract PlayerType GetPlayer(String paramString);
  
  public abstract PlayerType GetSpectator(Player paramPlayer);
  
  public abstract Collection<PlayerType> GetPlayers();
  
  public abstract void RemovePlayer(PlayerType paramPlayerType);
  
  public abstract void RemoveSpectator(PlayerType paramPlayerType);
  
  public abstract void UpdateReconnectedPlayer(Player paramPlayer);
  
  public abstract List<PlayerType> GetAssailants(Player paramPlayer);
  
  public abstract boolean HasStarted();
  
  public abstract boolean CanMove(PlayerType paramPlayerType, Location paramLocation1, Location paramLocation2);
  
  public abstract boolean CanInteract(PlayerType paramPlayerType, Block paramBlock);
  
  public abstract PlayerType AddPlayerToGame(Player paramPlayer);
  
  public abstract PlayerType AddSpectatorToGame(Player paramPlayer, Location paramLocation);
  
  public abstract boolean IsInArena(Location paramLocation);
  
  public abstract ArenaType GetArena();
  
  public abstract void Activate(ArenaType paramArenaType);
  
  public abstract boolean IsActive();
  
  public abstract void Deactivate();
  
  public abstract void StartRespawnFor(PlayerType paramPlayerType);
  
  public abstract void RespawnPlayer(PlayerType paramPlayerType);
  
  public abstract void ResetPlayer(PlayerType paramPlayerType);
  
  public abstract long GetStartTime();
  
  public abstract int GetWinLimit();
  
  public abstract boolean IsSpectatorInGame(Player paramPlayer);
  
  public abstract Collection<PlayerType> GetSpectators();
}
