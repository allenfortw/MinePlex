package nautilus.game.dominate.engine;

import nautilus.game.dominate.player.IDominatePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PointPowerUp extends PowerUp
{
  private int _pointReward;
  
  public PointPowerUp(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, Location location, long interval, int pointReward)
  {
    super(plugin, game, notifier, location, interval);
    
    this._pointReward = pointReward;
    this.BlockType = Material.EMERALD_BLOCK;
  }
  

  protected Item SpawnItem(Location location)
  {
    Item item = location.getWorld().dropItem(location, new ItemStack(Material.EMERALD, 1));
    item.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
    item.teleport(location);
    return item;
  }
  

  protected void RewardPlayer(Player player)
  {
    ((IDominateTeam)((IDominatePlayer)this.Game.GetPlayer(player)).GetTeam()).AddPoints((int)(this._pointReward * 2.5D));
    ((IDominatePlayer)this.Game.GetPlayer(player)).AddPoints(this._pointReward);
    this.Notifier.BroadcastMessageToPlayer("You added +" + ChatColor.YELLOW + this._pointReward + ChatColor.GRAY + " to your score and +" + ChatColor.YELLOW + (int)(this._pointReward * 2.5D) + ChatColor.GRAY + " to your team score!", player);
    player.playSound(player.getLocation(), org.bukkit.Sound.ORB_PICKUP, 0.4F, 0.9F);
  }
}
