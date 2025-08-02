package nautilus.game.core.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import nautilus.game.core.game.IGame;
import nautilus.game.core.notifier.IPlayerNotifier;
import nautilus.game.core.player.IGamePlayer;


public class BroadcastMinuteTimer
  extends BroadcastTimer
{
  private BroadcastSecondTimer countdownTimer;
  
  public BroadcastMinuteTimer(IPlayerNotifier notifier, IGame<?, ? extends IGamePlayer> game, int maxTime, String message, String endMessage, ActionListener listener)
  {
    super(notifier, game, maxTime, message, endMessage, listener);
    
    this.Timer = new Timer(60000, this.Listener);
    this.Timer.setDelay(60000);
    this.Timer.setInitialDelay(0);
    
    this.countdownTimer = new BroadcastSecondTimer(notifier, game, 60, message, this.EndMessage, new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        BroadcastMinuteTimer.this.KickOffListener();
      }
    });
  }
  
  public void LoadListener()
  {
    this.Listener = new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        if (BroadcastMinuteTimer.this.Ticks % 5 == 0)
        {
          BroadcastMinuteTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastMinuteTimer.this.Message + " in " + BroadcastMinuteTimer.this.Ticks + " minutes.", BroadcastMinuteTimer.this.Game.GetPlayers());
        }
        else if (BroadcastMinuteTimer.this.Ticks == 3)
        {
          BroadcastMinuteTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastMinuteTimer.this.Message + " in 3 minutes.", BroadcastMinuteTimer.this.Game.GetPlayers());
        }
        else if (BroadcastMinuteTimer.this.Ticks == 1)
        {
          BroadcastMinuteTimer.this.Notifier.BroadcastMessageToPlayers(BroadcastMinuteTimer.this.Message + " in 1 minute.", BroadcastMinuteTimer.this.Game.GetPlayers());
          
          BroadcastMinuteTimer.this.Stop();
          BroadcastMinuteTimer.this.countdownTimer.Start();
        }
        
        BroadcastMinuteTimer.this.Ticks -= 1;
      }
    };
  }
  

  public void SetTimeLeft(long time)
  {
    if (time > 60000L)
    {
      this.Ticks = ((int)(time / 60000L));
      
      if (!IsRunning().booleanValue()) {
        Start();
      }
    }
    else {
      this.Ticks = 0;
      Stop();
      this.countdownTimer.SetTimeLeft(time);
      this.countdownTimer.Start();
    }
  }
  

  public long GetTimeLeft()
  {
    long timeLeft = 0L;
    
    if (this.Ticks > 0)
    {
      timeLeft = this.Ticks * 60 * 1000;
    }
    else if (this.countdownTimer.IsRunning().booleanValue())
    {
      timeLeft = this.countdownTimer.GetTimeLeft();
    }
    
    return timeLeft;
  }
  

  public Boolean IsRunning()
  {
    if ((!this.Timer.isRunning()) && (!this.countdownTimer.IsRunning().booleanValue())) return Boolean.valueOf(false); return Boolean.valueOf(true);
  }
  

  public void Deactivate()
  {
    super.Deactivate();
    
    this.countdownTimer.Deactivate();
    this.countdownTimer = null;
  }
}
