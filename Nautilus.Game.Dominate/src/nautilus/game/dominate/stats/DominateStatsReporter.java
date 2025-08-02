package nautilus.game.dominate.stats;

import java.util.ArrayList;
import java.util.List;

import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.donation.repository.token.PlayerUpdateToken;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.GamePlayerAfkEvent;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.repository.DominateRepository;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DominateStatsReporter implements Listener
{
    private JavaPlugin _plugin;
    private DonationManager _donationManager;
    private DominateRepository _repository;
    
    public DominateStatsReporter(JavaPlugin plugin, DonationManager donationManager, String webServer)
    {
        _plugin = plugin;
        _donationManager = donationManager;
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
        _repository = new DominateRepository(webServer);
    }
    
    @EventHandler
    public void OnGamePlayerAFK(GamePlayerAfkEvent<IDominateGame, IDominatePlayer> event)
    {
    	/*
    	DominateGameStats gameStats = _dominateGameStatsMap.get(event.GetGame());
    	IDominateGame game = event.GetGame();
    	
        for (IDominatePlayer player : game.GetPlayers())
        {
        	if (player != event.GetPlayer())
        		continue;
        	
            DominatePlayerStats playerStats = null;
            
            if (player.GetTeam().GetTeamType() == TeamType.RED)
            {
                playerStats = gameStats.TeamStats.get(TeamType.RED).PlayerStats.get(player.getName());
            }
            else if (player.GetTeam().GetTeamType() == TeamType.BLUE)
            {
                playerStats = gameStats.TeamStats.get(TeamType.BLUE).PlayerStats.get(player.getName());
            }
            
            playerStats.AFK = true;
            break;
        }
        */
    }
    
    @EventHandler
    public void OnGameFinished(TeamGameFinishedEvent<IDominateGame, IDominateTeam, IDominatePlayer> event)
    {
        DominateGameStatsToken gameStats = new DominateGameStatsToken();
        gameStats.Duration = event.GetGame().GetStartTime() - System.currentTimeMillis();
        gameStats.PlayerStats = new ArrayList<DominatePlayerStatsToken>();
        boolean redTeamWon = event.GetGame().GetRedTeam().GetScore() > event.GetGame().GetBlueTeam().GetScore();
        
        for (IDominatePlayer player : event.GetGame().GetPlayers())
        {
            DominatePlayerStatsToken playerStats = new DominatePlayerStatsToken();
            playerStats.Name = player.getName();
            playerStats.PlayerStats = new DominatePlayerStats();
            
            playerStats.Won = (redTeamWon == (player.GetTeam().GetTeamType() == TeamType.RED));  
            playerStats.PlayerStats.Kills = player.GetKills();
            playerStats.PlayerStats.Deaths = player.GetDeaths();
            playerStats.PlayerStats.Assists = player.GetAssists();
            playerStats.PlayerStats.Points = player.GetPoints();
            
            gameStats.PlayerStats.add(playerStats);
        }
        
        _repository.SaveGameStats(new Callback<List<PlayerUpdateToken>>()
		{
			public void run(List<PlayerUpdateToken> tokenList)
			{
				for (PlayerUpdateToken token : tokenList)
				{
					try
					{
				    	Player player = _plugin.getServer().getPlayerExact(token.Name);
				    	Donor donor = _donationManager.Get(token.Name);
				    	
			    		if (player != null && player.isOnline())
			    		{
			    			donor.AddGems(token.Gems);
				    		
			    			player.sendMessage(F.main("Dominate", "You earned " + ChatColor.YELLOW + token.Gems + C.cGray + " gems for playing!"));
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
