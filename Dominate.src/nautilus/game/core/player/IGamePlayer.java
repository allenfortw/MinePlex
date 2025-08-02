package nautilus.game.core.player;

import mineplex.minecraft.game.classcombat.Class.ClientClass;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public abstract interface IGamePlayer
{
  public abstract int GetRemainingLives();
  
  public abstract void SetLives(int paramInt);
  
  public abstract int GetKills();
  
  public abstract void AddKills(int paramInt);
  
  public abstract int GetAssists();
  
  public abstract void AddAssists(int paramInt);
  
  public abstract int GetDeaths();
  
  public abstract void AddDeaths(int paramInt);
  
  public abstract void SetLogoutTime(long paramLong);
  
  public abstract long GetLogoutTime();
  
  public abstract boolean IsDead();
  
  public abstract void SetDead(boolean paramBoolean);
  
  public abstract boolean IsSpectating();
  
  public abstract void SetSpectating(boolean paramBoolean);
  
  public abstract Player GetPlayer();
  
  public abstract void RemoveArrows();
  
  public abstract void StartTimePlay();
  
  public abstract void StopTimePlay();
  
  public abstract long GetTimePlayed();
  
  public abstract String getName();
  
  public abstract void sendMessage(String paramString);
  
  public abstract Location getLocation();
  
  public abstract boolean teleport(Location paramLocation);
  
  public abstract boolean isOnline();
  
  public abstract void setFireTicks(int paramInt);
  
  public abstract void setHealth(double paramDouble);
  
  public abstract void setFoodLevel(int paramInt);
  
  public abstract boolean IsOut();
  
  public abstract void setVelocity(Vector paramVector);
  
  public abstract void SetLastInArenaPosition(World paramWorld, double paramDouble1, double paramDouble2, double paramDouble3);
  
  public abstract Location GetLastInArenaPosition();
  
  public abstract PlayerInventory getInventory();
  
  public abstract void playSound(Location paramLocation, Sound paramSound, float paramFloat1, float paramFloat2);
  
  public abstract int GetPoints();
  
  public abstract void AddPoints(int paramInt);
  
  public abstract void SetSkillTokens(int paramInt);
  
  public abstract void SetItemTokens(int paramInt);
  
  public abstract int GetSkillTokens();
  
  public abstract int GetItemTokens();
  
  public abstract ClientClass GetClass();
  
  public abstract void SetClass(ClientClass paramClientClass);
}
