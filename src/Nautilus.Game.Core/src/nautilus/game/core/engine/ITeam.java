package nautilus.game.core.engine;

import java.util.List;

import org.bukkit.Location;

import nautilus.game.core.arena.Region;
import nautilus.game.core.player.ITeamGamePlayer;

public interface ITeam<PlayerType extends ITeamGamePlayer<?>>
{
    TeamType GetTeamType();
    
    void AddPlayer(PlayerType player);
    
    void RemovePlayer(PlayerType player);
    
    List<PlayerType> GetPlayers();

    void AddPoint();
    void AddPoints(int points);
    
    void SetScore(int scores);
    int GetScore();
    
    void ClearPlayers();
    
    void SetSpawnRoom(Region spawnRoom);
    
    boolean IsInSpawnRoom(Location location);
}
