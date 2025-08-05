package nautilus.game.dominate.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import nautilus.game.core.scoreboard.TeamGameScoreHandler;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.engine.DominateNotifier;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.events.ControlPointCapturedEvent;
import nautilus.game.dominate.events.ControlPointLostEvent;
import nautilus.game.dominate.player.IDominatePlayer;

public class DominateScoreHandler extends TeamGameScoreHandler<DominateNotifier, IDominateGame, IDominateArena, IDominateTeam, IDominatePlayer> implements IDominateScoreHandler
{
	public DominateScoreHandler(JavaPlugin plugin, DominateNotifier notifier)
	{
		super(plugin, notifier);
	}

	public void RewardForDeath(IDominatePlayer player)
	{
		super.RewardForDeath(player);
		player.GetTeam().AddPoints(-5);
	}
	
	public void RewardForTeamKill(IDominatePlayer killer, IDominatePlayer victim)
	{
		super.RewardForTeamKill(killer, victim);
		killer.GetTeam().AddPoints(-5);
	}
	
	public void RewardForKill(IDominatePlayer killer, IDominatePlayer victim, int assists)
	{
		super.RewardForKill(killer, victim, assists);
        killer.GetTeam().AddPoints(15 + GetKillModifierValue(killer, victim, assists));
	}
	
	@EventHandler
	public void OnControlPointCaptured(ControlPointCapturedEvent event)
	{
		for (IDominatePlayer player : event.GetPlayersInvolved())
		{
			player.AddPoints(25);
			player.GetTeam().AddPoints(25);
		}
		
		Notifier.BroadcastMessageToPlayers("You helped capture " + event.GetControlPoint().GetName() + " for an additional +" + ChatColor.YELLOW + 25 + ChatColor.GRAY + " to your score!", event.GetPlayersInvolved());
	}

	@EventHandler
	public void OnControlPointLost(ControlPointLostEvent event)
	{
		for (IDominatePlayer player : event.GetPlayersInvolved())
		{
			player.AddPoints(15);
			player.GetTeam().AddPoints(15);
		}
		
		Notifier.BroadcastMessageToPlayers("You helped steal " + event.GetControlPoint().GetName() + " for an additional +" + ChatColor.YELLOW + 15 + ChatColor.GRAY + " to your score!", event.GetPlayersInvolved());
	}

	@Override
	protected int GetKillModifierValue(IDominatePlayer killer, IDominatePlayer victim, int assists) 
	{
		return 5 * victim.GetTeam().GetControlPoints().size();
	}
}
