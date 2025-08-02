package nautilus.game.core.events;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEvent<GameType extends IGame<? extends IArena, ? extends IGamePlayer>> extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private GameType _game;
 
    public GameEvent(GameType game)
    {
        _game = game;
    }
 
    public GameType GetGame()
    {
        return _game;
    }
 
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
