package nautilus.game.capturethepig.engine;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.capturethepig.event.PigCapturedEvent;
import nautilus.game.capturethepig.event.PigDroppedEvent;
import nautilus.game.capturethepig.event.PigPickedUpEvent;
import nautilus.game.capturethepig.event.PigStolenEvent;
import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.core.notifier.TeamPlayerNotifier;
import me.chiss.Core.Plugin.IPlugin;

public class CaptureThePigNotifier extends TeamPlayerNotifier<ICaptureThePigGame, ICaptureThePigArena, ICaptureThePigTeam, ICaptureThePigPlayer>
{
    public CaptureThePigNotifier(IPlugin plugin)
    {
        super(plugin, "CTP");
    }
    
    @EventHandler
    public void OnPigCaptured(PigCapturedEvent event)
    {
    	ICaptureThePigTeam ownerTeam = event.GetCapturer().GetTeam();
        
        BroadcastMessageToPlayers("Your team has captured a pig!", ownerTeam.GetPlayers());
        
        for (ICaptureThePigPlayer player : ownerTeam.GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, .8F, 0F);
        }

        BroadcastMessageToOtherGamePlayers(event.GetGame(), GetTeamString(event.GetCapturer().GetTeam()) + " captured a pig!", ownerTeam.GetPlayers());
        
        for (ICaptureThePigPlayer player : ownerTeam.GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.BLAZE_DEATH, .8F, 0F);
        }
    }
    
    @EventHandler
    public void OnPigPickedUp(PigPickedUpEvent event)
    {
        for (ICaptureThePigPlayer player : event.GetTeamOwner().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.PIG_IDLE, 3F, 1F);
        }
    }
    
    @EventHandler
    public void OnPigDropped(PigDroppedEvent event)
    {
        for (ICaptureThePigPlayer player : event.GetPreviousTeamOwner().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.ZOMBIE_PIG_HURT, .4F, 0F);
        }
    }
    
    @EventHandler
    public void OnPigStolen(PigStolenEvent event)
    {
        for (ICaptureThePigPlayer player : event.GetPreviousTeamOwner().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1F, .5F);
        }
        
        BroadcastMessageToOtherGamePlayers(event.GetGame(), "Your pigs are being stolen!", event.GetThief().GetTeam().GetPlayers());
        
        for (ICaptureThePigPlayer player : event.GetThief().GetTeam().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.ZOMBIE_PIG_IDLE, 3F, 1F);
        }
    }
}
