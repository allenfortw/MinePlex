package nautilus.game.core.player;

import mineplex.minecraft.game.classcombat.Class.ClientClass;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public interface IGamePlayer
{
    int GetRemainingLives();
    void SetLives(int getRemainingLives);
    int GetKills();
    void AddKills(int kills);
    int GetAssists();
    void AddAssists(int assists);
    int GetDeaths();
    void AddDeaths(int deaths);
    
    void SetLogoutTime(long logoutTime);
    long GetLogoutTime();
    boolean IsDead();
    void SetDead(boolean dead);
    boolean IsSpectating();
    void SetSpectating(boolean spectating);
    Player GetPlayer();
    void RemoveArrows();
    
    void StartTimePlay();
    void StopTimePlay();
    long GetTimePlayed();
    
    String getName();   
    void sendMessage(String string);
    Location getLocation();
    boolean teleport(Location location);
    boolean isOnline();

    void setFireTicks(int i);
    void setHealth(double i);
    void setFoodLevel(int i);
    boolean IsOut();
    void setVelocity(Vector vector);
    void SetLastInArenaPosition(World world, double x, double y, double z);
    Location GetLastInArenaPosition();
    PlayerInventory getInventory();
    void playSound(Location location, Sound zombieMetal, float f, float g);
    
    int GetPoints();
    void AddPoints(int points);
    void SetSkillTokens(int skillTokens);
    void SetItemTokens(int itemTokens);
    int GetSkillTokens();
    int GetItemTokens();
    
    ClientClass GetClass();
    void SetClass(ClientClass clientClass);
}
