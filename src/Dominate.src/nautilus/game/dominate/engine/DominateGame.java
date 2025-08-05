package nautilus.game.dominate.engine;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.energy.Energy;
import mineplex.core.packethandler.PacketHandler;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.TeamGame;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.DominatePlayer;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.scoreboard.DominateTabScoreboard;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DominateGame extends TeamGame<IDominatePlayer, IDominateTeam, IDominateArena> implements IDominateGame
{
  private DominateNotifier _notifier;
  private DominateTabScoreboard _scoreboard;
  private List<IControlPoint> _controlPoints;
  private List<IPowerUp> _powerUps;
  private int _lastUpdate;
  
  public DominateGame(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, ConditionManager conditionmanager, Energy energy, DominateNotifier notifier, PacketHandler packetHandler)
  {
    super(plugin, classManager, conditionmanager, energy);
    
    this._notifier = notifier;
    this.ScoreLimit = 15000;
    this._controlPoints = new ArrayList();
    this._powerUps = new ArrayList();
    this._scoreboard = new DominateTabScoreboard(plugin, clientManager, classManager, packetHandler, this);
  }
  

  public IDominatePlayer AddSpectatorToGame(Player player, Location to)
  {
    IDominatePlayer spectator = (IDominatePlayer)super.AddSpectatorToGame(player, to);
    
    this._scoreboard.AddSpectator(spectator);
    
    return spectator;
  }
  

  public void ClearPlayerSettings(IDominatePlayer player)
  {
    super.ClearPlayerSettings(player);
    
    if (this._scoreboard != null) {
      this._scoreboard.ClearScoreboardForSpectator(player);
    }
  }
  
  public void RemoveSpectator(IDominatePlayer player)
  {
    super.RemoveSpectator(player);
    
    this._scoreboard.ClearScoreboardForSpectator(player);
  }
  

  public void Activate(IDominateArena arena)
  {
    super.Activate(arena);
    
    for (Region controlPointRegion : arena.GetControlPointAreas())
    {
      IControlPoint controlPoint = new ControlPoint(this.Plugin, this, this._notifier, ((IDominateArena)this.Arena).GetWorld(), controlPointRegion, controlPointRegion.GetName());
      this._controlPoints.add(controlPoint);
    }
    
    for (Vector pointPowerUpPoint : arena.GetPointPowerUpPoints())
    {
      IPowerUp pointPowerUp = new PointPowerUp(this.Plugin, this, this._notifier, pointPowerUpPoint.toLocation(((IDominateArena)this.Arena).GetWorld()), 180000L, 100);
      this._powerUps.add(pointPowerUp);
    }
    
    for (Vector resupplyPowerUpPoint : arena.GetResupplyPowerUpPoints())
    {
      IPowerUp resupplyPowerUp = new ResupplyPowerUp(this.Plugin, this, this.ClassManager, this._notifier, resupplyPowerUpPoint.toLocation(((IDominateArena)this.Arena).GetWorld()), 60000L);
      this._powerUps.add(resupplyPowerUp);
    }
    
    this._scoreboard.Update();
  }
  
  public void Update()
  {
    for (IControlPoint controlPoint : this._controlPoints)
    {
      controlPoint.UpdateLogic();
      
      if (controlPoint.Captured())
      {
        controlPoint.GetOwnerTeam().AddPoints(controlPoint.GetPoints());
        
        for (IDominatePlayer defender : controlPoint.GetCapturers())
        {
          if (defender.GetTeam() == controlPoint.GetOwnerTeam())
          {
            defender.AddPoints(1);
          }
        }
        
        ((IDominateArena)this.Arena).GetWorld().playSound(controlPoint.GetMiddlePoint(), Sound.CHICKEN_EGG_POP, 0.1F, 0.9F);
      }
    }
    
    for (IPowerUp powerUp : this._powerUps)
    {
      powerUp.Update();
    }
    
    if (((IDominateTeam)this.RedTeam).GetScore() >= this.ScoreLimit)
    {
      ((IDominateTeam)this.RedTeam).SetScore(this.ScoreLimit);
      StopGame();
      this.Plugin.getServer().getPluginManager().callEvent(new TeamGameFinishedEvent(this, (IDominateTeam)this.RedTeam));
      this._scoreboard.Update();
    }
    else if (((IDominateTeam)this.BlueTeam).GetScore() >= this.ScoreLimit)
    {
      ((IDominateTeam)this.BlueTeam).SetScore(this.ScoreLimit);
      StopGame();
      this.Plugin.getServer().getPluginManager().callEvent(new TeamGameFinishedEvent(this, (IDominateTeam)this.BlueTeam));
      this._scoreboard.Update();
    }
    
    if (this._lastUpdate % 10 == 0)
    {
      this._scoreboard.Update();
    }
    
    this._lastUpdate += 1;
  }
  

  public void Deactivate()
  {
    this._scoreboard.Stop();
    
    for (IControlPoint controlPoint : this._controlPoints)
    {
      controlPoint.Deactivate();
    }
    
    for (IPowerUp powerUp : this._powerUps)
    {
      powerUp.Deactivate();
    }
    
    this._controlPoints.clear();
    this._powerUps.clear();
    
    this._scoreboard = null;
    
    this._controlPoints = null;
    this._powerUps = null;
    
    super.Deactivate();
  }
  

  protected IDominateTeam CreateTeam(TeamType teamType)
  {
    return new DominateTeam(teamType);
  }
  

  protected IDominatePlayer CreateGamePlayer(Player player, int playerLives)
  {
    return new DominatePlayer(this.Plugin, player);
  }
  

  public List<IControlPoint> GetControlPoints()
  {
    return this._controlPoints;
  }
}
