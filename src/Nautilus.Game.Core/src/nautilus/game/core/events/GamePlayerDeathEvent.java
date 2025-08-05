package nautilus.game.core.events;

import java.util.List;

import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class GamePlayerDeathEvent<GameType extends IGame<?, PlayerType>, PlayerType extends IGamePlayer> extends GameEvent<GameType>
{
    private static final HandlerList handlers = new HandlerList();
    private PlayerType _player;
    private PlayerType _killer;
    private List<PlayerType> _assistants;
    private List<ItemStack> _drops;
    private CombatLog _log;
 
    public GamePlayerDeathEvent(GameType game, PlayerType player, PlayerType killer, List<PlayerType> assistants, List<ItemStack> drops, CombatDeathEvent combatDeath) 
    {
        super(game);
        
        _player = player;
        _killer = killer;
        _assistants = assistants;
        _drops = drops;
        _log = combatDeath.GetLog();
    }
 
    public PlayerType GetPlayer()
    {
        return _player;
    }
    
    public PlayerType GetKiller()
    {
        return _killer;
    }
 
    public List<PlayerType> GetAssistants()
    {
        return _assistants;
    }
    
    public List<ItemStack> GetDrops()
    {
        return _drops;
    }
    
    public HandlerList getHandlers()
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    public CombatLog GetLog()
    {
    	return _log;
    }
}
