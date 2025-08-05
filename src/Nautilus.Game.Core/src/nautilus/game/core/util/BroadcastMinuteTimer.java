package nautilus.game.core.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import nautilus.game.core.game.IGame;
import nautilus.game.core.notifier.IPlayerNotifier;
import nautilus.game.core.player.IGamePlayer;

public class BroadcastMinuteTimer extends BroadcastTimer 
{
	private BroadcastSecondTimer countdownTimer;
	
	public BroadcastMinuteTimer(IPlayerNotifier notifier, IGame<?, ? extends IGamePlayer> game, int maxTime, String message, String endMessage, ActionListener listener)
	{
		super(notifier, game, maxTime, message, endMessage, listener);
		
		Timer = new Timer(60*1000, Listener);		
		Timer.setDelay(60*1000);
		Timer.setInitialDelay(0);
		
		countdownTimer = new BroadcastSecondTimer(notifier, game, 60, message, EndMessage, new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt) 
			{
				KickOffListener();
			}
		});
	}
	
	public void LoadListener()
	{
		Listener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt) 
			{
				if (Ticks % 5 == 0)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in " + Ticks + " minutes.", Game.GetPlayers());
				}
				else if (Ticks == 3)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in 3 minutes.", Game.GetPlayers());
				}
				else if (Ticks == 1)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in 1 minute.", Game.GetPlayers());
					
					Stop();
					countdownTimer.Start();
				}
				
				Ticks--;
		    }
		};
	}
	
	@Override
	public void SetTimeLeft(long time)
	{
		if (time > 60000)
		{
			Ticks = (int)(time / 60000);
			
			if (!IsRunning())
				Start();
		}
		else
		{
			Ticks = 0;
			Stop();
			countdownTimer.SetTimeLeft(time);
			countdownTimer.Start();
		}
	}
	
	@Override
	public long GetTimeLeft()
	{
		long timeLeft = 0;
		
		if (Ticks > 0)
		{
			timeLeft = Ticks * 60 * 1000;
		}
		else if (countdownTimer.IsRunning())
		{
			timeLeft = countdownTimer.GetTimeLeft();
		}
		
		return timeLeft;
	}
	
	@Override
	public Boolean IsRunning()
	{
		return Timer.isRunning() || countdownTimer.IsRunning();
	}
	
	@Override 
	public void Deactivate()
	{
		super.Deactivate();
		
		countdownTimer.Deactivate();
		countdownTimer = null;
	}
}
