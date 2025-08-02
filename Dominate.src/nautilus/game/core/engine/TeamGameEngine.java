package nautilus.game.core.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.npc.NpcManager;
import mineplex.core.packethandler.PacketHandler;
import mineplex.core.server.ServerTalker;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.events.GamePlayerAttackedPlayerEvent;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;
import nautilus.game.core.scoreboard.ITeamScoreHandler;
import nautilus.minecraft.core.utils.GenericRunnable;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public abstract class TeamGameEngine<GameType extends ITeamGame<ArenaType, PlayerType, PlayerTeamType>, ScoreHandlerType extends ITeamScoreHandler<PlayerType, PlayerTeamType>, ArenaType extends ITeamArena, PlayerTeamType extends ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>> extends GameEngine<GameType, ScoreHandlerType, ArenaType, PlayerType> implements ITeamGameEngine<GameType, ArenaType, PlayerTeamType, PlayerType>
{
  private int _gameQueueTickValue;
  protected int TeamSize = 1;
  
  protected PacketHandler PacketHandler;
  protected boolean BroadcastQueueJoinMessage = true;
  protected int MinQueuePlayersToStart = 4;
  protected int TimeToStart = 2;
  
  protected NpcManager NpcManager;
  
  private List<Entity> _shopEntities = new ArrayList();
  private NautHashMap<Player, Scoreboard> _scoreboardMap = new NautHashMap();
  

  public TeamGameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classManager, ConditionManager conditionManager, Energy energy, NpcManager npcManager, PacketHandler packetHandler, ArenaManager<ArenaType> arenaManager, ScoreHandlerType scoreHandler, World world, Location spawnLocation)
  {
    super(plugin, hubConnection, clientManager, donationManager, classManager, conditionManager, energy, arenaManager, scoreHandler, world, spawnLocation);
    
    this.NpcManager = npcManager;
    this.PacketHandler = packetHandler;
  }
  
  protected void TryToActivateGames()
  {
    Iterator<GameType> gameIterator = this.Scheduler.GetGames().iterator();
    
    while (gameIterator.hasNext())
    {
      GameType game = (ITeamGame)gameIterator.next();
      
      if ((game.GetPlayers().size() == this.TeamSize * 2) && (this.ArenaManager.HasAvailableArena()) && ((this.MaxGames == -1) || (this.ActiveGames.size() < this.MaxGames)))
      {
        SetupGame(game);
        gameIterator.remove();
        this._gameQueueTickValue = 0;
      }
    }
  }
  
  protected boolean IsGameFull(GameType gameType)
  {
    return gameType.GetPlayers().size() >= this.TeamSize * 2;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void PlayerJoin(PlayerJoinEvent event)
  {
    CreateScoreboard(event.getPlayer());
  }
  
  private void CreateScoreboard(Player player)
  {
    this._scoreboardMap.put(player, Bukkit.getScoreboardManager().getNewScoreboard());
    
    Scoreboard scoreboard = (Scoreboard)this._scoreboardMap.get(player);
    Objective objective = scoreboard.registerNewObjective("§lScore", "dummy");
    objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
    
    for (Rank rank : Rank.values())
    {
      if (rank == Rank.ALL)
      {
        scoreboard.registerNewTeam(rank.Name).setPrefix("");
      }
      else
      {
        scoreboard.registerNewTeam(rank.Name).setPrefix(rank.GetTag(true, false) + ChatColor.RESET + " " + ChatColor.WHITE);
      }
      
      if (rank == Rank.ALL)
      {
        scoreboard.registerNewTeam(rank.Name + "red".toUpperCase()).setPrefix(ChatColor.RED);
        scoreboard.registerNewTeam(rank.Name + "blue".toUpperCase()).setPrefix(ChatColor.BLUE);
      }
      else
      {
        scoreboard.registerNewTeam(rank.Name + "red".toUpperCase()).setPrefix(rank.GetTag(true, false) + ChatColor.RESET + " " + ChatColor.RED);
        scoreboard.registerNewTeam(rank.Name + "blue".toUpperCase()).setPrefix(rank.GetTag(true, false) + ChatColor.RESET + " " + ChatColor.BLUE);
      }
    }
    
    player.setScoreboard(scoreboard);
    
    for (Player otherPlayer : mineplex.core.common.util.UtilServer.getPlayers())
    {
      AddPlayerToScoreboards(otherPlayer, ((ITeamGame)GetGameForPlayer(otherPlayer)).IsActive() ? ((ITeamGamePlayer)((ITeamGame)GetGameForPlayer(otherPlayer)).GetPlayer(otherPlayer)).GetTeam().GetTeamType().name() : null);
    }
  }
  
  public Collection<Scoreboard> GetScoreboards()
  {
    return this._scoreboardMap.values();
  }
  
  public void AddPlayerToScoreboards(Player player, String teamName) {
    Iterator localIterator2;
    for (Iterator localIterator1 = GetScoreboards().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Scoreboard scoreboard = (Scoreboard)localIterator1.next();
      
      localIterator2 = scoreboard.getTeams().iterator(); continue;Team team = (Team)localIterator2.next();
      team.removePlayer(player);
    }
    
    if (teamName == null) {
      teamName = "";
    }
    for (Scoreboard scoreboard : GetScoreboards())
    {
      scoreboard.getTeam(this.ClientManager.Get(player).GetRank().Name + teamName).addPlayer(player);
    }
  }
  
  @EventHandler
  public void UpdateGameScoreboards(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      Iterator localIterator2;
      for (Iterator localIterator1 = this.ActiveGames.iterator(); localIterator1.hasNext(); 
          
          localIterator2.hasNext())
      {
        GameType activeGame = (ITeamGame)localIterator1.next();
        
        localIterator2 = activeGame.GetPlayers().iterator(); continue;PlayerType player = (ITeamGamePlayer)localIterator2.next();
        
        Scoreboard scoreboard = (Scoreboard)this._scoreboardMap.get(player.GetPlayer());
        
        Objective objective = scoreboard.getObjective("§lScore");
        objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue")).setScore(activeGame.GetBlueTeam().GetScore());
        objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Red")).setScore(activeGame.GetRedTeam().GetScore());
      }
    }
  }
  


  @EventHandler
  public void updateStartTimer(UpdateEvent event)
  {
    if (event.getType() != UpdateType.MIN_01) {
      return;
    }
    if (this.Scheduler.GetGames().size() > 0)
    {
      GameType game = (ITeamGame)this.Scheduler.GetGames().get(this.Scheduler.GetGames().size() - 1);
      int players = game.GetPlayers().size();
      
      if ((players < this.TeamSize * 2) && (players >= this.MinQueuePlayersToStart))
      {
        this._gameQueueTickValue += 1;
        
        if (this._gameQueueTickValue > this.TimeToStart)
        {
          this.TeamSize = (players / 2);
          TryToActivateGames();
        }
        else if (this.BroadcastQueueJoinMessage)
        {
          for (Player gameplayer : ((ITeamGamePlayer)game.GetBlueTeam().GetPlayers().get(0)).GetPlayer().getWorld().getPlayers())
          {
            String minuteMessage = 3 - this._gameQueueTickValue == 1 ? "minute" : "minutes";
            gameplayer.sendMessage(mineplex.core.common.util.F.main("Play Queue", "Game will start in " + (3 - this._gameQueueTickValue) + " " + minuteMessage + "!"));
          }
        }
      }
    }
  }
  
  @EventHandler
  public void onGamePlayerDeath(GamePlayerDeathEvent<GameType, PlayerType> event)
  {
    GameType game = (ITeamGame)event.GetGame();
    StackTraceElement[] arrayOfStackTraceElement1;
    int j;
    int i;
    try {
      if (((ITeamGamePlayer)event.GetPlayer()).GetTeam().GetTeamType() == TeamType.RED) {
        event.GetLog().SetKilledColor(C.cRed);
      } else {
        event.GetLog().SetKilledColor(C.cBlue);
      }
    }
    catch (Exception e) {
      System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Setting Killed Color : " + e.getMessage());
      
      j = (arrayOfStackTraceElement1 = e.getStackTrace()).length;i = 0; } for (; i < j; i++) { StackTraceElement trace = arrayOfStackTraceElement1[i];
      
      System.out.println(trace);
    }
    

    try
    {
      if (event.GetLog().GetKiller() == null)
        break label318;
      PlayerType killer = (ITeamGamePlayer)game.GetPlayer(event.GetLog().GetKiller().GetName());
      
      if (killer.GetTeam().GetTeamType() == TeamType.RED) {
        event.GetLog().SetKillerColor(C.cRed);
      } else {
        event.GetLog().SetKillerColor(C.cBlue);
      }
    }
    catch (Exception e)
    {
      System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Setting Killed Color  : " + e.getMessage());
      
      j = (arrayOfStackTraceElement1 = e.getStackTrace()).length;i = 0; } for (; i < j; i++) { StackTraceElement trace = arrayOfStackTraceElement1[i];
      
      System.out.println(trace);
    }
    
    label318:
    ((ITeamScoreHandler)this.ScoreHandler).RewardForDeath((ITeamGamePlayer)event.GetPlayer());
    
    boolean first = true;
    Object attacker;
    StackTraceElement[] arrayOfStackTraceElement2;
    PlayerType ?;
    PlayerType ?; try { for (CombatComponent source : event.GetLog().GetAttackers())
      {
        if ((source != null) && (source.IsPlayer()))
        {

          attacker = (ITeamGamePlayer)game.GetPlayer(source.GetName());
          
          if (attacker != null)
          {

            if (first)
            {
              if (((ITeamGamePlayer)attacker).GetTeam() == ((ITeamGamePlayer)event.GetPlayer()).GetTeam())
              {
                ((ITeamScoreHandler)this.ScoreHandler).RewardForTeamKill((ITeamGamePlayer)attacker, (ITeamGamePlayer)event.GetPlayer());
              }
              else
              {
                ((ITeamScoreHandler)this.ScoreHandler).RewardForKill((ITeamGamePlayer)attacker, (ITeamGamePlayer)event.GetPlayer(), event.GetLog().GetAssists());
              }
              
              first = false;
            }
            else
            {
              ((ITeamScoreHandler)this.ScoreHandler).RewardForAssist((ITeamGamePlayer)attacker, (ITeamGamePlayer)event.GetPlayer());
            }
          }
        }
      }
    } catch (Exception ex) {
      System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Handleing team kill/kill/assist  : " + ex.getMessage());
      
      ? = (arrayOfStackTraceElement2 = ex.getStackTrace()).length;? = 0; } for (; ? < ?; ?++) { StackTraceElement trace = arrayOfStackTraceElement2[?];
      
      System.out.println(trace);
    }
    

    game.StartRespawnFor((ITeamGamePlayer)event.GetPlayer());
  }
  
  private void AddPlayerToTeam(GameType game, Player player, PlayerTeamType team, Boolean notify)
  {
    PlayerType gamePlayer = (ITeamGamePlayer)game.AddPlayerToGame(player);
    this.PlayerGameMap.put(gamePlayer.getName(), game);
    
    team.AddPlayer(gamePlayer);
    
    if (this.ActiveGames.contains(game))
    {
      player.eject();
      
      if (player.isInsideVehicle()) {
        player.leaveVehicle();
      }
      for (Player otherPlayer : this.Plugin.getServer().getOnlinePlayers())
      {
        if (player != otherPlayer)
        {
          player.showPlayer(otherPlayer);
        }
      }
      
      game.ActivatePlayer(gamePlayer);
    }
    
    TryToActivateGames();
  }
  

  public boolean AddPlayerToGame(Player player, boolean notify)
  {
    GameType game = (ITeamGame)GetNextOpenGame();
    
    if (game == null)
    {
      return false;
    }
    
    int redTeamSize = game.GetRedTeam().GetPlayers().size();
    int blueTeamSize = game.GetBlueTeam().GetPlayers().size();
    
    if (blueTeamSize < redTeamSize)
    {
      AddPlayerToBlueTeam(game, player);
      player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.5F, 0.5F);
    }
    else if (redTeamSize < blueTeamSize)
    {
      AddPlayerToRedTeam(game, player);
      player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.5F, 0.5F);
    }
    else if (blueTeamSize < this.TeamSize)
    {
      AddPlayerToBlueTeam(game, player);
      player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, 0.5F, 0.5F);
    }
    else
    {
      System.out.println("Error neither blue nor red have < " + this.TeamSize + " players and " + player.getName() + " shouldn't be assigned to this game.");
      return false;
    }
    
    if ((game.GetPlayers().size() < this.TeamSize * 2) && (this.BroadcastQueueJoinMessage))
    {
      for (Player gameplayer : player.getWorld().getPlayers())
      {
        gameplayer.sendMessage(ChatColor.BLUE + "Play Queue> " + ChatColor.GRAY + "Only " + (this.TeamSize * 2 - game.GetPlayers().size()) + " more players needed to start game!");
      }
    }
    
    return true;
  }
  
  public void AddPlayerToRedTeam(GameType game, Player player)
  {
    AddPlayerToRedTeam(game, player, Boolean.valueOf(true));
  }
  
  public void AddPlayerToBlueTeam(GameType game, Player player)
  {
    AddPlayerToBlueTeam(game, player, Boolean.valueOf(true));
  }
  
  public void AddPlayerToRedTeam(GameType game, Player player, Boolean notify)
  {
    AddPlayerToTeam(game, player, game.GetRedTeam(), notify);
  }
  
  public void AddPlayerToBlueTeam(GameType game, Player player, Boolean notify)
  {
    AddPlayerToTeam(game, player, game.GetBlueTeam(), notify);
  }
  
  public void SetupGame(final GameType game)
  {
    this.GamesInSetup.add(game);
    
    for (PlayerType gamePlayer : game.GetPlayers())
    {
      if (IsSpectatorInActiveGame(gamePlayer.GetPlayer())) {
        RemoveSpectatorFromGame(gamePlayer.GetPlayer(), true);
      }
      gamePlayer.sendMessage(ChatColor.BLUE + "Dominate>" + ChatColor.GRAY + " Preparing map...");
    }
    
    this.ArenaManager.GetNextArena(new mineplex.core.common.util.Callback()
    {
      public void run(ArenaType arena)
      {
        TeamGameEngine.this.ActivateGame(game, arena);
      }
    });
  }
  

  protected void RemovePlayerFromGame(GameType game, PlayerType player, boolean quit)
  {
    super.RemovePlayerFromGame(game, player, quit);
    
    if ((!this.ActiveGames.contains(game)) && (game != null) && (game.GetRedTeam() != null) && (game.GetBlueTeam() != null))
    {
      int redTeamSize = game.GetRedTeam().GetPlayers().size();
      int blueTeamSize = game.GetBlueTeam().GetPlayers().size();
      
      if (redTeamSize - blueTeamSize > 1)
      {
        PlayerType movingPlayer = (ITeamGamePlayer)game.GetRedTeam().GetPlayers().get(0);
        
        game.GetRedTeam().RemovePlayer(movingPlayer);
        game.GetBlueTeam().AddPlayer(movingPlayer);
      }
      else if (blueTeamSize - redTeamSize > 1)
      {
        PlayerType movingPlayer = (ITeamGamePlayer)game.GetBlueTeam().GetPlayers().get(0);
        
        game.GetBlueTeam().RemovePlayer(movingPlayer);
        game.GetRedTeam().AddPlayer(movingPlayer);
      }
    }
  }
  

  public void ActivateGame(GameType game, ArenaType arena)
  {
    this.GamesInSetup.remove(game);
    this.ActiveGames.add(game);
    
    List<Packet> removeListPacket = new ArrayList();
    
    for (PlayerType player : game.GetPlayers())
    {
      if (player.isOnline())
      {
        player.GetPlayer().eject();
        
        if (player.GetPlayer().isInsideVehicle()) {
          player.GetPlayer().leaveVehicle();
        }
        removeListPacket.add(new net.minecraft.server.v1_6_R3.Packet201PlayerInfo(player.getName(), false, -9999));
      }
    }
    
    for (PlayerType player : game.GetPlayers())
    {
      if (player.isOnline())
      {
        for (Packet packet : removeListPacket)
        {
          ((CraftPlayer)player.GetPlayer()).getHandle().playerConnection.sendPacket(packet);
        }
        
        AddPlayerToScoreboards(player.GetPlayer(), player.GetTeam().GetTeamType().name());
      }
    }
    
    removeListPacket.clear();
    
    game.Activate(arena);
    
    for (Location location : arena.GetBlueShopPoints())
    {
      this._shopEntities.add(this.NpcManager.AddNpc(EntityType.ZOMBIE, 0, ChatColor.BLUE + "Select Class Here", location));
    }
    
    for (Location location : arena.GetRedShopPoints())
    {
      this._shopEntities.add(this.NpcManager.AddNpc(EntityType.ZOMBIE, 0, ChatColor.RED + "Select Class Here", location)); }
    int j;
    int i;
    for (??? = game.GetPlayers().iterator(); ???.hasNext(); 
        
        i < j)
    {
      PlayerType player = (ITeamGamePlayer)???.next();
      Player[] arrayOfPlayer;
      j = (arrayOfPlayer = this.Plugin.getServer().getOnlinePlayers()).length;i = 0; continue;Player otherPlayer = arrayOfPlayer[i];
      
      if (player.GetPlayer() != otherPlayer)
      {
        player.GetPlayer().showPlayer(otherPlayer);
      }
      i++;
    }
  }
  







  public void StopGame(GameType game)
  {
    for (Entity entity : this._shopEntities)
    {
      this.NpcManager.DeleteNpc(entity);
    }
    
    super.StopGame(game);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onGameFinished(final TeamGameFinishedEvent<GameType, PlayerTeamType, PlayerType> event)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.Plugin, new GenericRunnable((ITeamGame)event.GetGame())
    {
      public void run()
      {
        for (PlayerType player : ((ITeamGame)event.GetGame()).GetPlayers())
        {
          TeamGameEngine.this.Portal.SendPlayerToServer(player.GetPlayer(), "Lobby");
        }
      }
    }, 100L);
    
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.Plugin, new GenericRunnable((ITeamGame)event.GetGame())
    {
      public void run()
      {
        TeamGameEngine.this.StopGame((ITeamGame)this.t);
      }
    }, 300L);
  }
  
  @EventHandler
  public void onGamePlayerAttackedPlayerEvent(GamePlayerAttackedPlayerEvent<GameType, PlayerType> event)
  {
    PlayerType attacker = (ITeamGamePlayer)event.GetAttacker();
    PlayerType victim = (ITeamGamePlayer)event.GetVictim();
    
    if (attacker.GetTeam() == victim.GetTeam())
    {
      event.setCancelled(true);
    }
    else if ((victim.IsDead()) || (attacker.IsDead()))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HandleChat(AsyncPlayerChatEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player sender = event.getPlayer();
    String message = event.getMessage();
    
    if (message.length() < 1) {
      return;
    }
    if (!IsPlayerInActiveGame(sender))
    {
      Iterator<Player> recipientIterator = event.getRecipients().iterator();
      
      while (recipientIterator.hasNext())
      {
        Player otherPlayer = (Player)recipientIterator.next();
        
        if (IsPlayerInActiveGame(otherPlayer)) {
          recipientIterator.remove();
        }
      }
      StringBuilder playerNameBuilder = new StringBuilder();
      
      if (this.ClientManager.Get(sender.getName()) != null)
      {
        CoreClient client = this.ClientManager.Get(sender.getName());
        
        if (client.GetRank().Has(Rank.OWNER))
        {
          playerNameBuilder.append(ChatColor.DARK_RED);
        }
        else if (client.GetRank().Has(Rank.MODERATOR))
        {
          playerNameBuilder.append(ChatColor.RED);
        }
        else if (client.GetRank().Has(Rank.ULTRA))
        {
          playerNameBuilder.append(ChatColor.GOLD);
        }
        else
        {
          playerNameBuilder.append(ChatColor.YELLOW);
        }
      }
      
      event.setFormat(playerNameBuilder.toString() + "%1$s " + C.cWhite + "%2$s");
    }
    else
    {
      GameType senderGame = (ITeamGame)GetGameForPlayer(sender);
      TeamType senderTeamType = ((ITeamGamePlayer)senderGame.GetPlayer(sender)).GetTeam().GetTeamType();
      
      String teamColor = senderTeamType == TeamType.RED ? C.cRed : C.cBlue;
      
      boolean globalMessage = false;
      if (message.charAt(0) == '!')
      {
        globalMessage = true;
        event.setMessage(message.substring(1, message.length()));
        event.setFormat(C.cDGray + "@" + C.cDAqua + "All" + " " + teamColor + "%1$s " + C.cWhite + "%2$s");
      }
      else
      {
        event.setFormat(C.cDGray + "@" + C.cDAqua + "Team" + " " + teamColor + "%1$s " + C.cWhite + "%2$s");
      }
      
      Iterator<Player> recipientIterator = event.getRecipients().iterator();
      
      while (recipientIterator.hasNext())
      {
        Player receiver = (Player)recipientIterator.next();
        
        if ((!IsPlayerInActiveGame(receiver)) || (GetGameForPlayer(receiver) != senderGame))
        {
          recipientIterator.remove();


        }
        else if ((!globalMessage) && (senderTeamType != ((ITeamGamePlayer)senderGame.GetPlayer(receiver)).GetTeam().GetTeamType())) {
          recipientIterator.remove();
        }
      }
    }
  }
  
  public boolean CanHurt(Player a, Player b)
  {
    if ((IsPlayerInActiveGame(b)) && (IsPlayerInActiveGame(a)))
    {
      GameType game = (ITeamGame)GetGameForPlayer(b);
      
      if (game.HasStarted())
      {
        PlayerType victim = (ITeamGamePlayer)game.GetPlayer(b);
        PlayerType attacker = (ITeamGamePlayer)game.GetPlayer(a);
        
        if ((victim.IsDead()) || (victim.IsSpectating())) {
          return false;
        }
        if ((attacker == null) || (attacker.GetTeam() == victim.GetTeam())) {
          return false;
        }
      }
    }
    return true;
  }
  

  public ChatColor GetColorOfFor(String other, Player player)
  {
    ChatColor prefixColor = super.GetColorOfFor(other, player);
    
    if (IsPlayerInActiveGame(other))
    {
      PlayerTeamType playerTeam = ((ITeamGamePlayer)((ITeamGame)GetGameForPlayer(other)).GetPlayer(other)).GetTeam();
      
      prefixColor = playerTeam.GetTeamType() == TeamType.RED ? ChatColor.RED : ChatColor.BLUE;
    }
    
    return prefixColor;
  }
}
