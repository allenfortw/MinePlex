package nautilus.game.dominate.engine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PointPowerUp extends PowerUp
{
    private int _pointReward;
    
    public PointPowerUp(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, Location location, long interval, int pointReward)
    {
        super(plugin, game, notifier, location, interval);
        
        _pointReward = pointReward;
        BlockType = Material.EMERALD_BLOCK;
    }

    @Override
    protected Item SpawnItem(Location location)
    {
        Item item = location.getWorld().dropItem(location, new ItemStack(Material.EMERALD, 1));
        item.setVelocity(new Vector(0,0,0));
        item.teleport(location);
        return item;
    }

    @Override
    protected void RewardPlayer(Player player)
    {
        Game.GetPlayer(player).GetTeam().AddPoints((int)(_pointReward * 2.5));
        Game.GetPlayer(player).AddPoints(_pointReward);
        Notifier.BroadcastMessageToPlayer("You added +" + ChatColor.YELLOW + _pointReward + ChatColor.GRAY + " to your score and +"+ ChatColor.YELLOW + (int)(_pointReward * 2.5) + ChatColor.GRAY + " to your team score!", player);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, .4F, 0.9F);
    }
}
