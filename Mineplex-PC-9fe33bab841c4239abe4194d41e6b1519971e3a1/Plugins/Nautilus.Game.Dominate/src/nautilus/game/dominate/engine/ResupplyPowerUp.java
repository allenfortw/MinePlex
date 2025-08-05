package nautilus.game.dominate.engine;

import java.util.Map.Entry;

import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ResupplyPowerUp extends PowerUp
{
	private ClassManager _classManager;
	
    public ResupplyPowerUp(JavaPlugin plugin, IDominateGame game, ClassManager classManager, DominateNotifier notifier, Location location, long interval)
    {
        super(plugin, game, notifier, location, interval);
        _classManager = classManager;
        
        BlockType = Material.GOLD_BLOCK;
    }

    @Override
    protected Item SpawnItem(Location location)
    {
        Item item = location.getWorld().dropItem(location, new ItemStack(Material.CHEST, 1));
        item.setVelocity(new Vector(0,0,0));
        item.teleport(location);
        return item;
    }

    @Override
    protected void RewardPlayer(Player player)
    {
        ClientClass client = _classManager.Get(player);
        
        player.getInventory().clear();
        
        for (Entry<Integer, ItemStack> item : client.GetDefaultItems().entrySet())
        {
        	if (item.getValue() != null)
        		player.getInventory().setItem(item.getKey(), item.getValue().clone());
        }
        
        Notifier.BroadcastMessageToPlayer("You received supplies!", player);
        player.playSound(player.getLocation(), Sound.MAGMACUBE_WALK, .4F, 0.9F);
    }
}
