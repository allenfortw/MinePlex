package nautilus.game.dominate.engine;

import mineplex.core.common.util.MapUtil;
import net.minecraft.server.v1_6_R3.EntityItem;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PowerUp implements org.bukkit.event.Listener, IPowerUp
{
  private Location _location;
  private Location _blockLocation;
  private long _lastTimePickedUp = -1L;
  
  private long _interval;
  protected JavaPlugin Plugin;
  protected DominateNotifier Notifier;
  protected IDominateGame Game;
  protected Item Item;
  protected Material BlockType;
  
  public PowerUp(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, Location location, long interval)
  {
    this.Plugin = plugin;
    this.Game = game;
    this.Notifier = notifier;
    
    this._location = location;
    this._blockLocation = this._location.getBlock().getRelative(BlockFace.DOWN).getLocation();
    this.BlockType = Material.GOLD_BLOCK;
    this._interval = interval;
    
    this.Plugin.getServer().getPluginManager().registerEvents(this, this.Plugin);
  }
  
  @org.bukkit.event.EventHandler
  public void OnPlayerPickUp(PlayerPickupItemEvent event)
  {
    if (event.getItem().equals(this.Item))
    {
      if ((this.Game.IsPlayerInGame(event.getPlayer())) && (!((CraftPlayer)event.getPlayer()).getHandle().spectating))
      {
        RewardPlayer(event.getPlayer());
        this._lastTimePickedUp = System.currentTimeMillis();
        this.Item.remove();
        
        MapUtil.QuickChangeBlockAt(this._blockLocation.getWorld(), this._blockLocation.getBlockX(), this._blockLocation.getBlockY(), this._blockLocation.getBlockZ(), Material.IRON_BLOCK, 0);
      }
      
      event.setCancelled(true);
    }
  }
  
  public void Update()
  {
    if ((this._lastTimePickedUp == -1L) || (System.currentTimeMillis() >= this._lastTimePickedUp + this._interval))
    {
      this.Item = SpawnItem(this._location);
      MapUtil.QuickChangeBlockAt(this._blockLocation.getWorld(), this._blockLocation.getBlockX(), this._blockLocation.getBlockY(), this._blockLocation.getBlockZ(), this.BlockType, 0);
      this._lastTimePickedUp = (System.currentTimeMillis() + 900000L);
    }
    
    ((EntityItem)((CraftEntity)this.Item).getHandle()).age = 0;
  }
  
  public void Deactivate()
  {
    this._location = null;
    this._blockLocation = null;
    
    this.Plugin = null;
    this.Notifier = null;
    this.Game = null;
    this.Item = null;
    this.BlockType = null;
    
    HandlerList.unregisterAll(this);
  }
  
  protected abstract Item SpawnItem(Location paramLocation);
  
  protected abstract void RewardPlayer(Player paramPlayer);
}
