package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GameStartedEvent<GameType extends IGame<?, ? extends IGamePlayer>> extends GameEvent<GameType>
{
  public GameStartedEvent(GameType game)
  {
    super(game);
  }
}
