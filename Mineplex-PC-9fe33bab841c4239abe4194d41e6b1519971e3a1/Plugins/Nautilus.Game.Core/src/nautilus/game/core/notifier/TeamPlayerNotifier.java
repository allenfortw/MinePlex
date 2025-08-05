package nautilus.game.core.notifier;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;

public class TeamPlayerNotifier<GameType extends ITeamGame<ArenaType, PlayerType, PlayerTeamType>, ArenaType extends ITeamArena, PlayerTeamType extends ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>> extends PlayerNotifier<GameType, ArenaType, PlayerType>
{
    public TeamPlayerNotifier(JavaPlugin plugin, String chatCategoryName)
    {
        super(plugin, chatCategoryName);
    }
    
    public void NotifyGameWin(GameType game, PlayerTeamType team)
    {
        BroadcastMessageToGamePlayers(game, "Congratulations " + team.GetTeamType() + " team for winning this match!");
    }
    
    public void NotifyPlayerJoinTeam(GameType game, PlayerType player)
    {
        BroadcastMessageToOtherGamePlayers(game, player.getName() + " joined the " + player.GetTeam().GetTeamType() + " team.", player);
        UtilPlayer.message(player.GetPlayer(), F.main("Team", "You joined the " + player.GetTeam().GetTeamType() + " team."));
    }
    
    public void NotifyPlayerLeaveTeam(GameType game, PlayerType player)
    {
        BroadcastMessageToOtherGamePlayers(game, player.getName() + " left the " + player.GetTeam().GetTeamType() + " team.", player);       
        UtilPlayer.message(player.GetPlayer(), F.main("Team", "You left the " + player.GetTeam().GetTeamType() + " team."));
    }
    
    @EventHandler
    public void onTeamGameFinished(TeamGameFinishedEvent<GameType, PlayerTeamType, PlayerType> event)
    {
        BroadcastMessageToGamePlayers(event.GetGame(), GetTeamString(event.GetWinningTeam()) + " has won the game!");
    }
    
    protected String GetTeamString(PlayerTeamType team)
    {
    	String colorString = "" + (team.GetTeamType() == TeamType.RED ? ChatColor.RED : ChatColor.BLUE);
    	return ChatColor.WHITE + "[" + colorString + team.GetTeamType() + ChatColor.WHITE + "]" + ChatColor.GRAY;
    }
}
