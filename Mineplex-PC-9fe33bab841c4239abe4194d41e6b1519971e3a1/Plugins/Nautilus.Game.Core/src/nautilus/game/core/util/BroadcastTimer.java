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
		Notifier = notifier;
		Game = game;
		Message = message;
		EndMessage = endMessage;
		Ticks = maxTime;
		OutsideListener = actionListener;
		
		LoadListener();
	}
	
	public void Start()
	{
		Timer.start();
	}
	
	public void Stop()
	{
		Timer.stop();
	}
	
	public void Deactivate()
	{
		Timer.stop();
		
		Notifier = null;
		Game = null;
		Timer = null;
		Message = null;
		EndMessage = null;	
		OutsideListener = null;
		Listener = null;
	}
	
	protected void KickOffListener()
	{
		if (OutsideListener != null)
			OutsideListener.actionPerformed(null);
	}
	
	abstract void LoadListener();
	
	public abstract void SetTimeLeft(long time);

	public abstract long GetTimeLeft();
	
	public Boolean IsRunning()
	{
		return Timer.isRunning();
	}
}
