package nautilus.game.dominate.engine;

import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.server.ServerTalker;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.engine.GameScheduler;
import nautilus.game.core.engine.TeamGameEngine;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.scoreboard.DominateScoreHandler;
import nautilus.game.dominate.scoreboard.IDominateScoreHandler;
import nautilus.game.dominate.stats.DominateStatsReporter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class DominateGameEngine
  extends TeamGameEngine<IDominateGame, IDominateScoreHandler, IDominateArena, IDominateTeam, IDominatePlayer> implements IDominateGameEngine
{
  private DominateNotifier _notifier;
  
  public DominateGameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classManager, ConditionManager conditionManager, Energy energy, NpcManager npcManager, DominateNotifier notifier, PacketHandler packetHandler, ArenaManager<IDominateArena> arenaManager, World world, Location spawnLocation, String webServerAddress)
  {
    super(plugin, hubConnection, clientManager, donationManager, classManager, conditionManager, energy, npcManager, packetHandler, arenaManager, new DominateScoreHandler(plugin, notifier), world, spawnLocation);
    
    this._notifier = notifier;
    new DominateStatsReporter(plugin, donationManager, webServerAddress);
    
    this.MinQueuePlayersToStart = 4;
    this.TeamSize = 5;
    this.AddToActiveGame = true;
  }
  

  public void ActivateGame(IDominateGame game, IDominateArena arena)
  {
    super.ActivateGame(game, arena);
    
    for (IDominatePlayer dominatePlayer : game.GetPlayers())
    {
      if (dominatePlayer.isOnline())
      {
        ClientClass clientClass = (ClientClass)this.ClassManager.Get(dominatePlayer.GetPlayer());
        
        clientClass.ResetToDefaults(true, true);
      }
    }
  }
  

  public IDominateGame ScheduleNewGame()
  {
    return (IDominateGame)this.Scheduler.ScheduleNewGame(new DominateGame(this.Plugin, this.ClientManager, this.ClassManager, this.ConditionManager, this.Energy, this._notifier, this.PacketHandler));
  }
  

  public String GetGameType()
  {
    return "Dominate";
  }
}
