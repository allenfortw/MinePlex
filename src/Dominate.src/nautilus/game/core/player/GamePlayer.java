package nautilus.game.core.player;

import mineplex.minecraft.game.classcombat.Class.ClientClass;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;



public class GamePlayer
  implements IGamePlayer
{
  private Player _player;
  private int _lives;
  private int _kills;
  private int _assists;
  private int _deaths;
  private int _points;
  private int _skillTokens;
  private int _itemTokens;
  private long _logoutTime;
  private Location _lastInArenaPosition;
  private boolean _isDead;
  private boolean _isSpectating;
  private long _timePlayed;
  private long _lastTimePlayStart;
  private ClientClass _clientClass;
  
  public GamePlayer(JavaPlugin plugin, Player player)
  {
    this._player = player;
  }
  

  public int GetRemainingLives()
  {
    return this._lives;
  }
  

  public void SetLives(int getRemainingLives)
  {
    this._lives = getRemainingLives;
  }
  

  public int GetKills()
  {
    return this._kills;
  }
  

  public void AddKills(int kills)
  {
    this._kills += kills;
  }
  

  public int GetAssists()
  {
    return this._assists;
  }
  

  public void AddAssists(int assists)
  {
    this._assists += assists;
  }
  

  public int GetDeaths()
  {
    return this._deaths;
  }
  

  public void AddDeaths(int deaths)
  {
    this._deaths += deaths;
  }
  

  public int GetSkillTokens()
  {
    return this._skillTokens;
  }
  

  public void SetSkillTokens(int tokens)
  {
    this._skillTokens = tokens;
  }
  

  public int GetItemTokens()
  {
    return this._itemTokens;
  }
  

  public void SetItemTokens(int tokens)
  {
    this._itemTokens = tokens;
  }
  

  public int GetPoints()
  {
    return this._points;
  }
  

  public void AddPoints(int points)
  {
    this._points += points;
  }
  

  public void SetLogoutTime(long logoutTime)
  {
    this._logoutTime = logoutTime;
  }
  

  public long GetLogoutTime()
  {
    return this._logoutTime;
  }
  

  public boolean IsOut()
  {
    return this._lives <= 0;
  }
  

  public void SetLastInArenaPosition(World world, double x, double y, double z)
  {
    this._lastInArenaPosition = new Location(world, x, y, z);
  }
  

  public Location GetLastInArenaPosition()
  {
    return this._lastInArenaPosition;
  }
  

  public boolean IsDead()
  {
    return this._isDead;
  }
  

  public void SetDead(boolean dead)
  {
    this._isDead = dead;
  }
  

  public Player GetPlayer()
  {
    return this._player;
  }
  


  public void RemoveArrows() {}
  


  public boolean IsSpectating()
  {
    return this._isSpectating;
  }
  

  public void SetSpectating(boolean spectating)
  {
    this._isSpectating = spectating;
    

    ((CraftPlayer)this._player).getHandle().spectating = spectating;
    
    if (spectating)
    {
      this._player.setAllowFlight(true);
      this._player.setFlying(true);
    }
    else
    {
      this._player.setAllowFlight(false);
      this._player.setFlying(false);
    }
  }
  

  public ClientClass GetClass()
  {
    return this._clientClass;
  }
  

  public void SetClass(ClientClass clientClass)
  {
    this._clientClass = clientClass;
  }
  

  public long GetTimePlayed()
  {
    return this._timePlayed;
  }
  

  public void StartTimePlay()
  {
    this._lastTimePlayStart = System.currentTimeMillis();
  }
  

  public void StopTimePlay()
  {
    this._timePlayed += System.currentTimeMillis() - this._lastTimePlayStart;
  }
  

  public String getName()
  {
    return this._player.getName();
  }
  

  public void sendMessage(String string)
  {
    this._player.sendMessage(string);
  }
  

  public Location getLocation()
  {
    return this._player.getLocation();
  }
  

  public boolean teleport(Location location)
  {
    return this._player.teleport(location);
  }
  

  public boolean isOnline()
  {
    return this._player.isOnline();
  }
  

  public void setFireTicks(int i)
  {
    this._player.setFireTicks(i);
  }
  

  public void setHealth(double i)
  {
    this._player.setHealth(i);
  }
  

  public void setFoodLevel(int i)
  {
    this._player.setFoodLevel(i);
  }
  

  public void setVelocity(Vector vector)
  {
    this._player.setVelocity(vector);
  }
  

  public PlayerInventory getInventory()
  {
    return this._player.getInventory();
  }
  

  public void playSound(Location location, Sound zombieMetal, float f, float g)
  {
    this._player.playSound(location, zombieMetal, f, g);
  }
}
