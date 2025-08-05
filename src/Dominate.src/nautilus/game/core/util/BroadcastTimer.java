package nautilus.game.core.util;

import java.awt.event.ActionListener;
import javax.swing.Timer;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.notifier.IPlayerNotifier;
import nautilus.game.core.player.IGamePlayer;


public abstract class BroadcastTimer
{
  protected IPlayerNotifier Notifier;
  protected IGame<?, ? extends IGamePlayer> Game;
  protected Timer Timer;
  protected String Message;
  protected String EndMessage;
  protected int Ticks = 5;
  protected ActionListener OutsideListener;
  protected ActionListener Listener;
  
  public BroadcastTimer(IPlayerNotifier notifier, IGame<? extends IArena, ? extends IGamePlayer> game, int maxTime, String message, String endMessage, ActionListener actionListener)
  {
    this.Notifier = notifier;
    this.Game = game;
    this.Message = message;
    this.EndMessage = endMessage;
    this.Ticks = maxTime;
    this.OutsideListener = actionListener;
    
    LoadListener();
  }
  
  public void Start()
  {
    this.Timer.start();
  }
  
  public void Stop()
  {
    this.Timer.stop();
  }
  
  public void Deactivate()
  {
    this.Timer.stop();
    
    this.Notifier = null;
    this.Game = null;
    this.Timer = null;
    this.Message = null;
    this.EndMessage = null;
    this.OutsideListener = null;
    this.Listener = null;
  }
  
  protected void KickOffListener()
  {
    if (this.OutsideListener != null) {
      this.OutsideListener.actionPerformed(null);
    }
  }
  
  abstract void LoadListener();
  
  public abstract void SetTimeLeft(long paramLong);
  
  public abstract long GetTimeLeft();
  
  public Boolean IsRunning() {
    return Boolean.valueOf(this.Timer.isRunning());
  }
}
