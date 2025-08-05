package nautilus.game.core.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.notifier.IPlayerNotifier;
import nautilus.game.core.player.IGamePlayer;


public class BroadcastSecondTimer
  extends BroadcastTimer
{
  public BroadcastSecondTimer(IPlayerNotifier notifier, IGame<? extends IArena, ? extends IGamePlayer> game, int maxTime, String message, String endMessage, ActionListener actionListener)
  {
    super(notifier, game, maxTime, message, endMessage, actionListener);
    
    this.Timer = new Timer(1000, this.Listener);
    this.Timer.setDelay(1000);
    this.Timer.setInitialDelay(0);
    this.Timer.setRepeats(true);
  }
  
  public void LoadListener()
  {
    this.Listener = new ActionListener()
    {
      public void actionPerformed(ActionEvent evt) {
        if (BroadcastSecondTimer.this.Ticks <= 0)
        {
          if (BroadcastSecondTimer.this.EndMessage != null) {
            BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.EndMessage, BroadcastSecondTimer.this.Game.GetPlayers());
          }
          BroadcastSecondTimer.this.Stop();
          BroadcastSecondTimer.this.KickOffListener();
        }
        else if (BroadcastSecondTimer.this.Ticks == 1)
        {
          BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.Message + " in 1 second.", BroadcastSecondTimer.this.Game.GetPlayers());
        }
        else if (BroadcastSecondTimer.this.Ticks < 11)
        {
          BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.Message + " in " + BroadcastSecondTimer.this.Ticks + " seconds.", BroadcastSecondTimer.this.Game.GetPlayers());
        }
        else if (BroadcastSecondTimer.this.Ticks == 15)
        {
          BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.Message + " in " + BroadcastSecondTimer.this.Ticks + " seconds.", BroadcastSecondTimer.this.Game.GetPlayers());
        }
        else if (BroadcastSecondTimer.this.Ticks == 30)
        {
          BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.Message + " in " + BroadcastSecondTimer.this.Ticks + " seconds.", BroadcastSecondTimer.this.Game.GetPlayers());
        }
        else if (BroadcastSecondTimer.this.Ticks == 45)
        {
          BroadcastSecondTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastSecondTimer.this.Message + " in " + BroadcastSecondTimer.this.Ticks + " seconds.", BroadcastSecondTimer.this.Game.GetPlayers());
        }
        
        BroadcastSecondTimer.this.Ticks -= 1;
      }
    };
  }
  

  public void SetTimeLeft(long time)
  {
    if (time > 1000L)
    {
      this.Ticks = ((int)(time / 1000L));
      
      if (!IsRunning().booleanValue()) {
        Start();
      }
    }
    else {
      this.Ticks = 0;
    }
    
    if (!IsRunning().booleanValue()) {
      Start();
    }
  }
  
  public long GetTimeLeft()
  {
    return this.Ticks * 1000;
  }
}
