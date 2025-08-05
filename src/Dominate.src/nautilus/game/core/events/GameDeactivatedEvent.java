package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GameDeactivatedEvent<GameType extends IGame<?, ? extends IGamePlayer>> extends GameEvent<GameType>
{
  public GameDeactivatedEvent(GameType game)
  {
    super(game);
  }
}
