package nautilus.game.core.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import me.chiss.Core.Events.ServerSaveEvent;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.creature.event.CreatureSpawnCustomEvent;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.portal.Portal;
import mineplex.core.server.ServerTalker;
import mineplex.core.server.event.PlayerGameAssignmentEvent;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.updater.event.RestartServerEvent;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.shop.ClassCombatPurchaseShop;
import mineplex.minecraft.game.core.combat.ClientCombat;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.ClearCombatEvent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.events.GamePlayerAfkEvent;
import nautilus.game.core.events.GamePlayerAttackedPlayerEvent;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.GamePlayerQuitEvent;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;
import nautilus.game.core.scoreboard.IScoreHandler;
import nautilus.minecraft.core.event.AfkEvent;
import nautilus.minecraft.core.player.AfkMonitor;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.minecraft.server.v1_6_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GameEngine<GameType extends IGame<ArenaType, PlayerType>, ScoreHandlerType extends IScoreHandler<PlayerType>, ArenaType extends IArena, PlayerType extends IGamePlayer> implements IGameEngine<GameType, ArenaType, PlayerType>, org.bukkit.event.Listener, mineplex.minecraft.game.core.IRelation
{
  protected int MaxGames = -1;
  
  protected JavaPlugin Plugin;
  
  protected World World;
  
  protected Location SpawnLocation;
  
  protected CoreClientManager ClientManager;
  
  protected ClassManager ClassManager;
  protected ConditionManager ConditionManager;
  protected Energy Energy;
  protected ArenaManager<ArenaType> ArenaManager;
  protected GameScheduler<GameType> Scheduler;
  protected ScoreHandlerType ScoreHandler;
  protected HashMap<String, Integer> PlayerTaskIdMap;
  protected HashMap<String, Long> PendingPlayerRemoveMap;
  protected HashMap<String, GameType> PlayerGameMap;
  protected HashMap<String, GameType> SpectatorGameMap;
  protected List<GameType> ActiveGames;
  protected List<GameType> GamesInSetup;
  protected AfkMonitor AfkMonitor;
  protected CraftItemStack StartBook;
  protected ShopItem LobbyGem;
  protected mineplex.minecraft.game.classcombat.shop.ClassCombatShop GameShop;
  protected ClassCombatPurchaseShop DonationShop;
  protected mineplex.minecraft.game.classcombat.shop.ClassCombatCustomBuildShop CustomBuildShop;
  protected int LogoutPeriod = 30;
  
  protected boolean AddToActiveGame;
  
  protected Portal Portal;
  
  protected HashSet<String> ConnectingPlayersToAddToGame;
  
  protected ServerTalker HubConnection;
  
  public GameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classFactory, ConditionManager conditionManager, Energy energy, ArenaManager<ArenaType> arenaManager, ScoreHandlerType scoreHandler, World world, Location spawnLocation)
  {
    this.Plugin = plugin;
    this.HubConnection = hubConnection;
    this.ClientManager = clientManager;
    this.ClassManager = classFactory;
    this.ConditionManager = conditionManager;
    this.Energy = energy;
    
    this.World = world;
    this.SpawnLocation = spawnLocation;
    this.ArenaManager = arenaManager;
    this.Scheduler = new GameScheduler();
    this.ScoreHandler = scoreHandler;
    this.PlayerTaskIdMap = new HashMap();
    
    this.PendingPlayerRemoveMap = new HashMap();
    this.PlayerGameMap = new HashMap();
    this.SpectatorGameMap = new HashMap();
    this.ActiveGames = new ArrayList();
    this.GamesInSetup = new ArrayList();
    this.AfkMonitor = new AfkMonitor(plugin);
    
    this.ConnectingPlayersToAddToGame = new HashSet();
    
    this.Portal = new Portal(plugin);
    
    this.Plugin.getServer().getPluginManager().registerEvents(this, this.Plugin);
    
    this.StartBook = CraftItemStack.asNewCraftStack(net.minecraft.server.v1_6_R3.Item.WRITTEN_BOOK);
    NBTTagCompound bookData = this.StartBook.getHandle().tag;
    
    if (bookData == null) {
      bookData = new NBTTagCompound("tag");
    }
    bookData.setString("title", ChatColor.GREEN + ChatColor.BOLD + "Instructions");
    
    NBTTagList nPages = new NBTTagList();
    nPages.add(new NBTTagString("1", ChatColor.DARK_GREEN + "         DOMINATE" + 
      ChatColor.DARK_GRAY + "\n\n\n   Table of Contents" + 
      ChatColor.DARK_GRAY + "\n\n1. " + ChatColor.BLUE + "Signup to play" + 
      ChatColor.DARK_GRAY + "\n2. " + ChatColor.BLUE + "Setting up a class" + 
      ChatColor.DARK_GRAY + "\n3. " + ChatColor.BLUE + "Choosing class" + 
      ChatColor.DARK_GRAY + "\n4. " + ChatColor.BLUE + "Skills" + 
      ChatColor.DARK_GRAY + "\n5. " + ChatColor.BLUE + "How to Play" + 
      ChatColor.DARK_GRAY + "\n6. " + ChatColor.BLUE + "Tips"));
    nPages.add(new NBTTagString("a", ChatColor.DARK_GREEN + "          SIGNUP" + 
      ChatColor.BLUE + "\n\nClick with dye in your hand. It will turn green." + 
      "\n\nGame will start when 10 players sign up." + 
      "\n\nIf you wish to leave queue, click with dye in your hand again. It will go back to gray"));
    nPages.add(new NBTTagString("2", ChatColor.DARK_GREEN + "       CLASS SETUP" + 
      ChatColor.BLUE + "\n\nRight-click on the Enchantment Table." + 
      "\n\nLeft-click class and Anvil to setup a custom build." + 
      "\n\nChoose skills, weapons and items."));
    nPages.add(new NBTTagString("3", ChatColor.DARK_GREEN + "      CLASS IN-GAME" + 
      ChatColor.BLUE + "\n\nRight-click on the Enchantment Table." + 
      "\n\nLeft-click class." + 
      "\n\nLeft-click dye to select a custom build." + 
      "\n\nLeft-click anvil to edit build or workbench modify without saving build."));
    nPages.add(new NBTTagString("4", ChatColor.DARK_GREEN + "          SKILLS" + 
      ChatColor.BLUE + "\n\nOpen inventory and mouse over the 'Skill Hotbar' to see how each skill works." + 
      "\n\nActive skills are attached to weapons." + 
      "\n\nPassive skills are active all the time."));
    nPages.add(new NBTTagString("5", ChatColor.DARK_GREEN + "      HOW TO PLAY" + 
      ChatColor.BLUE + "\n\nEach control point is marked by a beacon." + 
      "\n\nTo capture, stand on the glass flooring of the point until the wool floor underneath the glass is your team's color."));
    nPages.add(new NBTTagString("6", ChatColor.DARK_GREEN + "          TIPS" + 
      ChatColor.BLUE + "\n\n1. Gather emeralds for an additional " + ChatColor.DARK_GREEN + "+100" + ChatColor.BLUE + " to your score." + 
      "\n\n2. Defend control points for " + ChatColor.DARK_GREEN + "+2" + ChatColor.BLUE + " score per second" + 
      "\n\n3. Watch scoreboard by holding TAB to see what points the enemy team controls."));
    
    bookData.set("pages", nPages);
    
    this.StartBook.getHandle().tag = bookData;
    
    this.LobbyGem = new ShopItem(Material.EYE_OF_ENDER, "Lobby Teleport", new String[] { "§rClick with this in hand to teleport back to lobby." }, 1, false, true);
  }
  
  protected abstract void ActivateGame(GameType paramGameType, ArenaType paramArenaType);
  
  public abstract GameType ScheduleNewGame();
  
  public abstract String GetGameType();
  
  public void StopGame(GameType game)
  {
    for (PlayerType player : game.GetPlayers())
    {
      RemovePlayerFromGame(game, player, !player.isOnline());
      
      player.setFireTicks(0);
      player.setHealth(20.0D);
      player.setFoodLevel(20);
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      
      player.SetDead(false);
      player.SetSpectating(false);
      
      player.GetPlayer().eject();
      
      if (player.GetPlayer().isInsideVehicle()) {
        player.GetPlayer().leaveVehicle();
      }
      player.teleport(this.SpawnLocation);
      
      ClearCombatEvent clearCombatEvent = new ClearCombatEvent(player.GetPlayer());
      this.Plugin.getServer().getPluginManager().callEvent(clearCombatEvent);
    }
    
    for (PlayerType player : game.GetSpectators())
    {
      RemoveSpectatorFromGame(player.GetPlayer(), !player.isOnline(), false);
      
      player.setFireTicks(0);
      player.setHealth(20.0D);
      player.setFoodLevel(20);
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      
      player.SetDead(false);
      player.SetSpectating(false);
      
      player.GetPlayer().eject();
      
      if (player.GetPlayer().isInsideVehicle()) {
        player.GetPlayer().leaveVehicle();
      }
      player.teleport(this.SpawnLocation);
      
      ClearCombatEvent clearCombatEvent = new ClearCombatEvent(player.GetPlayer());
      this.Plugin.getServer().getPluginManager().callEvent(clearCombatEvent);
    }
    
    this.ActiveGames.remove(game);
    ArenaType arena = game.GetArena();
    

    game.Deactivate();
    
    this.ArenaManager.RemoveArena(arena);
    TryToActivateGames();
    
    this.Plugin.getServer().shutdown();
  }
  
  public Boolean CanScheduleNewGame()
  {
    if ((this.Scheduler.GetGames().size() > 0) && (((IGame)this.Scheduler.GetGames().get(this.Scheduler.GetGames().size() - 1)).GetPlayers().size() < 10))
    {
      return Boolean.valueOf(false);
    }
    
    if (!this.ArenaManager.HasAvailableArena())
    {
      return Boolean.valueOf(false);
    }
    
    return Boolean.valueOf(true);
  }
  
  protected abstract void TryToActivateGames();
  
  protected boolean AddPlayerToGame(GameType game, Player player, Boolean notify)
  {
    PlayerType gamePlayer = game.AddPlayerToGame(player);
    this.PlayerGameMap.put(gamePlayer.getName(), game);
    
    notify.booleanValue();
    



    TryToActivateGames();
    


    return true;
  }
  
  public void AddSpectatorToGame(GameType game, Player player, Location location)
  {
    if (IsPlayerInActiveGame(player)) {
      return;
    }
    if (IsSpectatorInGame(player))
    {
      if (this.SpectatorGameMap.get(player.getName()) != game) {
        RemoveSpectatorFromGame(player, false);
      }
      else {
        player.teleport(location);
        return;
      }
    }
    
    PlayerType spectator = game.AddSpectatorToGame(player, location);
    this.SpectatorGameMap.put(spectator.getName(), game);
  }
  

  public boolean AddPlayerToGame(Player player, boolean notify)
  {
    GameType game = GetNextOpenGame();
    
    if (game == null)
    {
      return false;
    }
    
    return AddPlayerToGame(game, player, Boolean.valueOf(notify));
  }
  

  public List<GameType> GetGames()
  {
    return this.Scheduler.GetGames();
  }
  

  public List<GameType> GetActiveGames()
  {
    return this.ActiveGames;
  }
  

  public void RemovePlayerFromGame(Player player)
  {
    RemovePlayerFromGame(player, false);
  }
  

  public void RemovePlayerFromGame(Player player, boolean quit)
  {
    GameType game = GetGameForPlayer(player);
    PlayerType gamePlayer = game.GetPlayer(player);
    
    RemovePlayerFromGame(game, gamePlayer, quit);
    
    player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 0.5F, 0.5F);
  }
  
  protected void RemovePlayerFromGame(GameType game, PlayerType player, boolean quit)
  {
    this.PlayerGameMap.remove(player.getName());
    
    if (!this.ActiveGames.contains(game))
    {
      game.RemovePlayer(player);
      this.Scheduler.CleanEmptyGames();
    }
  }
  
  public void RemoveSpectatorFromGame(Player player, boolean quit)
  {
    RemoveSpectatorFromGame(player, quit, true);
  }
  
  public void RemoveSpectatorFromGame(Player player, boolean quit, boolean removeAndTeleport)
  {
    GameType game = (IGame)this.SpectatorGameMap.get(player.getName());
    this.SpectatorGameMap.remove(player.getName());
    
    if (removeAndTeleport)
    {
      game.RemoveSpectator(game.GetSpectator(player));
      
      player.eject();
      player.leaveVehicle();
      player.teleport(this.SpawnLocation);
    }
  }
  
  public GameType GetNextOpenGame()
  {
    if ((this.ActiveGames.size() > 0) && (this.AddToActiveGame) && (!IsGameFull((IGame)this.ActiveGames.get(0))))
    {
      return (IGame)this.ActiveGames.get(0);
    }
    if (this.Scheduler.GetGames().size() > 0)
    {
      return (IGame)this.Scheduler.GetGames().get(this.Scheduler.GetGames().size() - 1);
    }
    
    return ScheduleNewGame();
  }
  
  protected boolean IsGameFull(GameType gameType)
  {
    return false;
  }
  

  public boolean IsPlayerInGame(Player player)
  {
    return this.PlayerGameMap.containsKey(player.getName());
  }
  
  public boolean IsPlayerInGame(String playerName)
  {
    return this.PlayerGameMap.containsKey(playerName);
  }
  
  @EventHandler
  public void restartServerCheck(RestartServerEvent event)
  {
    if ((this.ActiveGames.size() > 0) || (this.GamesInSetup.size() > 0)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void MessageMOTD(ServerListPingEvent event) {
    String extrainformation = "|Competitive|" + GetGameType() + (GetActiveGames().size() != 0 ? "|" + ((IGame)GetActiveGames().get(0)).GetArena().GetName() : "");
    
    if ((this.ActiveGames.size() > 0) || (this.GamesInSetup.size() > 0))
    {
      event.setMotd(ChatColor.YELLOW + "In Progress" + extrainformation);
    }
    else
    {
      event.setMotd(ChatColor.GREEN + "Recruiting" + extrainformation);
    }
    
    event.setMaxPlayers(10);
  }
  
  @EventHandler
  public void PlayerGameRequest(PlayerGameAssignmentEvent event)
  {
    this.ConnectingPlayersToAddToGame.add(event.GetPlayerName());
    
    if (this.ConnectingPlayersToAddToGame.size() >= 10)
    {
      this.HubConnection.QueuePacket(new mineplex.core.server.packet.GameReadyPacket(new ArrayList(this.ConnectingPlayersToAddToGame)));
    }
  }
  

  public boolean IsSpectatorInActiveGame(Player player)
  {
    return (IsSpectatorInGame(player)) && (this.ActiveGames.contains(GetGameForSpectator(player)));
  }
  

  public boolean IsPlayerInActiveGame(String playerName)
  {
    return (IsPlayerInGame(playerName)) && (this.ActiveGames.contains(GetGameForPlayer(playerName)));
  }
  

  public GameType GetGameForPlayer(Player player)
  {
    return GetGameForPlayer(player.getName());
  }
  

  public GameType GetGameForPlayer(String playerName)
  {
    if (this.PlayerGameMap.containsKey(playerName)) {
      return (IGame)this.PlayerGameMap.get(playerName);
    }
    return (IGame)this.SpectatorGameMap.get(playerName);
  }
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnCustomEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void OnWeatherChange(WeatherChangeEvent event)
  {
    if (event.toWeatherState())
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onEntityDamage(EntityDamageEvent event)
  {
    if ((event.getEntity() instanceof Player))
    {
      if (IsPlayerInActiveGame((Player)event.getEntity()))
      {
        GameType game = GetGameForPlayer((Player)event.getEntity());
        
        if ((game.HasStarted()) && ((event.getEntity().getLastDamageCause() == null) || ((event.getEntity().getLastDamageCause().getEntity() instanceof Player))))
        {
          PlayerType victim = game.GetPlayer((Player)event.getEntity());
          PlayerType attacker = null;
          
          if ((victim.IsDead()) || (victim.IsSpectating())) {
            event.setCancelled(true);
          }
          if ((event instanceof EntityDamageByEntityEvent))
          {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
            Entity damager = entityEvent.getDamager();
            
            if (entityEvent != null)
            {
              if ((damager instanceof Projectile))
              {
                damager = ((Projectile)entityEvent.getDamager()).getShooter();
              }
              
              if ((damager instanceof Player))
              {
                if (IsPlayerInActiveGame((Player)damager)) {
                  attacker = game.GetPlayer((Player)damager);
                } else if (IsSpectatorInGame((Player)damager)) {
                  attacker = game.GetSpectator((Player)damager);
                }
              }
            }
          }
          if (attacker != null)
          {
            if ((attacker.IsDead()) || (attacker.IsSpectating()))
            {
              event.setCancelled(true);
            }
            else
            {
              GamePlayerAttackedPlayerEvent<GameType, PlayerType> customEvent = new GamePlayerAttackedPlayerEvent(game, attacker, victim);
              this.Plugin.getServer().getPluginManager().callEvent(customEvent);
              
              if (customEvent.isCancelled())
              {
                event.setCancelled(true);
              }
            }
          }
        }
        else
        {
          event.setCancelled(true);
        }
      }
      else
      {
        Player victim = (Player)event.getEntity();
        Player attacker = null;
        
        if ((event instanceof EntityDamageByEntityEvent))
        {
          EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
          Entity damager = entityEvent.getDamager();
          
          if (entityEvent != null)
          {
            if ((damager instanceof Projectile))
            {
              damager = ((Projectile)entityEvent.getDamager()).getShooter();
            }
            
            if ((damager instanceof Player))
            {
              attacker = (Player)damager;
            }
          }
        }
        
        if ((attacker != null) && (victim.isOp()) && (attacker.isOp()))
        {
          return;
        }
        
        event.setCancelled(true);
      }
      
    }
    else {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onEntityTarget(EntityTargetEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onEntityDeath(CombatDeathEvent event)
  {
    if ((event.GetEvent().getEntity() instanceof Player))
    {
      Player victim = (Player)event.GetEvent().getEntity();
      
      if (!IsPlayerInGame(victim))
      {
        return;
      }
      
      GameType game = GetGameForPlayer(victim);
      
      this.World.playSound(victim.getLocation(), Sound.BREATH, 0.5F, 0.3F);
      
      PlayerType gamekiller = null;
      List<PlayerType> assistants = new ArrayList();
      
      if (!event.GetClientCombat().GetDeaths().isEmpty())
      {
        CombatLog victimCombatLog = (CombatLog)event.GetClientCombat().GetDeaths().getFirst();
        List<CombatComponent> attackers = victimCombatLog.GetAttackers();
        
        for (CombatComponent component : attackers)
        {
          if (component != victimCombatLog.GetKiller())
          {

            if (component.IsPlayer())
            {

              Player player = Bukkit.getPlayer(component.GetName());
              
              if (player != null)
              {

                PlayerType assistant = game.GetPlayer(player);
                
                if (assistant != null)
                {
                  assistant.AddAssists(1);
                  assistants.add(assistant);
                }
              }
            } } }
        if ((victimCombatLog.GetKiller() != null) && (Bukkit.getPlayer(victimCombatLog.GetKiller().GetName()) != null))
        {
          gamekiller = game.GetPlayer(Bukkit.getPlayer(victimCombatLog.GetKiller().GetName()));
          
          if (gamekiller != null) {
            gamekiller.AddKills(1);
          } else {
            System.out.println("Null gameplayer for " + victimCombatLog.GetKiller().GetName());
          }
        }
      }
      PlayerType gameVictim = game.GetPlayer(victim);
      gameVictim.AddDeaths(1);
      
      GamePlayerDeathEvent<GameType, PlayerType> customEvent = new GamePlayerDeathEvent(game, gameVictim, gamekiller, assistants, event.GetEvent().getDrops(), event);
      this.Plugin.getServer().getPluginManager().callEvent(customEvent);
      event.GetEvent().getDrops().clear();
    }
    
    if ((event.GetEvent() instanceof PlayerDeathEvent))
    {
      PlayerDeathEvent pDeathEvent = (PlayerDeathEvent)event.GetEvent();
      
      pDeathEvent.setDeathMessage("");
    }
  }
  
  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent event)
  {
    if ((IsPlayerInActiveGame(event.getPlayer())) || (IsSpectatorInActiveGame(event.getPlayer())))
    {
      event.setCancelled(true);
    }
    else if (!event.getPlayer().isOp())
    {
      event.getPlayer().teleport(this.SpawnLocation);
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent event)
  {
    if (IsPlayerInActiveGame(event.getPlayer()))
    {
      event.setCancelled(true);
    }
    else if (!event.getPlayer().isOp()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    if (IsPlayerInActiveGame(event.getPlayer()))
    {
      event.setCancelled(true);
    }
    else if (!event.getPlayer().isOp()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    if (IsPlayerInActiveGame(event.getPlayer()))
    {
      event.setCancelled(true);
    }
    else if (!event.getPlayer().isOp()) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void BurnCancel(BlockBurnEvent event) {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void SpreadCancel(BlockFromToEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void GrowCancel(BlockGrowEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void DecayCancel(LeavesDecayEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void onPlayerFoodBarChange(FoodLevelChangeEvent event)
  {
    if (!IsPlayerInActiveGame((Player)event.getEntity()))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent event)
  {
    if ((event.getResult() != PlayerLoginEvent.Result.KICK_BANNED) && (IsPlayerInGame(event.getPlayer())))
    {
      event.allow();
      return;
    }
    
    if ((this.ActiveGames.size() > 0) || (this.GamesInSetup.size() > 0))
    {
      event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Game in progress");
    }
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    
    if (IsPlayerInGame(player))
    {
      GameType game = GetGameForPlayer(player);
      
      event.getPlayer().setNoDamageTicks(0);
      game.UpdateReconnectedPlayer(event.getPlayer());
      
      PlayerType gamePlayer = game.GetPlayer(player);
      
      if ((this.ActiveGames.contains(game)) && (gamePlayer.GetPlayer().getWorld() != game.GetArena().GetWorld()))
      {
        gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
      }
      
      if (this.ActiveGames.contains(game)) {
        gamePlayer.StartTimePlay();
      }
      onGamePlayerJoin(game, gamePlayer);
    }
    else if (this.AddToActiveGame)
    {
      player.getInventory().setItem(1, ItemStackFactory.Instance.CreateStack(Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
        new String[] { "", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub." }));
      
      AddPlayerToGame(event.getPlayer(), false);
      
      player.setGameMode(GameMode.SURVIVAL);
      player.getInventory().clear();
      player.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
      
      player.teleport(this.SpawnLocation);
    }
    else
    {
      player.setGameMode(GameMode.SURVIVAL);
      player.getInventory().clear();
      player.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
      
      player.teleport(this.SpawnLocation);
      
      player.sendMessage(ChatColor.AQUA + "*************************************");
      player.sendMessage(C.cDGreen + "           Welcome to " + GetGameType() + "!");
      player.sendMessage(C.cDGreen + "  Right click book for instructions ");
      player.sendMessage(ChatColor.AQUA + "*************************************");
    }
  }
  
  @EventHandler
  public void onPlayerAfk(AfkEvent event)
  {
    if (IsPlayerInActiveGame(event.GetPlayerName()))
    {
      GameType game = GetGameForPlayer(event.GetPlayerName());
      PlayerType gamePlayer = game.GetPlayer(event.GetPlayerName());
      
      this.Plugin.getServer().getPluginManager().callEvent(new GamePlayerAfkEvent(game, gamePlayer));









    }
    else if (IsPlayerInGame(event.GetPlayerName()))
    {
      Player player = Bukkit.getPlayerExact(event.GetPlayerName());
      
      if (player != null)
      {
        RemovePlayerFromGame(Bukkit.getPlayerExact(event.GetPlayerName()));
        player.sendMessage(ChatColor.RED + "You were removed from the Play Queue for being afk.");
        this.Portal.SendPlayerToServer(player, "Lobby");
      }
    }
  }
  

  @EventHandler(priority=EventPriority.NORMAL)
  public void onPlayerDropItem(PlayerDropItemEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void onPlayerCommand(PlayerCommandPreprocessEvent event)
  {
    if ((event.getMessage().startsWith("/spectate")) || (event.getMessage().startsWith("/spec")))
    {
      if (IsPlayerInActiveGame(event.getPlayer()))
      {
        event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + "You are already in a game, doh."));
      }
      else
      {
        String[] messageSplit = event.getMessage().split(" ");
        
        if (messageSplit.length == 2)
        {
          Player player = mineplex.core.common.util.UtilPlayer.searchOnline(event.getPlayer(), messageSplit[1], true);
          
          if (player != null)
          {
            if (IsPlayerInActiveGame(player))
            {
              AddSpectatorToGame(GetGameForPlayer(player), event.getPlayer(), player.getLocation());
            }
            else
            {
              event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + player.getName() + " isn't in a game."));
            }
          }
        }
        else if (IsSpectatorInGame(event.getPlayer()))
        {
          RemoveSpectatorFromGame(event.getPlayer(), false);
        }
        else
        {
          event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + "Incorrect syntax.  Example : '/spectate defek7' or '/spectate' to leave"));
        }
      }
      
      event.setCancelled(true);
    }
    else if (event.getMessage().trim().equalsIgnoreCase("/stopgame"))
    {
      if (this.ClientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), Rank.MODERATOR, true))
      {
        GameType game = null;
        
        if (IsSpectatorInGame(event.getPlayer()))
        {
          game = GetGameForSpectator(event.getPlayer());
        }
        else if (IsPlayerInActiveGame(event.getPlayer()))
        {
          game = GetGameForPlayer(event.getPlayer());
        }
        
        if (game != null)
        {
          for (PlayerType player : game.GetPlayers())
          {
            player.sendMessage(ChatColor.RED + event.getPlayer().getName() + " stopped the game.");
          }
          
          StopGame(game);
        }
        else {
          event.getPlayer().sendMessage(ChatColor.RED + "You must be in or spectating an active game to issue '/stopgame'.");
        }
        event.setCancelled(true);
      }
    }
    else if (event.getMessage().startsWith("/spawn"))
    {
      if ((!IsPlayerInActiveGame(event.getPlayer())) && (!IsSpectatorInActiveGame(event.getPlayer())))
      {
        event.getPlayer().teleport(this.SpawnLocation);
      }
      
      event.setCancelled(true);
    }
  }
  

  @EventHandler(priority=EventPriority.NORMAL)
  public void onPlayerPickupItem(PlayerPickupItemEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((!IsPlayerInActiveGame(event.getPlayer())) || (IsSpectatorInActiveGame(event.getPlayer())) || (event.getItem().getItemStack().getType() == Material.BONE))
    {
      event.setCancelled(true);
    }
    else
    {
      PlayerType gamePlayer = GetGameForPlayer(event.getPlayer()).GetPlayer(event.getPlayer());
      
      if ((gamePlayer.IsDead()) || (gamePlayer.IsSpectating()))
      {
        event.setCancelled(true);
        return;
      }
      
      if (event.getItem().getItemStack().getType() != Material.ARROW)
      {
        event.setCancelled(true);
        event.getItem().remove();
        return;
      }
      
      PlayerInventory inventory = event.getPlayer().getInventory();
      int typeSlot = inventory.first(event.getItem().getItemStack().getType());
      org.bukkit.inventory.ItemStack itemStack = event.getItem().getItemStack();
      
      if (typeSlot != -1)
      {
        org.bukkit.inventory.ItemStack firstFoundItemStack = inventory.getItem(typeSlot);
        int pickupAmount = itemStack.getAmount();
        int maxAmount = firstFoundItemStack.getType() == Material.ARROW ? 64 : firstFoundItemStack.getType() == Material.NETHER_STAR ? 4 : 1;
        
        while (typeSlot < 9)
        {
          org.bukkit.inventory.ItemStack existingItemStack = inventory.getItem(typeSlot);
          
          if ((existingItemStack != null) && (existingItemStack.getType() == itemStack.getType()))
          {
            int existingAmount = existingItemStack.getAmount() + pickupAmount;
            
            if (existingAmount > maxAmount)
            {
              pickupAmount = existingAmount - maxAmount;
              existingAmount = maxAmount;
            }
            else
            {
              pickupAmount = 0;
            }
            
            inventory.getItem(typeSlot).setAmount(existingAmount);
            existingAmount = 0;
            
            if (pickupAmount == 0) {
              break;
            }
          }
          typeSlot++;
        }
        
        if (pickupAmount > 0)
        {
          org.bukkit.inventory.ItemStack newItemStack = firstFoundItemStack.clone();
          int existingAmount = pickupAmount;
          maxAmount = newItemStack.getType() == Material.ARROW ? 64 : newItemStack.getType() == Material.NETHER_STAR ? 4 : 1;
          
          while (inventory.firstEmpty() < 9)
          {
            if (existingAmount > maxAmount)
            {
              pickupAmount = existingAmount - maxAmount;
              existingAmount = maxAmount;
            }
            else
            {
              pickupAmount = 0;
            }
            
            newItemStack.setAmount(existingAmount);
            inventory.setItem(inventory.firstEmpty(), newItemStack);
            existingAmount = pickupAmount;
            
            if (pickupAmount == 0) {
              break;
            }
            newItemStack = firstFoundItemStack.clone();
          }
        }
        
        event.setCancelled(true);
        
        if (pickupAmount == 0) {
          event.getItem().remove();
        }
      }
      if (inventory.firstEmpty() > 8)
      {
        event.setCancelled(true);
      }
      
      event.getPlayer().updateInventory();
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    
    if (IsSpectatorInGame(player))
    {
      RemoveSpectatorFromGame(player, true);
    }
    
    if (IsPlayerInGame(player))
    {
      GameType game = GetGameForPlayer(player);
      PlayerType gamePlayer = game.GetPlayer(player);
      
      if ((game.IsActive()) || (this.GamesInSetup.contains(game)))
      {
        GamePlayerQuitEvent<GameType, PlayerType> customEvent = new GamePlayerQuitEvent(game, gamePlayer);
        this.Plugin.getServer().getPluginManager().callEvent(customEvent);
        
        gamePlayer.StopTimePlay();
        
        boolean online = false;
        
        for (PlayerType otherPlayer : game.GetPlayers())
        {
          if (gamePlayer != otherPlayer)
          {

            if (otherPlayer.isOnline())
            {
              online = true;
              break;
            }
          }
        }
        if ((!online) || (Bukkit.getOnlinePlayers().length <= 1)) {
          this.Plugin.getServer().shutdown();
        }
      }
      else {
        RemovePlayerFromGame(player, true);
      }
    }
  }
  
  private GameType GetGameForSpectator(Player player)
  {
    return (IGame)this.SpectatorGameMap.get(player.getName());
  }
  
  private boolean IsSpectatorInGame(Player player)
  {
    return this.SpectatorGameMap.containsKey(player.getName());
  }
  

  public boolean IsPlayerInActiveGame(Player player)
  {
    return (IsPlayerInGame(player)) && (this.ActiveGames.contains(GetGameForPlayer(player)));
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerInventoryClick(InventoryClickEvent event)
  {
    if ((IsPlayerInActiveGame((Player)event.getWhoClicked())) || (IsSpectatorInActiveGame((Player)event.getWhoClicked())))
    {
      if ((event.getSlotType() != InventoryType.SlotType.QUICKBAR) || (event.isShiftClick()) || (event.getCursor().getType() == event.getCurrentItem().getType()))
      {
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (IsSpectatorInActiveGame(player))
    {
      event.setCancelled(true);
      return;
    }
    
    if (event.getAction() == Action.PHYSICAL)
    {
      if (event.getClickedBlock().getType() == Material.SOIL)
      {
        event.setCancelled(true);
        return;
      }
    }
    
    if (IsPlayerInActiveGame(player))
    {
      if (player.getItemInHand() != null)
      {
        if (player.getItemInHand().getType() == Material.BUCKET)
        {
          event.setCancelled(true);
        }
        else if ((player.getItemInHand().getType() == Material.FIREBALL) || (player.getItemInHand().getType() == Material.FIRE))
        {
          event.setCancelled(true);
        }
      }
    }
    else
    {
      if (player.getItemInHand().getType() == Material.WATCH)
      {
        this.Portal.SendPlayerToServer(player, "Lobby");
      }
      
      if (player.isOp())
      {
        player.getItemInHand().getType();
        
































































        if (player.getItemInHand().getType() == Material.BLAZE_ROD)
        {
          if (event.getAction() == Action.LEFT_CLICK_AIR)
          {
            player.setItemInHand(new org.bukkit.inventory.ItemStack(Material.BLAZE_ROD, player.getItemInHand().getAmount() + 1));
            int index = player.getItemInHand().getAmount() - 1;
            player.sendMessage("Sound." + Sound.values()[index]);
          }
          else if (event.getAction() == Action.RIGHT_CLICK_AIR)
          {
            player.setItemInHand(new org.bukkit.inventory.ItemStack(Material.BLAZE_ROD, player.getItemInHand().getAmount() - 1));
            
            int index = player.getItemInHand().getAmount() - 1;
            player.sendMessage("Sound." + Sound.values()[index]);
          }
        }
        
        if (player.getItemInHand().getType() == Material.STICK)
        {
          if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_AIR))
          {
            player.damage(1.0D, Bukkit.getPlayer("Chiss"));
            return;
          }
        }
        
        if (player.getItemInHand().getType() == Material.STRING)
        {
          if (event.getAction() == Action.LEFT_CLICK_AIR)
          {
            player.setItemInHand(new org.bukkit.inventory.ItemStack(Material.STRING, player.getItemInHand().getAmount() + 1));
          }
          else if (event.getAction() == Action.RIGHT_CLICK_AIR)
          {
            player.setItemInHand(new org.bukkit.inventory.ItemStack(Material.STRING, player.getItemInHand().getAmount() - 1));
          }
        }
        
        if (player.getItemInHand().getType() == Material.JUKEBOX)
        {
          if (player.getInventory().getItem(0).getAmount() - 1 > Sound.values().length)
          {
            player.getInventory().setItem(0, new org.bukkit.inventory.ItemStack(Material.BLAZE_ROD, 0));
          }
          else if (player.getInventory().getItem(0).getAmount() - 1 < 0)
          {
            player.getInventory().setItem(0, new org.bukkit.inventory.ItemStack(Material.BLAZE_ROD, Sound.values().length));
          }
          
          if (player.getInventory().getItem(1).getAmount() - 1 > 30)
          {
            player.getInventory().setItem(1, new org.bukkit.inventory.ItemStack(Material.STRING, 0));
          }
          else if (player.getInventory().getItem(1).getAmount() - 1 < 0)
          {
            player.getInventory().setItem(1, new org.bukkit.inventory.ItemStack(Material.STRING, 10));
          }
          
          int index = player.getInventory().getItem(0).getAmount() - 1;
          float pitch = (player.getInventory().getItem(1).getAmount() - 1) * 0.1F;
          
          System.out.println("index: " + index + " pitch: " + pitch);
          
          player.getWorld().playSound(player.getLocation(), Sound.values()[index], 0.3F, pitch);
          player.sendMessage("Sound." + Sound.values()[index]);
        }
      }
      
      if (player.getItemInHand().getType() == Material.EYE_OF_ENDER)
      {
        this.Portal.SendPlayerToServer(player, "lobby");
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    
    if ((IsPlayerInActiveGame(player)) || (IsSpectatorInActiveGame(player)))
    {
      GameType game = GetGameForPlayer(player);
      
      if (game == null) {
        game = GetGameForSpectator(player);
      }
      PlayerType gamePlayer = game.GetPlayer(player);
      
      if (gamePlayer == null) {
        gamePlayer = game.GetSpectator(player);
      }
      if (!game.CanMove(gamePlayer, event.getFrom(), event.getTo()))
      {
        if (game.IsInArena(event.getFrom()))
        {
          gamePlayer.SetLastInArenaPosition(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
        }
        
        player.eject();
        player.leaveVehicle();
        
        gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
        
        if ((game.HasStarted()) && (!gamePlayer.IsDead())) {
          gamePlayer.GetPlayer().damage(4.0D);
        }
      } else {
        gamePlayer.SetLastInArenaPosition(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    
    if ((IsPlayerInActiveGame(player)) || (IsSpectatorInActiveGame(player)))
    {
      GameType game = GetGameForPlayer(player);
      
      if (game == null) {
        game = GetGameForSpectator(player);
      }
      PlayerType gamePlayer = game.GetPlayer(player);
      
      if (gamePlayer == null) {
        gamePlayer = game.GetSpectator(player);
      }
      if (!game.CanMove(gamePlayer, event.getFrom(), event.getTo()))
      {
        if (game.IsInArena(event.getFrom()))
        {
          gamePlayer.SetLastInArenaPosition(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
        }
        
        event.setCancelled(true);
        gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
      }
      else
      {
        gamePlayer.SetLastInArenaPosition(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
      }
    }
    else if (event.getTo() == this.SpawnLocation)
    {
      GivePlayerLobbyItems(player);
    }
  }
  




































  @EventHandler
  public void onServerSaveEvent(ServerSaveEvent event)
  {
    event.setCancelled(true);
  }
  
  public void onGamePlayerJoin(GameType game, PlayerType gamePlayer)
  {
    if (game.HasStarted())
    {
      this.ScoreHandler.RewardForDeath(gamePlayer);
      
      if (!this.PlayerTaskIdMap.containsKey(gamePlayer.getName()))
      {
        game.RespawnPlayer(gamePlayer);
        game.ResetPlayer(gamePlayer);
        
        gamePlayer.SetSpectating(false);
        gamePlayer.SetDead(false);
        
        this.ConditionManager.EndCondition(gamePlayer.GetPlayer(), mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, null);
        this.ConditionManager.Factory().Regen("Respawn", gamePlayer.GetPlayer(), gamePlayer.GetPlayer(), 7.0D, 1, false, true, true);
      }
    }
  }
  
  protected void GivePlayerLobbyItems(Player player)
  {
    player.getInventory().clear();
    player.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
    
    player.getInventory().setItem(1, ItemStackFactory.Instance.CreateStack(Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
      new String[] { "", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub." }));
  }
  































  public boolean IsInLobby(Player player)
  {
    return (!IsPlayerInActiveGame(player)) && (!IsSpectatorInActiveGame(player));
  }
  
  public boolean CanHurt(Player a, Player b)
  {
    if ((IsPlayerInActiveGame(b)) && (IsPlayerInActiveGame(a)))
    {
      GameType game = GetGameForPlayer(b);
      
      if (game.HasStarted())
      {
        PlayerType victim = game.GetPlayer(b);
        
        if ((victim.IsDead()) || (victim.IsSpectating())) {
          return false;
        }
      }
    }
    return true;
  }
  
  public ChatColor GetColorOfFor(String other, Player player)
  {
    ChatColor prefixColor = null;
    
    if (IsPlayerInGame(other))
    {
      prefixColor = ChatColor.GREEN;
    }
    else if ((this.ClientManager.Get(other) != null) && (this.ClientManager.Get(other).GetRank().Has(player, Rank.ADMIN, false)))
    {
      prefixColor = ChatColor.DARK_RED;
    }
    else if ((this.ClientManager.Get(other) != null) && (this.ClientManager.Get(other).GetRank().Has(player, Rank.MODERATOR, false)))
    {
      prefixColor = ChatColor.RED;
    }
    else
    {
      prefixColor = ChatColor.YELLOW;
    }
    
    return prefixColor;
  }
  
  public boolean CanHurt(String a, String b)
  {
    return CanHurt(Bukkit.getPlayerExact(a), Bukkit.getPlayerExact(b));
  }
  

  public boolean IsSafe(Player player)
  {
    return false;
  }
}
