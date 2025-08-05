package nautilus.game.core.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.player.ITeamGamePlayer;

public abstract class Team<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>> implements ITeam<PlayerType> 
{
    private int _score;
    private TeamType _teamType;
    private Region _spawnRoom;
    
    protected List<PlayerType> Players;
    
    public Team(TeamType teamType)
    {
        _teamType = teamType;
        Players = new ArrayList<PlayerType>();
    }
    
    @Override
    public TeamType GetTeamType()
    {
        return _teamType;
    }

    @Override
    public List<PlayerType> GetPlayers()
    {
        return Players;
    }
    
    @Override
    public void RemovePlayer(PlayerType player)
    {
        player.SetTeam(null);
        Players.remove(player);
    }

    @Override
    public void AddPoint()
    {
        _score++;
    }
    
    @Override
    public void AddPoints(int points)
    {
        _score += points;
    }
    
    @Override
    public void SetScore(int score)
    {
        _score = score;
    }
    
    @Override
    public int GetScore()
    {
        return _score;
    }

    @Override
    public void ClearPlayers()
    {
        Players.clear();
    }

    @Override
    public void SetSpawnRoom(Region spawnRoom)
    {
        _spawnRoom = spawnRoom;
    }

    @Override
    public boolean IsInSpawnRoom(Location location)
    {
        return _spawnRoom.Contains(location.toVector());
    }
}
