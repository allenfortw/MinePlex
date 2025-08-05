package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GamePlayerAfkEvent<GameType extends IGame<?, PlayerType>, PlayerType extends IGamePlayer> extends GameEvent<GameType>
{
  private PlayerType _player;
  
  public GamePlayerAfkEvent(GameType game, PlayerType player)
  {
    super(game);
    
    this._player = player;
  }
  
  public PlayerType GetPlayer()
  {
    return this._player;
  }
}
