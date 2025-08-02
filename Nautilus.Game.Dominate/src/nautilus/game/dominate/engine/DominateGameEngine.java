package nautilus.game.dominate.engine;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.account.CoreClientManager;
import mineplex.minecraft.game.core.condition.*;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.npc.*;
import mineplex.core.server.ServerTalker;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.engine.TeamGameEngine;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.scoreboard.DominateScoreHandler;
import nautilus.game.dominate.scoreboard.IDominateScoreHandler;
import nautilus.game.dominate.stats.DominateStatsReporter;

public class DominateGameEngine extends TeamGameEngine<IDominateGame, IDominateScoreHandler, IDominateArena, IDominateTeam, IDominatePlayer> implements IDominateGameEngine
{
    private DominateNotifier _notifier;
    
    public DominateGameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classManager, 
    				ConditionManager conditionManager, Energy energy, NpcManager npcManager, DominateNotifier notifier, PacketHandler packetHandler, ArenaManager<IDominateArena> arenaManager, World world, Location spawnLocation, String webServerAddress)
    {
        super(plugin, hubConnection, clientManager, donationManager, classManager, conditionManager, energy, npcManager, packetHandler, arenaManager, new DominateScoreHandler(plugin, notifier), world, spawnLocation);
        
        _notifier = notifier;
        new DominateStatsReporter(plugin, donationManager, webServerAddress);
        
        TeamSize = 5;
        AddToActiveGame = true;
    }
    
    @Override
    public void ActivateGame(IDominateGame game, IDominateArena arena)
    {
        super.ActivateGame(game, arena);
        
        for (IDominatePlayer dominatePlayer : game.GetPlayers())
        {
            ClientClass clientClass = ClassManager.Get(dominatePlayer.GetPlayer());
            
            clientClass.ResetToDefaults(true, true);
        }
    }

    @Override
    public IDominateGame ScheduleNewGame()
    {
        return Scheduler.ScheduleNewGame(new DominateGame(Plugin, ClientManager, ClassManager, ConditionManager, Energy, _notifier, PacketHandler));
    }

    @Override
    public String GetGameType()
    {
        return "Dominate";
    }
}
