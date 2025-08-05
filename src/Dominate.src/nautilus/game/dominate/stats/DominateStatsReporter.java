package nautilus.game.dominate.stats;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.donation.repository.token.GemRewardToken;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.GamePlayerAfkEvent;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.dominate.engine.IDominateGame;
import nautilus.game.dominate.engine.IDominateTeam;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.repository.DominateRepository;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DominateStatsReporter implements Listener
{
  private JavaPlugin _plugin;
  private DonationManager _donationManager;
  private DominateRepository _repository;
  
  public DominateStatsReporter(JavaPlugin plugin, DonationManager donationManager, String webServer)
  {
    this._plugin = plugin;
    this._donationManager = donationManager;
    this._plugin.getServer().getPluginManager().registerEvents(this, this._plugin);
    this._repository = new DominateRepository(webServer);
  }
  













  @EventHandler
  public void OnGamePlayerAFK(GamePlayerAfkEvent<IDominateGame, IDominatePlayer> event) {}
  













  @EventHandler
  public void OnGameFinished(TeamGameFinishedEvent<IDominateGame, IDominateTeam, IDominatePlayer> event)
  {
    DominateGameStatsToken gameStats = new DominateGameStatsToken();
    gameStats.Duration = (((IDominateGame)event.GetGame()).GetStartTime() - System.currentTimeMillis());
    gameStats.PlayerStats = new java.util.ArrayList();
    boolean redTeamWon = ((IDominateTeam)((IDominateGame)event.GetGame()).GetRedTeam()).GetScore() > ((IDominateTeam)((IDominateGame)event.GetGame()).GetBlueTeam()).GetScore();
    
    for (IDominatePlayer player : ((IDominateGame)event.GetGame()).GetPlayers())
    {
      DominatePlayerStatsToken playerStats = new DominatePlayerStatsToken();
      playerStats.Name = player.getName();
      playerStats.PlayerStats = new DominatePlayerStats();
      
      playerStats.Won = (redTeamWon == (((IDominateTeam)player.GetTeam()).GetTeamType() == TeamType.RED));
      playerStats.PlayerStats.Kills = player.GetKills();
      playerStats.PlayerStats.Deaths = player.GetDeaths();
      playerStats.PlayerStats.Assists = player.GetAssists();
      playerStats.PlayerStats.Points = player.GetPoints();
      
      gameStats.PlayerStats.add(playerStats);
    }
    
    this._repository.SaveGameStats(new mineplex.core.common.util.Callback() {
      public void run(List<GemRewardToken> tokenList) { int j;
        int i;
        label214:
        for (Iterator localIterator = tokenList.iterator(); localIterator.hasNext(); 
            
















            i < j)
        {
          GemRewardToken token = (GemRewardToken)localIterator.next();
          StackTraceElement[] arrayOfStackTraceElement;
          try
          {
            Player player = DominateStatsReporter.this._plugin.getServer().getPlayerExact(token.Name);
            Donor donor = DominateStatsReporter.this._donationManager.Get(token.Name);
            
            if ((player == null) || (!player.isOnline()))
              break label214;
            donor.AddGems(token.Amount);
            
            player.sendMessage(F.main("Dominate", "You earned " + ChatColor.YELLOW + token.Amount + C.cGray + " gems for playing!"));

          }
          catch (Exception ex)
          {
            System.out.println("Error updating player with token : " + token.Name + "\n" + ex.getMessage());
            
            j = (arrayOfStackTraceElement = ex.getStackTrace()).length;i = 0; continue; } StackTraceElement trace = arrayOfStackTraceElement[i];
          
          System.out.println(trace);i++;

        }
        
      }
      

    }, gameStats);
  }
}
