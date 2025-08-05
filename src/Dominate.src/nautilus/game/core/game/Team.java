package nautilus.game.core.game;

import java.util.ArrayList;
import java.util.List;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.player.ITeamGamePlayer;
import org.bukkit.Location;



public abstract class Team<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>>
  implements ITeam<PlayerType>
{
  private int _score;
  private TeamType _teamType;
  private Region _spawnRoom;
  protected List<PlayerType> Players;
  
  public Team(TeamType teamType)
  {
    this._teamType = teamType;
    this.Players = new ArrayList();
  }
  

  public TeamType GetTeamType()
  {
    return this._teamType;
  }
  

  public List<PlayerType> GetPlayers()
  {
    return this.Players;
  }
  

  public void RemovePlayer(PlayerType player)
  {
    player.SetTeam(null);
    this.Players.remove(player);
  }
  

  public void AddPoint()
  {
    this._score += 1;
  }
  

  public void AddPoints(int points)
  {
    this._score += points;
  }
  

  public void SetScore(int score)
  {
    this._score = score;
  }
  

  public int GetScore()
  {
    return this._score;
  }
  

  public void ClearPlayers()
  {
    this.Players.clear();
  }
  

  public void SetSpawnRoom(Region spawnRoom)
  {
    this._spawnRoom = spawnRoom;
  }
  

  public boolean IsInSpawnRoom(Location location)
  {
    return this._spawnRoom.Contains(location.toVector()).booleanValue();
  }
}
