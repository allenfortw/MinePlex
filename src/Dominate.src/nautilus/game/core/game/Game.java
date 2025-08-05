package nautilus.game.core.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.energy.Energy;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.notifier.PlayerNotifier;
import nautilus.game.core.player.IGamePlayer;
import nautilus.minecraft.core.utils.GenericRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public abstract class Game<PlayerType extends IGamePlayer, ArenaType extends IArena> implements IGame<ArenaType, PlayerType>, Listener, Runnable
{
  protected JavaPlugin Plugin;
  protected ClassManager ClassManager;
  protected ConditionManager ConditionManager;
  protected Energy Energy;
  protected ArenaType Arena;
  protected Map<String, PlayerType> Players;
  protected Map<String, PlayerType> Spectators;
  protected int PlayerLives = 1;
  
  protected int ScoreLimit;
  
  protected long StartTime;
  
  protected boolean HasStarted;
  protected boolean CountdownRunning;
  protected HashMap<String, Integer> PlayerTaskIdMap;
  protected int UpdaterTaskId;
  protected PlayerNotifier<IGame<ArenaType, PlayerType>, ArenaType, PlayerType> Notifier;
  private int _countDown;
  
  public Game(JavaPlugin plugin, ClassManager classManager, ConditionManager conditionManager, Energy energy)
  {
    this.Plugin = plugin;
    this.ClassManager = classManager;
    this.ConditionManager = conditionManager;
    this.Energy = energy;
    this.Players = new HashMap();
    this.Spectators = new HashMap();
    
    this.PlayerTaskIdMap = new HashMap();
    
    this.Plugin.getServer().getPluginManager().registerEvents(this, this.Plugin);
  }
  
  public void run()
  {
    Update();
  }
  

  protected void Update() {}
  
  protected abstract PlayerType CreateGamePlayer(Player paramPlayer, int paramInt);
  
  public void RemovePlayer(PlayerType player)
  {
    if (this.Players.containsKey(player.getName()))
    {
      this.Players.remove(player.getName());
      ClearPlayerSettings(player);
    }
  }
  

  public void RemoveSpectator(PlayerType player)
  {
    if (this.Spectators.containsKey(player.getName()))
    {
      this.Spectators.remove(player.getName());
      ClearSpectatorSettings(player);
    }
  }
  

  public boolean IsPlayerInGame(Player player)
  {
    return this.Players.containsKey(player.getName());
  }
  

  public boolean IsSpectatorInGame(Player player)
  {
    return this.Spectators.containsKey(player.getName());
  }
  

  public PlayerType AddPlayerToGame(Player player)
  {
    PlayerType gamePlayer = CreateGamePlayer(player, this.PlayerLives);
    this.Players.put(gamePlayer.getName(), gamePlayer);
    gamePlayer.SetClass((ClientClass)this.ClassManager.Get(player));
    
    return gamePlayer;
  }
  

  public PlayerType AddSpectatorToGame(Player player, Location to)
  {
    PlayerType gamePlayer = CreateGamePlayer(player, this.PlayerLives);
    this.Spectators.put(gamePlayer.getName(), gamePlayer);
    
    gamePlayer.SetSpectating(true);
    gamePlayer.teleport(to);
    gamePlayer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.SHEARS, 1) });
    
    this.ConditionManager.Factory().Cloak("Spectator", player, player, 7200.0D, false, true);
    
    return gamePlayer;
  }
  

  public PlayerType GetPlayer(Player player)
  {
    return (IGamePlayer)this.Players.get(player.getName());
  }
  

  public PlayerType GetPlayer(String playerName)
  {
    return (IGamePlayer)this.Players.get(playerName);
  }
  

  public PlayerType GetSpectator(Player player)
  {
    return (IGamePlayer)this.Spectators.get(player.getName());
  }
  

  public Collection<PlayerType> GetPlayers()
  {
    return this.Players.values();
  }
  

  public Collection<PlayerType> GetSpectators()
  {
    return this.Spectators.values();
  }
  

  public void UpdateReconnectedPlayer(Player player)
  {
    Map.Entry<String, PlayerType> oldPlayerEntry = GetGamePlayer(player.getName());
    
    if (oldPlayerEntry != null)
    {
      PlayerType oldPlayer = (IGamePlayer)oldPlayerEntry.getValue();
      PlayerType newPlayer = CreateGamePlayer(player, oldPlayer.GetRemainingLives());
      
      UpdateNewPlayerWithOldPlayer(newPlayer, oldPlayer);
      
      ClientClass client = (ClientClass)this.ClassManager.Get(player);
      
      if (oldPlayer.GetClass().GetDefaultItems() != null)
      {
        for (Map.Entry<Integer, ItemStack> item : oldPlayer.GetClass().GetDefaultItems().entrySet())
        {
          client.PutDefaultItem((ItemStack)item.getValue(), (Integer)item.getKey());
        }
      }
      
      if (oldPlayer.GetClass().GetDefaultArmor() != null)
      {
        client.SetDefaultHead(oldPlayer.GetClass().GetDefaultArmor()[3]);
        client.SetDefaultChest(oldPlayer.GetClass().GetDefaultArmor()[2]);
        client.SetDefaultLegs(oldPlayer.GetClass().GetDefaultArmor()[1]);
        client.SetDefaultFeet(oldPlayer.GetClass().GetDefaultArmor()[0]);
      }
      
      client.SetGameClass(oldPlayer.GetClass().GetGameClass());
      
      if (oldPlayer.GetClass().GetDefaultSkills() != null)
      {
        client.ClearDefaultSkills();
        
        for (mineplex.minecraft.game.classcombat.Skill.ISkill skill : oldPlayer.GetClass().GetDefaultSkills())
        {
          client.AddSkill(skill);
        }
      }
      
      this.Players.put(player.getName(), newPlayer);
    }
  }
  


  public java.util.List<PlayerType> GetAssailants(Player player)
  {
    return null;
  }
  

  public boolean HasStarted()
  {
    return this.HasStarted;
  }
  

  public boolean IsInArena(Location location)
  {
    return this.Arena.IsInArena(location.toVector());
  }
  

  public boolean CanMove(PlayerType player, Location from, Location to)
  {
    if (this.CountdownRunning) if (!this.Arena.CanMove(player.getName(), from != null ? from.toVector() : null, to != null ? to.toVector() : null)) return false; return true;
  }
  

  public boolean CanInteract(PlayerType player, Block block)
  {
    return (!this.CountdownRunning) || (this.Arena.CanInteract(player.getName(), block));
  }
  
  public void StartRespawnFor(PlayerType player)
  {
    ResetPlayer(player);
    
    player.SetSpectating(true);
    player.SetDead(true);
    
    this.ConditionManager.Factory().Cloak("Death", player.GetPlayer(), player.GetPlayer(), 10.0D, false, true);
    
    this.PlayerTaskIdMap.put(player.getName(), Integer.valueOf(this.Plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.Plugin, new GenericRunnable(player.getName())
    {
      public void run()
      {
        PlayerType player = Game.this.GetPlayer((String)this.t);
        
        if ((player != null) && (player.isOnline()) && (Game.this.HasStarted))
        {
          Game.this.ResetPlayer(player);
          Game.this.RespawnPlayer(player);
          
          player.SetSpectating(false);
          player.SetDead(false);
        }
        
        Game.this.ConditionManager.EndCondition(player.GetPlayer(), Condition.ConditionType.CLOAK, null);
        Game.this.ConditionManager.Factory().Regen("Respawn", player.GetPlayer(), player.GetPlayer(), 7.0D, 3, true, true, true);
        
        Game.this.PlayerTaskIdMap.remove(player.getName());
      }
    }, 200L)));
  }
  
  public void ResetPlayer(PlayerType player)
  {
    player.setFireTicks(0);
    player.setHealth(20.0D);
    player.setVelocity(new Vector(0, 0, 0));
    player.setFoodLevel(20);
    player.GetPlayer().setFallDistance(0.0F);
    player.RemoveArrows();
    player.GetPlayer().eject();
    
    player.getInventory().clear();
    player.getInventory().setArmorContents(new ItemStack[4]);
    
    for (PotionEffect potionEffect : player.GetPlayer().getActivePotionEffects())
    {
      player.GetPlayer().addPotionEffect(new PotionEffect(potionEffect.getType(), 0, 0), true);
    }
    
    if (player.GetPlayer().isOnline())
    {
      ((mineplex.core.energy.ClientEnergy)this.Energy.Get(player.GetPlayer())).Energy = this.Energy.GetMax(player.GetPlayer());
      ((ClientClass)this.ClassManager.Get(player.GetPlayer())).ResetSkills(player.GetPlayer());
    }
  }
  
  public void Activate(ArenaType arena)
  {
    this.Arena = arena;
    this.Notifier = new PlayerNotifier(this.Plugin, "Dominate");
    
    this._countDown = 30;
    this.CountdownRunning = true;
  }
  
  @EventHandler
  public void updateStartTimer(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SEC) {
      return;
    }
    if (!this.CountdownRunning) {
      return;
    }
    if (this._countDown == 0)
    {
      ReallyStartGame();
      this.CountdownRunning = false;
    }
    else if (this._countDown == 1)
    {
      this.Notifier.BroadcastMessageToPlayers("Game starting in 1 second.", GetPlayers());
    }
    else if (this._countDown < 11)
    {
      this.Notifier.BroadcastMessageToPlayers("Game starting in " + this._countDown + " seconds.", GetPlayers());
    }
    else if (this._countDown == 15)
    {
      this.Notifier.BroadcastMessageToPlayers("Game starting in " + this._countDown + " seconds.", GetPlayers());
    }
    else if (this._countDown == 30)
    {
      this.Notifier.BroadcastMessageToPlayers("Game starting in " + this._countDown + " seconds.", GetPlayers());
    }
    else if (this._countDown == 45)
    {
      this.Notifier.BroadcastMessageToPlayers("Game starting in " + this._countDown + " seconds.", GetPlayers());
    }
    
    this._countDown -= 1;
  }
  
  public boolean IsActive()
  {
    return (this.CountdownRunning) || (this.HasStarted);
  }
  
  protected void ReallyStartGame()
  {
    this.StartTime = System.currentTimeMillis();
    this.HasStarted = true;
    
    for (Entity entity : this.Arena.GetWorld().getEntitiesByClasses(new Class[] { org.bukkit.entity.Item.class, Arrow.class }))
    {
      if (this.Arena.IsInArena(entity.getLocation().toVector()))
      {
        entity.remove();
      }
    }
    
    this.UpdaterTaskId = this.Plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.Plugin, this, 0L, 10L);
  }
  
  public void Deactivate()
  {
    for (PlayerType player : this.Players.values())
    {
      ClearPlayerSettings(player);
    }
    
    for (PlayerType spectator : this.Spectators.values())
    {
      ClearSpectatorSettings(spectator);
    }
    
    this.Players.clear();
    this.Spectators.clear();
    
    this.Plugin.getServer().getScheduler().cancelTask(this.UpdaterTaskId);
    HandlerList.unregisterAll(this);
    
    this.Arena = null;
    this.Plugin = null;
    this.Players = null;
    this.Spectators = null;
    this.HasStarted = false;
  }
  
  protected void ClearSpectatorSettings(PlayerType spectator)
  {
    this.ConditionManager.EndCondition(spectator.GetPlayer(), Condition.ConditionType.CLOAK, null);
    spectator.SetSpectating(false);
  }
  
  protected void ClearPlayerSettings(PlayerType player) {}
  
  protected Map.Entry<String, PlayerType> GetGamePlayer(String playerName)
  {
    for (Map.Entry<String, PlayerType> player : this.Players.entrySet())
    {
      if (((String)player.getKey()).equalsIgnoreCase(playerName))
      {
        return player;
      }
    }
    
    return null;
  }
  

  public ArenaType GetArena()
  {
    return this.Arena;
  }
  

  public long GetStartTime()
  {
    return this.StartTime;
  }
  

  public int GetWinLimit()
  {
    return this.ScoreLimit;
  }
  
  protected void UpdateNewPlayerWithOldPlayer(PlayerType newPlayer, PlayerType oldPlayer)
  {
    newPlayer.SetLives(oldPlayer.GetRemainingLives());
    newPlayer.AddKills(oldPlayer.GetKills());
    newPlayer.AddAssists(oldPlayer.GetAssists());
    newPlayer.AddDeaths(oldPlayer.GetDeaths());
    newPlayer.AddPoints(oldPlayer.GetPoints());
    newPlayer.SetLogoutTime(oldPlayer.GetLogoutTime());
    newPlayer.SetDead(oldPlayer.IsDead());
    newPlayer.SetSpectating(oldPlayer.IsSpectating());
    newPlayer.SetClass(oldPlayer.GetClass());
    Location lastInArenaPosition = oldPlayer.GetLastInArenaPosition();
    
    if (lastInArenaPosition != null)
    {
      newPlayer.SetLastInArenaPosition(lastInArenaPosition.getWorld(), lastInArenaPosition.getX(), lastInArenaPosition.getY(), lastInArenaPosition.getZ());
    }
  }
}
