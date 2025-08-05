package nautilus.game.capturethepig.player;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.core.player.TeamGamePlayer;

public class CaptureThePigPlayer extends TeamGamePlayer<ICaptureThePigTeam> implements ICaptureThePigPlayer
{
	private int _captures;
	
    public CaptureThePigPlayer(JavaPlugin plugin, Player player)
    {
        super(plugin, player);
        
        SetLives(1);
    }

    public void AddCapture()
    {
    	_captures++;
    }
    
	@Override
	public int GetCaptures()
	{
		return _captures;
	}
}