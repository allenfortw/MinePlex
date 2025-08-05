package nautilus.game.core.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.player.ITeamGamePlayer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class TeamGame<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>, ArenaType extends ITeamArena> extends Game<PlayerType, ArenaType> implements ITeamGame<ArenaType, PlayerType, PlayerTeamType>
{
  private Random _random;
  protected PlayerTeamType RedTeam;
  protected PlayerTeamType BlueTeam;
  
  public TeamGame(JavaPlugin plugin, ClassManager classManager, ConditionManager conditionManager, mineplex.core.energy.Energy energy)
  {
    super(plugin, classManager, conditionManager, energy);
    
    this._random = new Random();
    
    this.RedTeam = CreateTeam(TeamType.RED);
    this.BlueTeam = CreateTeam(TeamType.BLUE);
  }
  

  protected abstract PlayerTeamType CreateTeam(TeamType paramTeamType);
  
  public void Activate(ArenaType arena)
  {
    super.Activate(arena);
    
    this.RedTeam.SetSpawnRoom(arena.GetRedSpawnRoom());
    this.BlueTeam.SetSpawnRoom(arena.GetBlueSpawnRoom());
    
    for (PlayerType player : this.Players.values())
    {
      ActivatePlayer(player);
    }
  }
  
  public void ActivatePlayer(PlayerType player)
  {
    if (player.GetTeam() == this.BlueTeam)
    {
      Location spawnPoint = GetRandomSpawnPoint(((ITeamArena)this.Arena).GetBlueSpawnPoints());
      player.SetLastInArenaPosition(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
      player.teleport(spawnPoint);
    }
    else if (player.GetTeam() == this.RedTeam)
    {
      Location spawnPoint = GetRandomSpawnPoint(((ITeamArena)this.Arena).GetRedSpawnPoints());
      player.SetLastInArenaPosition(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
      player.teleport(spawnPoint);
    }
    
    if (this.HasStarted)
    {
      ResetPlayer(player);
      RespawnPlayer(player);
      
      player.SetSpectating(false);
      player.SetDead(false);
    }
    else
    {
      player.SetSpectating(true);
      player.SetDead(true);
    }
  }
  

  public void ClearPlayerSettings(PlayerType player)
  {
    super.ClearPlayerSettings(player);
    
    player.GetTeam().RemovePlayer(player);
  }
  

  public void ReallyStartGame()
  {
    super.ReallyStartGame();
    
    for (PlayerType player : this.Players.values())
    {
      player.StartTimePlay();
      
      ResetPlayer(player);
      RespawnPlayer(player);
      
      player.SetSpectating(false);
      player.SetDead(false);
    }
  }
  

  public boolean CanInteract(PlayerType player, Block block)
  {
    if (super.CanInteract(player, block))
      return true;
    if ((player.GetTeam().IsInSpawnRoom(block.getLocation())) && (player.GetTeam().IsInSpawnRoom(player.getLocation()))) {
      return true;
    }
    return false;
  }
  

  public void Deactivate()
  {
    this.RedTeam.ClearPlayers();
    this.BlueTeam.ClearPlayers();
    
    this.RedTeam = null;
    this.BlueTeam = null;
    this._random = null;
    
    super.Deactivate();
  }
  

  public PlayerTeamType GetBlueTeam()
  {
    return this.BlueTeam;
  }
  

  public PlayerTeamType GetRedTeam()
  {
    return this.RedTeam;
  }
  
  protected Location GetRandomSpawnPoint(List<Location> spawnPoints)
  {
    Location randomSpawnPoint = (Location)spawnPoints.get(this._random.nextInt(spawnPoints.size()));
    return randomSpawnPoint;
  }
  
  protected void SpawnPlayer(PlayerType player)
  {
    player.GetPlayer().eject();
    player.GetPlayer().leaveVehicle();
    
    if (player.GetTeam() == this.RedTeam)
    {
      player.teleport(GetRandomSpawnPoint(((ITeamArena)this.Arena).GetRedSpawnPoints()));
    }
    else if (player.GetTeam() == GetBlueTeam())
    {
      player.teleport(GetRandomSpawnPoint(((ITeamArena)this.Arena).GetBlueSpawnPoints()));
    }
    
    player.playSound(player.getLocation(), org.bukkit.Sound.ENDERMAN_TELEPORT, 0.5F, 0.0F);
  }
  
  public void RespawnPlayer(PlayerType player)
  {
    if (!player.isOnline()) {
      return;
    }
    SpawnPlayer(player);
    
    this.ClassManager.Get(player.getName()).ResetToDefaults(true, true);
  }
  
  protected void StopGame()
  {
    for (PlayerType player : this.Players.values())
    {
      player.StopTimePlay();
      
      if (player.isOnline())
      {

        player.GetPlayer().eject();
        
        if (player.GetPlayer().isInsideVehicle()) {
          player.GetPlayer().leaveVehicle();
        }
        if (player.GetTeam() == this.BlueTeam)
        {
          player.teleport(GetRandomSpawnPoint(((ITeamArena)this.Arena).GetBlueSpawnPoints()));
        }
        else if (player.GetTeam() == this.RedTeam)
        {
          player.teleport(GetRandomSpawnPoint(((ITeamArena)this.Arena).GetRedSpawnPoints()));
        }
        
        ResetPlayer(player);
        
        if (player.isOnline())
        {
          ClientClass playerClass = this.ClassManager.Get(player.getName());
          playerClass.SetGameClass(null);
          playerClass.ClearDefaults();
        }
        
        player.SetDead(true);
        player.SetSpectating(true);
        
        if (this.PlayerTaskIdMap.containsKey(player.getName()))
        {
          this.Plugin.getServer().getScheduler().cancelTask(((Integer)this.PlayerTaskIdMap.get(player.getName())).intValue());
          this.ConditionManager.EndCondition(player.GetPlayer(), mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, null);
          
          this.PlayerTaskIdMap.remove(player.getName());
        }
        
        player.playSound(player.getLocation(), org.bukkit.Sound.WITHER_SPAWN, 1.0F, 0.9F);
      }
    }
    this.Plugin.getServer().getScheduler().cancelTask(this.UpdaterTaskId);
  }
  
  protected void UpdateNewPlayerWithOldPlayer(PlayerType newPlayer, PlayerType oldPlayer)
  {
    super.UpdateNewPlayerWithOldPlayer(newPlayer, oldPlayer);
    
    PlayerTeamType playerTeam = oldPlayer.GetTeam();
    
    playerTeam.RemovePlayer(oldPlayer);
    playerTeam.AddPlayer(newPlayer);
  }
}
