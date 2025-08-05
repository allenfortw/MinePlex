package nautilus.game.dominate.engine;

import mineplex.core.common.util.MapUtil;
import net.minecraft.server.v1_6_R2.EntityItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PowerUp implements Listener, IPowerUp
{    
    private Location _location;
    private Location _blockLocation;    
    private long _lastTimePickedUp = -1;
    private long _interval;

    protected JavaPlugin Plugin;
    protected DominateNotifier Notifier;
    protected IDominateGame Game;
    protected Item Item;
    protected Material BlockType;
    
    public PowerUp(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, Location location, long interval)
    {
        Plugin = plugin;
        Game = game;
        Notifier = notifier;
        
        _location = location;
        _blockLocation = _location.getBlock().getRelative(BlockFace.DOWN).getLocation();
        BlockType = Material.GOLD_BLOCK;
        _interval = interval;
        
        Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
    }
    
    @EventHandler
    public void OnPlayerPickUp(PlayerPickupItemEvent event)
    {
        if (event.getItem().equals(Item))
        {
            if (Game.IsPlayerInGame(event.getPlayer()) && !((CraftPlayer)event.getPlayer()).getHandle().spectating)
            {
                RewardPlayer(event.getPlayer());
                _lastTimePickedUp = System.currentTimeMillis();
                Item.remove();
                
                MapUtil.QuickChangeBlockAt(_blockLocation.getWorld(), _blockLocation.getBlockX(), _blockLocation.getBlockY(), _blockLocation.getBlockZ(), Material.IRON_BLOCK, 0);
            }
            
            event.setCancelled(true);
        }
    }
    
    public void Update()
    {
        if (_lastTimePickedUp == -1 || System.currentTimeMillis() >= (_lastTimePickedUp + _interval))
        {
            Item = SpawnItem(_location);
            MapUtil.QuickChangeBlockAt(_blockLocation.getWorld(), _blockLocation.getBlockX(), _blockLocation.getBlockY(), _blockLocation.getBlockZ(), BlockType, 0);
            _lastTimePickedUp = System.currentTimeMillis() + (15 * 60 * 1000);
        }
        
        ((EntityItem)((CraftEntity)Item).getHandle()).age = 0;
    }
    
    public void Deactivate()
    {
        _location = null;
        _blockLocation = null;  
     
        Plugin = null;
        Notifier = null;
        Game = null;
        Item = null;
        BlockType = null;
        
        HandlerList.unregisterAll(this);
    }
    
    protected abstract Item SpawnItem(Location location);
    
    protected abstract void RewardPlayer(Player player);
}
