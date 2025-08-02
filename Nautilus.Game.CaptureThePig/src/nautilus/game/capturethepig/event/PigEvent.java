package nautilus.game.capturethepig.event;

import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.core.events.GameEvent;

public class PigEvent extends GameEvent<ICaptureThePigGame>
{
    public PigEvent(ICaptureThePigGame game)
    {
        super(game);
    }
}
