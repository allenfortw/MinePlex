package nautilus.game.dominate.engine;

import java.util.Map.Entry;
import mineplex.core.common.util.NautHashMap;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ResupplyPowerUp extends PowerUp
{
  private ClassManager _classManager;
  
  public ResupplyPowerUp(JavaPlugin plugin, IDominateGame game, ClassManager classManager, DominateNotifier notifier, Location location, long interval)
  {
    super(plugin, game, notifier, location, interval);
    this._classManager = classManager;
    
    this.BlockType = Material.GOLD_BLOCK;
  }
  

  protected Item SpawnItem(Location location)
  {
    Item item = location.getWorld().dropItem(location, new ItemStack(Material.CHEST, 1));
    item.setVelocity(new Vector(0, 0, 0));
    item.teleport(location);
    return item;
  }
  

  protected void RewardPlayer(Player player)
  {
    ClientClass client = (ClientClass)this._classManager.Get(player);
    
    player.getInventory().clear();
    
    for (Map.Entry<Integer, ItemStack> item : client.GetDefaultItems().entrySet())
    {
      if (item.getValue() != null) {
        player.getInventory().setItem(((Integer)item.getKey()).intValue(), ((ItemStack)item.getValue()).clone());
      }
    }
    this.Notifier.BroadcastMessageToPlayer("You received supplies!", player);
    player.playSound(player.getLocation(), org.bukkit.Sound.MAGMACUBE_WALK, 0.4F, 0.9F);
  }
}
