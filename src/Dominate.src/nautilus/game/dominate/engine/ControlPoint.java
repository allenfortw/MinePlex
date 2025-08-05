package nautilus.game.dominate.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mineplex.core.common.util.MapUtil;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.GamePlayerJoinedEvent;
import nautilus.game.core.events.GamePlayerQuitEvent;
import nautilus.game.dominate.events.ControlPointLostEvent;
import nautilus.game.dominate.player.IDominatePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ControlPoint implements IControlPoint, Listener
{
  private JavaPlugin _plugin;
  private IDominateGame _game;
  private DominateNotifier _notifier;
  private World _world;
  private Region _captureRegion;
  private IDominateTeam _capturingTeam = null;
  private int _captureValue;
  private int _captureThreshold;
  private int _incrementValue;
  private List<IDominatePlayer> _capturers;
  private Boolean _captured;
  private IDominateTeam _ownerTeam = null;
  
  private int _pointValue;
  
  private boolean _captureVisualChange;
  private List<Vector> _ownedLandVisualRegionBlocks;
  private List<Vector> _neutralLandVisualRegionBlocks;
  private Random _random;
  private Location _middlePoint;
  private List<Location> _topCornerPoints;
  private boolean _captureTeamChanged;
  private boolean _alternateColor;
  boolean Locked;
  String Message;
  private int _defaultPointValue = 3;
  
  public ControlPoint(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, World world, Region captureSquare, String message)
  {
    this._plugin = plugin;
    this._game = game;
    this._notifier = notifier;
    this._world = world;
    this._captureRegion = captureSquare;
    this._capturers = new ArrayList();
    this.Message = (ChatColor.GREEN + message + ChatColor.GRAY);
    this._captured = Boolean.valueOf(false);
    this._incrementValue = 0;
    this._captureValue = 0;
    this._captureThreshold = 20;
    this._pointValue = this._defaultPointValue;
    this._neutralLandVisualRegionBlocks = new ArrayList();
    this._ownedLandVisualRegionBlocks = new ArrayList();
    this._random = new Random();
    this._topCornerPoints = new ArrayList();
    
    SetupVisuals();
    
    this._plugin.getServer().getPluginManager().registerEvents(this, this._plugin);
  }
  
  protected void SetupVisuals()
  {
    Vector minPoint = this._captureRegion.GetMinimumPoint();
    Vector maxPoint = this._captureRegion.GetMaximumPoint();
    
    Vector middleVector = minPoint.getMidpoint(maxPoint.clone().setY(minPoint.getBlockY()));
    this._middlePoint = new Location(this._world, middleVector.getBlockX(), middleVector.getBlockY(), middleVector.getBlockZ());
    
    this._topCornerPoints.add(new Location(this._world, minPoint.getBlockX(), maxPoint.getBlockY(), minPoint.getBlockZ()));
    this._topCornerPoints.add(new Location(this._world, maxPoint.getBlockX(), maxPoint.getBlockY(), minPoint.getBlockZ()));
    this._topCornerPoints.add(new Location(this._world, minPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockZ()));
    this._topCornerPoints.add(new Location(this._world, maxPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockZ()));
    
    for (Location cornerPoint : this._topCornerPoints)
    {
      MapUtil.QuickChangeBlockAt(this._world, cornerPoint.getBlockX(), cornerPoint.getBlockY(), cornerPoint.getBlockZ(), Material.WOOL, 0);
    }
    
    int minX = minPoint.getBlockX() + 1;
    int minY = minPoint.getBlockY();
    int minZ = minPoint.getBlockZ() + 1;
    
    int maxX = maxPoint.getBlockX() - 1;
    int maxZ = maxPoint.getBlockZ() - 1;
    
    for (int x = minX; x <= maxX; x++)
    {
      if (Math.abs(x - middleVector.getBlockX()) < 3)
      {

        for (int z = minZ; z <= maxZ; z++)
        {
          if (Math.abs(z - middleVector.getBlockZ()) < 3)
          {

            if ((this._middlePoint.getBlockX() == x) && (this._middlePoint.getBlockZ() == z))
            {
              MapUtil.QuickChangeBlockAt(this._world, x, minY, z, Material.BEACON, 0);
            }
            else
            {
              this._neutralLandVisualRegionBlocks.add(new Vector(x, minY, z));
              MapUtil.QuickChangeBlockAt(this._world, x, minY, z, Material.WOOL, 0);
            } } }
      }
    }
    for (int x = this._middlePoint.getBlockX() - 1; x <= this._middlePoint.getBlockX() + 1; x++)
    {
      for (int z = this._middlePoint.getBlockZ() - 1; z <= this._middlePoint.getBlockZ() + 1; z++)
      {
        MapUtil.QuickChangeBlockAt(this._world, x, minY - 1, z, Material.IRON_BLOCK, 0);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (this._game.IsPlayerInGame(player))
    {
      IDominatePlayer gamePlayer = (IDominatePlayer)this._game.GetPlayer(player);
      
      if ((this._game.HasStarted()) && (!gamePlayer.IsSpectating()))
      {
        if ((!IsLocationInControlPoint(event.getFrom())) && (IsLocationInControlPoint(event.getTo())))
        {
          if (this._capturers.contains(gamePlayer))
          {
            System.out.println("Move tried to add existing player " + player.getName());
          }
          else
          {
            AddCapturer(gamePlayer);
          }
        }
        else if ((IsLocationInControlPoint(event.getFrom())) && (!IsLocationInControlPoint(event.getTo())))
        {
          RemoveCapturer(gamePlayer);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (this._game.IsPlayerInGame(player))
    {
      IDominatePlayer gamePlayer = (IDominatePlayer)this._game.GetPlayer(player);
      
      if ((this._game.HasStarted()) && (!gamePlayer.IsSpectating()))
      {
        if ((!IsLocationInControlPoint(event.getFrom())) && (IsLocationInControlPoint(event.getTo())))
        {
          if (this._capturers.contains(gamePlayer))
          {
            System.out.println("Teleport tried to add existing player " + player.getName());
          }
          else
          {
            AddCapturer(gamePlayer);
          }
        }
        else if ((IsLocationInControlPoint(event.getFrom())) && (!IsLocationInControlPoint(event.getTo())))
        {
          RemoveCapturer(gamePlayer);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onGamePlayerDeath(GamePlayerDeathEvent<IDominateGame, IDominatePlayer> event)
  {
    if (event.GetGame() == this._game)
    {
      if (this._game.HasStarted())
      {
        if (IsLocationInControlPoint(((IDominatePlayer)event.GetPlayer()).getLocation()))
        {
          RemoveCapturer((IDominatePlayer)event.GetPlayer());
        }
      }
    }
  }
  
  @EventHandler
  public void onPlayerJoin(GamePlayerJoinedEvent<IDominateGame, IDominatePlayer> event)
  {
    if (event.GetGame() == this._game)
    {
      if ((this._game.HasStarted()) && (!((IDominatePlayer)event.GetPlayer()).IsDead()) && (!((IDominatePlayer)event.GetPlayer()).IsSpectating()))
      {
        if (IsLocationInControlPoint(((IDominatePlayer)event.GetPlayer()).getLocation()))
        {
          if (this._capturers.contains(event.GetPlayer()))
          {
            System.out.println("Join tried to add existing player " + ((IDominatePlayer)event.GetPlayer()).getName());
          }
          else
          {
            AddCapturer((IDominatePlayer)event.GetPlayer());
          }
        }
      }
    }
  }
  
  @EventHandler
  public void onPlayerLeave(GamePlayerQuitEvent<IDominateGame, IDominatePlayer> event)
  {
    if (event.GetGame() == this._game)
    {
      if (this._game.HasStarted())
      {
        if (IsLocationInControlPoint(((IDominatePlayer)event.GetPlayer()).getLocation()))
        {
          RemoveCapturer((IDominatePlayer)event.GetPlayer());
        }
      }
    }
  }
  
  protected boolean IsLocationInControlPoint(Location location)
  {
    return this._captureRegion.Contains(location.toVector()).booleanValue();
  }
  
  public boolean IsPlayerInControlPoint(IDominatePlayer player)
  {
    return IsLocationInControlPoint(player.getLocation());
  }
  
  public void AddCapturer(IDominatePlayer player)
  {
    this._capturers.add(player);
    
    if (this._ownerTeam == null)
    {
      if (this._capturingTeam == null)
      {
        this._capturingTeam = ((IDominateTeam)player.GetTeam());
        this._captureValue = 0;
        this._captureTeamChanged = true;
      }
      
      if (this._capturingTeam == player.GetTeam())
      {
        this._incrementValue += 1;
        this._notifier.BroadcastMessageToPlayer("You are taking control of " + this.Message + "!", player.GetPlayer());
      }
      else
      {
        this.Locked = true;
      }
    }
    else if (this._ownerTeam != player.GetTeam())
    {
      this._capturingTeam = ((IDominateTeam)player.GetTeam());
      this._incrementValue += 1;
      
      if (this._pointValue == this._defaultPointValue)
      {
        this._notifier.BroadcastMessageToPlayer("You are taking control of " + this.Message + "!", player.GetPlayer());
        this._plugin.getServer().getPluginManager().callEvent(new nautilus.game.dominate.events.ControlPointEnemyCapturingEvent(this._game, this, this._ownerTeam, (IDominateTeam)player.GetTeam()));
      }
      else
      {
        this.Locked = true;
      }
    }
    else
    {
      if (this._pointValue <= 5)
      {
        this._pointValue += 1;
        this._notifier.BroadcastMessageToPlayer("You are defending " + this.Message + ", " + ChatColor.YELLOW + "+1" + ChatColor.GRAY + " to capture score!", player.GetPlayer());
      }
      
      if (this._capturingTeam != null)
      {
        this.Locked = true;
      }
    }
  }
  
  public void UpdateLogic()
  {
    if (!this.Locked)
    {
      if (this._ownerTeam == null)
      {
        if (this._capturingTeam != null)
        {
          if ((this._captureValue <= this._captureThreshold) && (this._incrementValue > 0))
          {
            this._captureValue += this._incrementValue;
          }
          
          if (this._captureValue >= this._captureThreshold)
          {
            this._captured = Boolean.valueOf(true);
            this._captureValue = this._captureThreshold;
            this._ownerTeam = this._capturingTeam;
            this._ownerTeam.AddControlPoint(this);
            this._captureVisualChange = true;
            
            this._capturingTeam = null;
            this._incrementValue = 0;
            
            this._plugin.getServer().getPluginManager().callEvent(new nautilus.game.dominate.events.ControlPointCapturedEvent(this._game, this, this._ownerTeam, this._capturers));
            
            for (IDominatePlayer player : this._capturers)
            {
              if (this._pointValue <= 5)
              {
                this._pointValue += 1;
                this._notifier.BroadcastMessageToPlayer("You are defending " + this.Message + ", " + ChatColor.YELLOW + "+1" + ChatColor.GRAY + " to capture score!", player.GetPlayer());
              }
            }
          }
          
          UpdateVisual();
        }
      }
      else if (this._capturingTeam != null)
      {
        if (this._captureValue > 0)
        {
          this._captureValue -= this._incrementValue;
        }
        
        if (this._captureValue <= 0)
        {
          ControlPointLost();
        }
        
        UpdateVisual();
      }
    }
  }
  
  public void RemoveCapturer(IDominatePlayer player)
  {
    this._capturers.remove(player);
    
    if (this._capturers.size() == 0)
    {
      this._capturingTeam = null;
      this._captureTeamChanged = true;
      this._incrementValue = 0;
      this._pointValue = this._defaultPointValue;
      
      if (this._ownerTeam == null)
      {
        this._captureValue = 0;
      }
      
      if (player.GetTeam() == this._ownerTeam)
      {
        this._notifier.BroadcastMessageToPlayer("You are no longer defending " + this.Message + "!", player.GetPlayer());
      }
      
      UpdateVisual();


    }
    else if (!this.Locked)
    {
      if (player.GetTeam() == this._ownerTeam)
      {
        this._notifier.BroadcastMessageToPlayer("You are no longer defending " + this.Message + "!", player.GetPlayer());
        
        this._pointValue = (this._defaultPointValue + this._capturers.size());
        
        if (this._pointValue > 5)
        {
          this._pointValue = 5;
        }
      }
      else if (player.GetTeam() == this._capturingTeam)
      {
        this._incrementValue = this._capturers.size();
      }
    }
    else
    {
      int capturingTeam = 0;
      int teamB = 0;
      
      for (IDominatePlayer gamePlayer : this._capturers)
      {
        if (gamePlayer.GetTeam() == this._capturingTeam)
        {
          capturingTeam++;
        }
        else
        {
          teamB++;
        }
      }
      
      if ((capturingTeam == 0) || (teamB == 0))
      {
        this.Locked = false;
      }
      
      this._incrementValue = capturingTeam;
      
      if (capturingTeam == 0)
      {
        this._capturingTeam = null;
      }
      
      if (this._ownerTeam != null)
      {
        this._pointValue = (this._defaultPointValue + teamB);
        
        if (this._pointValue > 5)
        {
          this._pointValue = 5;
        }
        

      }
      else if (capturingTeam == 0)
      {
        this._capturingTeam = ((IDominateTeam)((IDominatePlayer)this._capturers.get(0)).GetTeam());
        this._captureTeamChanged = true;
        this._incrementValue = this._capturers.size();
        this._captureValue = 0;
        
        this._notifier.BroadcastMessageToPlayers("You are taking control of " + this.Message + "!", this._capturers);
      }
    }
  }
  


  private void ControlPointLost()
  {
    if (this._ownerTeam != null)
    {
      this._plugin.getServer().getPluginManager().callEvent(new ControlPointLostEvent(this._game, this, this._ownerTeam, this._capturers));
      this._ownerTeam.RemoveControlPoint(this);
    }
    
    this._captureValue = 0;
    this._captured = Boolean.valueOf(false);
    this._ownerTeam = null;
    this._captureVisualChange = true;
  }
  
  public void UpdateVisual()
  {
    try
    {
      this._alternateColor = (!this._alternateColor);
      
      byte data = 0;
      
      if (this._ownerTeam != null)
      {
        if (this._ownerTeam.GetTeamType() == TeamType.RED)
        {
          data = 14;
        }
        else if (this._ownerTeam.GetTeamType() == TeamType.BLUE)
        {
          data = 11;
        }
      }
      else if (this._capturingTeam != null)
      {
        if (this._capturingTeam.GetTeamType() == TeamType.RED)
        {
          data = 14;
        }
        else if (this._capturingTeam.GetTeamType() == TeamType.BLUE)
        {
          data = 11;
        }
      }
      Location cornerPoint;
      if (this._captureVisualChange)
      {
        byte topPointColor = data;
        
        if (this._ownerTeam == null)
        {
          topPointColor = 0;
        }
        
        for (Iterator localIterator = this._topCornerPoints.iterator(); localIterator.hasNext();) { cornerPoint = (Location)localIterator.next();
          
          MapUtil.QuickChangeBlockAt(this._world, cornerPoint.getBlockX(), cornerPoint.getBlockY(), cornerPoint.getBlockZ(), Material.WOOL, topPointColor);
        }
        
        this._captureVisualChange = false;
      }
      
      if (this._captureTeamChanged)
      {
        for (Vector blockVector : this._neutralLandVisualRegionBlocks)
        {
          if ((this._middlePoint.getBlockX() != blockVector.getBlockX()) || (this._middlePoint.getBlockZ() != blockVector.getBlockZ()))
          {

            MapUtil.QuickChangeBlockAt(this._world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, 0);
          }
        }
        for (Vector blockVector : this._ownedLandVisualRegionBlocks)
        {
          if ((this._middlePoint.getBlockX() != blockVector.getBlockX()) || (this._middlePoint.getBlockZ() != blockVector.getBlockZ()))
          {

            MapUtil.QuickChangeBlockAt(this._world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, data);
          }
        }
        this._captureTeamChanged = false;
      }
      
      float capturePercentage = this._captureValue / this._captureThreshold;
      
      byte captureProgressColorData = data;
      int neutralLandBlockCount = this._neutralLandVisualRegionBlocks.size();
      int ownedLandBlockCount = this._ownedLandVisualRegionBlocks.size();
      
      int captureBlocksToColorCount = (int)(capturePercentage * (neutralLandBlockCount + ownedLandBlockCount));
      
      if (captureBlocksToColorCount > ownedLandBlockCount)
      {
        captureBlocksToColorCount -= ownedLandBlockCount;
        Vector blockVector;
        for (int i = 0; i < captureBlocksToColorCount; i++)
        {
          blockVector = (Vector)this._neutralLandVisualRegionBlocks.get(this._random.nextInt(this._neutralLandVisualRegionBlocks.size()));
          this._ownedLandVisualRegionBlocks.add(blockVector);
          this._neutralLandVisualRegionBlocks.remove(blockVector);
          
          if ((this._middlePoint.getBlockX() != blockVector.getBlockX()) || (this._middlePoint.getBlockZ() != blockVector.getBlockZ()))
          {

            MapUtil.QuickChangeBlockAt(this._world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, captureProgressColorData);
          }
        }
        for (Location cornerPoint : this._topCornerPoints)
        {
          this._world.playEffect(cornerPoint, Effect.STEP_SOUND, captureProgressColorData == 14 ? 55 : 8);
        }
      }
      else if (captureBlocksToColorCount < this._ownedLandVisualRegionBlocks.size())
      {
        captureBlocksToColorCount = this._ownedLandVisualRegionBlocks.size() - captureBlocksToColorCount;
        captureProgressColorData = 0;
        Vector blockVector;
        for (int i = 0; i < captureBlocksToColorCount; i++)
        {
          blockVector = (Vector)this._ownedLandVisualRegionBlocks.get(this._random.nextInt(this._ownedLandVisualRegionBlocks.size()));
          this._neutralLandVisualRegionBlocks.add(blockVector);
          this._ownedLandVisualRegionBlocks.remove(blockVector);
          
          if ((this._middlePoint.getBlockX() != blockVector.getBlockX()) || (this._middlePoint.getBlockZ() != blockVector.getBlockZ()))
          {

            MapUtil.QuickChangeBlockAt(this._world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, captureProgressColorData);
          }
        }
        for (Location cornerPoint : this._topCornerPoints)
        {
          this._world.playEffect(cornerPoint, Effect.STEP_SOUND, 35);
        }
      }
    }
    catch (Exception ex)
    {
      System.out.println("Exception in ControlPoint.UpdateVisual(): " + ex.getMessage());
      System.out.println("Increment value = " + this._incrementValue);
      System.out.println("Capturing team = " + (this._capturingTeam != null ? this._capturingTeam.GetTeamType() : "None"));
      System.out.println("Owner team = " + (this._ownerTeam != null ? this._ownerTeam.GetTeamType() : "None"));
      System.out.println("Capturers size = " + this._capturers.size());
      System.out.println("capturePercentage = " + this._captureValue / this._captureThreshold);
      System.out.println("neutralLandBlockCount = " + this._neutralLandVisualRegionBlocks.size());
      System.out.println("ownedLandBlockCount = " + this._ownedLandVisualRegionBlocks.size());
      System.out.println("captureBlocksToColorCount = " + (int)(this._captureValue / this._captureThreshold * (this._neutralLandVisualRegionBlocks.size() + this._ownedLandVisualRegionBlocks.size())));
    }
  }
  

  @EventHandler(priority=EventPriority.LOWEST)
  public void OnPlayerChat(AsyncPlayerChatEvent event)
  {
    Player player = event.getPlayer();
    
    if ((player.isOp()) && (event.getMessage().startsWith("!test cpinfo")))
    {
      player.sendMessage("Control Point Name: " + this.Message);
      player.sendMessage("Increment value = " + this._incrementValue);
      player.sendMessage("Capturing team = " + (this._capturingTeam != null ? this._capturingTeam.GetTeamType() : "None"));
      player.sendMessage("Owner team = " + (this._ownerTeam != null ? this._ownerTeam.GetTeamType() : "None"));
      player.sendMessage("Capturers size = " + this._capturers.size());
      player.sendMessage("capturePercentage = " + this._captureValue / this._captureThreshold);
      player.sendMessage("neutralLandBlockCount = " + this._neutralLandVisualRegionBlocks.size());
      player.sendMessage("ownedLandBlockCount = " + this._ownedLandVisualRegionBlocks.size());
      player.sendMessage("captureBlocksToColorCount = " + (int)(this._captureValue / this._captureThreshold * (this._neutralLandVisualRegionBlocks.size() + this._ownedLandVisualRegionBlocks.size())));
      
      player.sendMessage("Capturer list:");
      for (IDominatePlayer capturer : this._capturers)
      {
        player.sendMessage(capturer.getName());
      }
    }
  }
  










































  public String GetName()
  {
    return this.Message;
  }
  

  public boolean Captured()
  {
    return this._captured.booleanValue();
  }
  

  public IDominateTeam GetOwnerTeam()
  {
    return this._ownerTeam;
  }
  

  public void Deactivate()
  {
    this._plugin = null;
    this._game = null;
    this._notifier = null;
    this._world = null;
    this._captureRegion = null;
    this._capturingTeam = null;
    this._capturers = null;
    this._captured = null;
    this._ownerTeam = null;
    this._ownedLandVisualRegionBlocks = null;
    this._neutralLandVisualRegionBlocks = null;
    this._random = null;
    this._middlePoint = null;
    this._topCornerPoints = null;
    
    HandlerList.unregisterAll(this);
  }
  

  public int GetPoints()
  {
    return this._pointValue;
  }
  

  public Location GetMiddlePoint()
  {
    return this._middlePoint;
  }
  

  public List<IDominatePlayer> GetCapturers()
  {
    return this._capturers;
  }
}
