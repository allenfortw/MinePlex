package nautilus.game.capturethepig.stats;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import nautilus.game.capturethepig.engine.CaptureThePigGameEngine;
import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.capturethepig.repository.ICaptureThePigRepository;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import me.chiss.Core.Plugin.IPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.donation.repository.token.PlayerUpdateToken;

public class CaptureThePigStatsReporter implements Listener
{
    private JavaPlugin _plugin;
    private CaptureThePigGameEngine _engine;
    private ICaptureThePigRepository _repository;
    
	public CaptureThePigStatsReporter(JavaPlugin plugin, CaptureThePigGameEngine engine,  ICaptureThePigRepository repository) 
	{
        _plugin = plugin;
        _engine = engine;
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
        _repository = repository;
	}

    @EventHandler
    public void OnGameFinished(TeamGameFinishedEvent<ICaptureThePigGame, ICaptureThePigTeam, ICaptureThePigPlayer> event)
    {
    	CaptureThePigGameStatsToken gameStats = new CaptureThePigGameStatsToken();
        gameStats.Length = System.currentTimeMillis() - event.GetGame().GetStartTime();
        gameStats.PlayerStats = new ArrayList<CaptureThePigPlayerStatsToken>(event.GetGame().GetPlayers().size());
        
        for (ICaptureThePigPlayer player : event.GetGame().GetPlayers())
        {
        	CaptureThePigPlayerStats stats = new CaptureThePigPlayerStats();
            
        	stats.Captures = player.GetPoints();
        	stats.Kills = player.GetKills();
        	stats.Deaths = player.GetDeaths();
            stats.Assists = player.GetAssists();
            
            CaptureThePigPlayerStatsToken token = new CaptureThePigPlayerStatsToken();
            token.Name = player.getName();
            token.Won = event.GetGame().GetWinLimit() == player.GetTeam().GetScore();
            token.TimePlayed = player.GetTimePlayed();
            token.PlayerStats = stats;
            
            gameStats.PlayerStats.add(token);
        }
        
        _repository.SaveGameStats(new Callback<List<PlayerUpdateToken>>()
		{
			public void run(List<PlayerUpdateToken> tokenList)
			{
				for (PlayerUpdateToken token : tokenList)
				{
					try
					{
				    	CoreClient client = _plugin.GetClients().GetNull(token.Name);
				    	
			    		if (client != null && client.GetPlayer().isOnline())
			    		{
			    			client.Donor().AddPoints(token.Points);
				    		
			    			_engine.UpdatePlayerLobbyItemBalances(client);
				    		
			    			client.GetPlayer().sendMessage(F.main(_engine.GetGameType(), "You earned " + ChatColor.YELLOW + token.Points + C.cGray + " points for playing!"));
			    		}
					}
					catch (Exception ex)
					{
			            System.out.println("Error updating player with token : " + token.Name + "\n" + ex.getMessage());
			            
			            for (StackTraceElement trace : ex.getStackTrace())
			            {
			            	System.out.println(trace);
			            }
					}
				}
			}
		}, gameStats);
    }
}
