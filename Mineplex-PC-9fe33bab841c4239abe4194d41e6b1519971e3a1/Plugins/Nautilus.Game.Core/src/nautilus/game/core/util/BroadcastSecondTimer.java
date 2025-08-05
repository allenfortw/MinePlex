package nautilus.game.core.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import nautilus.game.core.arena.IArena;
import nautilus.game.core.game.IGame;
import nautilus.game.core.notifier.IPlayerNotifier;
import nautilus.game.core.player.IGamePlayer;

public class BroadcastSecondTimer extends BroadcastTimer 
{	
	public BroadcastSecondTimer(IPlayerNotifier notifier, IGame<? extends IArena, ? extends IGamePlayer> game, int maxTime, String message, String endMessage, ActionListener actionListener)
	{
		super(notifier, game, maxTime, message, endMessage, actionListener);

		Timer = new Timer(1000, Listener);		
		Timer.setDelay(1000);
		Timer.setInitialDelay(0);
		Timer.setRepeats(true);
	}
	
	public void LoadListener()
	{
		Listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) 
			{
				if (Ticks <= 0)
				{
					if (EndMessage != null)
						Notifier.BroadcastMessageToPlayers(EndMessage, Game.GetPlayers());
					
					Stop();
					KickOffListener();
				}
				else if (Ticks == 1)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in 1 second.", Game.GetPlayers());
				}
				else if (Ticks < 11)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in " + Ticks + " seconds.", Game.GetPlayers());
				}
				else if (Ticks == 15)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in " + Ticks + " seconds.", Game.GetPlayers());
				}
				else if (Ticks == 30)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in " + Ticks + " seconds.", Game.GetPlayers());
				}
				else if (Ticks == 45)
				{
					Notifier.BroadcastMessageToPlayers(Message + " in " + Ticks + " seconds.", Game.GetPlayers());
				}
				
				Ticks--;
		    }
		};
	}
	
	@Override
	public void SetTimeLeft(long time)
	{
		if (time > 1000)
		{
			Ticks = (int)(time / 1000);
			
			if (!IsRunning())
				Start();
		}
		else
		{
			Ticks = 0;
		}
		
		if (!IsRunning())
			Start();
	}
	
	@Override
	public long GetTimeLeft()
	{
		return Ticks * 1000;
	}
}
