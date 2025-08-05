package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

public class GameActivatedEvent<GameType extends IGame<?, ? extends IGamePlayer>> extends GameEvent<GameType>
{
    public GameActivatedEvent(GameType game)
    {
        super(game);
    }
}
