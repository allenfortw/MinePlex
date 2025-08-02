package nautilus.game.core.events;

import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GamePlayerAttackedPlayerEvent<GameType extends IGame<?, PlayerType>, PlayerType extends IGamePlayer> extends GameEvent<GameType> implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean _cancelled = false;
    private PlayerType _attacker;
    private PlayerType _victim;
 
    public GamePlayerAttackedPlayerEvent(GameType game, PlayerType attacker, PlayerType victim)
    {
        super(game);
        
        _attacker = attacker;
        _victim = victim;
    }
 
    public PlayerType GetAttacker()
    {
        return _attacker;
    }
    
    public PlayerType GetVictim()
    {
        return _victim;
    }
 
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return _cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        _cancelled = cancel;
    }
}
