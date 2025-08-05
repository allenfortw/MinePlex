package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class BlockToss extends Skill implements mineplex.core.projectile.IThrown
{
  private HashMap<Player, FallingBlock> _holding = new HashMap();
  private HashMap<Player, Long> _charge = new HashMap();
  private HashSet<Player> _charged = new HashSet();
  private HashMap<FallingBlock, Player> _falling = new HashMap();
  
  public BlockToss(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Hold Block to pick up a block,", 
      "Release Block to throw it, dealing", 
      "up to 12 damage.", 
      "", 
      "You must hold the block for", 
      "1 second for full throw power.", 
      "", 
      "You can only pick up Stone, Dirt,", 
      "Cobblestone, Sand, Gravel or Snow." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 40";
  }
  
  @EventHandler
  public void Grab(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    if (!mineplex.core.common.util.UtilGear.isSword(player.getItemInHand())) {
      return;
    }
    if (this._holding.containsKey(player)) {
      return;
    }
    
    int level = GetLevel(player);
    if (level == 0) { return;
    }
    Block grab = event.getClickedBlock();
    
    int id = event.getClickedBlock().getTypeId();
    

    if ((id != 1) && 
      (id != 2) && 
      (id != 3) && 
      (id != 12) && 
      (id != 13) && 
      (id != 80)) {
      return;
    }
    
    if ((grab.getRelative(BlockFace.UP).getTypeId() == 64) || (grab.getRelative(BlockFace.UP).getTypeId() == 71))
    {
      UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
      return;
    }
    

    if ((grab.getRelative(BlockFace.NORTH).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.SOUTH).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.EAST).getType() == Material.TRAP_DOOR) || 
      (grab.getRelative(BlockFace.WEST).getType() == Material.TRAP_DOOR))
    {
      UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
      return;
    }
    

    if (!this.Factory.Energy().Use(player, GetName(level), 40.0D, true, true)) {
      return;
    }
    
    FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), event.getClickedBlock().getType(), (byte)0);
    this.Factory.BlockRestore().Add(event.getClickedBlock(), 0, (byte)0, 10000L);
    

    this.Factory.Condition().SetIndicatorVisibility(player, false);
    

    player.eject();
    player.setPassenger(block);
    this._holding.put(player, block);
    this._falling.put(block, player);
    this._charge.put(player, Long.valueOf(System.currentTimeMillis()));
    

    player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, block.getMaterial().getId());
  }
  
  @EventHandler
  public void Throw(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> voidSet = new HashSet();
    HashSet<Player> throwSet = new HashSet();
    
    for (Player cur : this._holding.keySet())
    {
      if (cur.getPassenger() == null)
      {
        voidSet.add(cur);


      }
      else if (((FallingBlock)this._holding.get(cur)).getVehicle() == null)
      {
        voidSet.add(cur);


      }
      else if (!((FallingBlock)this._holding.get(cur)).getVehicle().equals(cur))
      {
        voidSet.add(cur);

      }
      else
      {
        if (!cur.isBlocking()) {
          throwSet.add(cur);
        }
        
        if ((!this._charged.contains(cur)) && 
          (System.currentTimeMillis() - ((Long)this._charge.get(cur)).longValue() > 1000L))
        {
          this._charged.add(cur);
          cur.playEffect(cur.getLocation(), Effect.CLICK1, 0);
        }
      }
    }
    for (Player cur : voidSet)
    {
      FallingBlock block = (FallingBlock)this._holding.remove(cur);
      this._charge.remove(cur);
      this._charged.remove(cur);
      block.remove();
    }
    
    for (Player cur : throwSet)
    {
      FallingBlock block = (FallingBlock)this._holding.remove(cur);
      this._charged.remove(cur);
      long charge = System.currentTimeMillis() - ((Long)this._charge.remove(cur)).longValue();
      

      cur.eject();
      double mult = 1.0D;
      if (charge < 1000L) {
        mult *= charge / 1000.0D;
      }
      
      this.Factory.Condition().SetIndicatorVisibility(cur, true);
      

      UtilAction.velocity(block, cur.getLocation().getDirection(), mult, false, 0.0D, 0.0D, 1.0D, true);
      this.Factory.Projectile().AddThrow(block, cur, this, -1L, true, true, true, 
        null, 0.0F, 0.0F, null, 0, UpdateType.FASTEST, 2.0D);
      

      UtilAction.velocity(cur, cur.getLocation().getDirection().multiply(-1), 0.4D, false, 0.0D, 0.0D, 1.0D, false);
      

      mineplex.core.common.util.UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(cur, GetName(), IPvpClass.ClassType.Brute));
    }
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    
    this.Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 2.0D + data.GetThrown().getVelocity().length() * 10.0D, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
    

    if ((data.GetThrown() instanceof FallingBlock))
    {
      FallingBlock thrown = (FallingBlock)data.GetThrown();
      
      FallingBlock newThrown = data.GetThrown().getWorld().spawnFallingBlock(data.GetThrown().getLocation(), thrown.getMaterial(), (byte)0);
      

      this._falling.remove(thrown);
      thrown.remove();
      

      if ((data.GetThrower() instanceof Player)) {
        this._falling.put(newThrown, (Player)data.GetThrower());
      }
    }
  }
  



  public void Idle(ProjectileUser data) {}
  


  public void Expire(ProjectileUser data) {}
  


  @EventHandler
  public void CreateBlock(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<FallingBlock> fallen = new HashSet();
    
    for (FallingBlock cur : this._falling.keySet())
    {
      if ((cur.isDead()) || (!cur.isValid())) {
        fallen.add(cur);
      }
    }
    for (FallingBlock cur : fallen)
    {
      this._falling.remove(cur);
      Block block = cur.getLocation().getBlock();
      
      int id = block.getTypeId();
      
      if ((id == 1) || 
        (id == 2) || 
        (id == 3) || 
        (id == 4) || 
        (id == 12) || 
        (id == 13) || 
        (id == 80))
      {

        block.setTypeIdAndData(0, (byte)0, false);
        

        this.Factory.BlockRestore().Add(block, cur.getBlockId(), (byte)0, 10000L);
        

        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
      }
    }
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event) {
    int id = event.getEntity().getItemStack().getTypeId();
    

    if ((id != 1) && 
      (id != 2) && 
      (id != 3) && 
      (id != 4) && 
      (id != 12) && 
      (id != 13) && 
      (id != 80)) {
      return;
    }
    for (FallingBlock block : this._falling.keySet()) {
      if (mineplex.core.common.util.UtilMath.offset(event.getEntity().getLocation(), block.getLocation()) < 1.0D) {
        event.setCancelled(true);
      }
    }
  }
  
  public void Reset(Player player) {
    if (this._holding.containsKey(player))
    {
      player.eject();
    }
    
    this._holding.remove(player);
    this._charge.remove(player);
    this._charged.remove(player);
  }
}
