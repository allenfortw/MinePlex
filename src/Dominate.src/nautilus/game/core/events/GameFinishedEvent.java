package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GameFinishedEvent<GameType extends IGame<?, ? extends IGamePlayer>> extends GameEvent<GameType>
{
  public GameFinishedEvent(GameType game)
  {
    super(game);
  }
}
