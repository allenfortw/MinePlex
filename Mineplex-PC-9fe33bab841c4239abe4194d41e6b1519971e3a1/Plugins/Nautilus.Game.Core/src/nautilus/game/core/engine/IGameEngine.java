package nautilus.game.core.engine;

import java.util.List;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IGameEngine<GameType extends IGame<ArenaType, PlayerType>, ArenaType extends IArena, PlayerType extends IGamePlayer>
{
    boolean IsPlayerInGame(Player player);
    boolean IsPlayerInActiveGame(Player player);
    boolean IsPlayerInActiveGame(String playerName);
    
    GameType GetGameForPlayer(Player player);
    GameType GetGameForPlayer(String playerName);

    void RemovePlayerFromGame(Player player);
    void RemovePlayerFromGame(Player player, boolean quit);
    
    String GetGameType();

    List<GameType> GetGames();

    GameType ScheduleNewGame();

    boolean AddPlayerToGame(Player player, boolean notify);
	List<GameType> GetActiveGames();
	void AddSpectatorToGame(GameType game, Player player, Location location);
	void RemoveSpectatorFromGame(Player player, boolean quit);
	boolean IsSpectatorInActiveGame(Player player);
}
