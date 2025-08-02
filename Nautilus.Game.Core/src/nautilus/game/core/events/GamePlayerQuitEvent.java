package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GamePlayerQuitEvent<GameType extends IGame<?, PlayerType>, PlayerType extends IGamePlayer> extends GameEvent<GameType>
{
    private PlayerType _player;
    
    public GamePlayerQuitEvent(GameType game, PlayerType player)
    {
        super(game);
        
        _player = player;
    }
    
    public PlayerType GetPlayer()
    {
        return _player;
    }
}
