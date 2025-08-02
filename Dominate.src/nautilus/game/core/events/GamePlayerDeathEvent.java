package nautilus.game.core.events;

import java.util.List;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;


public class GamePlayerDeathEvent<GameType extends IGame<?, PlayerType>, PlayerType extends IGamePlayer>
  extends GameEvent<GameType>
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
    
    this._player = player;
    this._killer = killer;
    this._assistants = assistants;
    this._drops = drops;
    this._log = combatDeath.GetLog();
  }
  
  public PlayerType GetPlayer()
  {
    return this._player;
  }
  
  public PlayerType GetKiller()
  {
    return this._killer;
  }
  
  public List<PlayerType> GetAssistants()
  {
    return this._assistants;
  }
  
  public List<ItemStack> GetDrops()
  {
    return this._drops;
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
    return this._log;
  }
}
