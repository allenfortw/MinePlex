package nautilus.game.capturethepig.scoreboard;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.capturethepig.engine.CaptureThePigNotifier;
import nautilus.game.capturethepig.event.PigCapturedEvent;
import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.core.scoreboard.TeamGameScoreHandler;

public class CaptureThePigScoreHandler extends TeamGameScoreHandler<CaptureThePigNotifier, ICaptureThePigGame, ICaptureThePigArena, ICaptureThePigTeam, ICaptureThePigPlayer> implements ICaptureThePigScoreHandler
{
	public CaptureThePigScoreHandler(JavaPlugin plugin, CaptureThePigNotifier notifier) 
	{
		super(plugin, notifier);
	}

    @EventHandler
    public void OnPigCaptured(PigCapturedEvent event)
    {
    	ICaptureThePigTeam ownerTeam = event.GetCapturer().GetTeam();

    	event.GetCapturer().AddCapture();
    	ownerTeam.AddPoint();
    }
    
	@Override
	protected int GetKillModifierValue(ICaptureThePigPlayer killer,	ICaptureThePigPlayer victim, int assists) 
	{
		return 5 * victim.GetTeam().GetScore();
	}
}
