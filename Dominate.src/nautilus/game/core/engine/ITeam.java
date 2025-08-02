package nautilus.game.core.engine;

import java.util.List;
import nautilus.game.core.arena.Region;
import nautilus.game.core.player.ITeamGamePlayer;
import org.bukkit.Location;

public abstract interface ITeam<PlayerType extends ITeamGamePlayer<?>>
{
  public abstract TeamType GetTeamType();
  
  public abstract void AddPlayer(PlayerType paramPlayerType);
  
  public abstract void RemovePlayer(PlayerType paramPlayerType);
  
  public abstract List<PlayerType> GetPlayers();
  
  public abstract void AddPoint();
  
  public abstract void AddPoints(int paramInt);
  
  public abstract void SetScore(int paramInt);
  
  public abstract int GetScore();
  
  public abstract void ClearPlayers();
  
  public abstract void SetSpawnRoom(Region paramRegion);
  
  public abstract boolean IsInSpawnRoom(Location paramLocation);
}
