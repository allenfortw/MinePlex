package nautilus.game.core.game;

import java.util.Collection;
import java.util.List;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.player.IGamePlayer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface IGame<ArenaType extends IArena, PlayerType extends IGamePlayer>
{
    boolean IsPlayerInGame(Player player);
    PlayerType GetPlayer(Player player);
    PlayerType GetPlayer(String playerName);
    PlayerType GetSpectator(Player player);
    Collection<PlayerType> GetPlayers();
    
    void RemovePlayer(PlayerType player);
    void RemoveSpectator(PlayerType player);
    void UpdateReconnectedPlayer(Player player);
    
    List<PlayerType> GetAssailants(Player player);
    
    boolean HasStarted();
    boolean CanMove(PlayerType player, Location from, Location to);
    boolean CanInteract(PlayerType player, Block block);
    PlayerType AddPlayerToGame(Player player);
    PlayerType AddSpectatorToGame(Player player, Location to);
    boolean IsInArena(Location location);
    ArenaType GetArena();
    
    void Activate(ArenaType arena);
    boolean IsActive();
    void Deactivate();
    
    void StartRespawnFor(PlayerType player);
    void RespawnPlayer(PlayerType player);
    void ResetPlayer(PlayerType player);
    
    long GetStartTime();
    int GetWinLimit();
	boolean IsSpectatorInGame(Player player);
	Collection<PlayerType> GetSpectators();
}