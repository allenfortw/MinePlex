package nautilus.game.dominate.engine;

import nautilus.game.core.notifier.TeamPlayerNotifier;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.events.ControlPointCapturedEvent;
import nautilus.game.dominate.events.ControlPointEnemyCapturingEvent;
import nautilus.game.dominate.events.ControlPointLostEvent;
import nautilus.game.dominate.player.IDominatePlayer;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class DominateNotifier extends TeamPlayerNotifier<IDominateGame, IDominateArena, IDominateTeam, IDominatePlayer>
{
    public DominateNotifier(JavaPlugin plugin)
    {
        super(plugin, "Dominate");
    }
    
    @EventHandler
    public void OnControlPointCaptured(ControlPointCapturedEvent event)
    {
        IDominateTeam ownerTeam = event.GetNewTeamOwner();
        
        BroadcastMessageToPlayers("You now have control of " + event.GetControlPoint().GetName() + "!", ownerTeam.GetPlayers());
        
        for (IDominatePlayer player : ownerTeam.GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, .4F, 0F);
        }

        BroadcastMessageToOtherGamePlayers(event.GetGame(), GetTeamString(event.GetNewTeamOwner()) + " has captured " + event.GetControlPoint().GetName() + "!", ownerTeam.GetPlayers());
    }
    
    @EventHandler
    public void OnControlPointLost(ControlPointLostEvent event)
    {
        BroadcastMessageToPlayers("You have lost control of " + event.GetControlPoint().GetName() + "!", event.GetPreviousTeamOwner().GetPlayers());
        
        for (IDominatePlayer player : event.GetPreviousTeamOwner().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.BLAZE_DEATH, .4F, 0F);
        }
        
        BroadcastMessageToOtherGamePlayers(event.GetGame(), GetTeamString(event.GetPreviousTeamOwner()) + " has lost " + event.GetControlPoint().GetName() + "!", event.GetPreviousTeamOwner().GetPlayers());
    }
    
    @EventHandler
    public void OnControlPointEnemyCapturing(ControlPointEnemyCapturingEvent event)
    {
        BroadcastMessageToPlayers(GetTeamString(event.GetEnemyTeam()) + " is capturing " + event.GetControlPoint().GetName() + "!", event.GetTeamOwner().GetPlayers());
        
        for (IDominatePlayer player : event.GetTeamOwner().GetPlayers())
        {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, .4F, 0.6F);
        }
    }
}
