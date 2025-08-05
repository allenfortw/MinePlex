package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Barrage extends Skill
{
  private WeakHashMap<Player, Integer> _charge = new WeakHashMap();
  private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap();
  
  private HashSet<Player> _firing = new HashSet();
  private HashSet<Projectile> _arrows = new HashSet();
  
  public Barrage(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Load an extra arrow into your bow", 
      "while charging, every 0.2 seconds.", 
      "", 
      "Maximum of 20 additional arrows." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 3 per Arrow";
  }
  
  @EventHandler
  public void DrawBow(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(event.getItem(), Material.BOW)) {
      return;
    }
    if (!mineplex.core.common.util.UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    if (!player.getInventory().contains(Material.ARROW)) {
      return;
    }
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9)) {
      return;
    }
    if (event.getClickedBlock() != null)
    {
      if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
        return;
      }
    }
    
    int level = GetLevel(player);
    if (level == 0) { return;
    }
    
    this._charge.put(player, Integer.valueOf(0));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
    this._firing.remove(player);
  }
  
  @EventHandler
  public void Charge(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (this._charge.containsKey(cur))
      {

        if (!this._firing.contains(cur))
        {

          int level = GetLevel(cur);
          if (level != 0)
          {

            if (((Integer)this._charge.get(cur)).intValue() < 20)
            {


              if (((Integer)this._charge.get(cur)).intValue() == 0 ? 
              
                UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 1200L) : 
                



                UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 100L))
              {



                if (!UtilGear.isMat(cur.getItemInHand(), Material.BOW))
                {
                  this._charge.remove(cur);
                  this._chargeLast.remove(cur);



                }
                else if (this.Factory.Energy().Use(cur, GetName(), 3.0D, true, false))
                {


                  this._charge.put(cur, Integer.valueOf(((Integer)this._charge.get(cur)).intValue() + 1));
                  this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
                  

                  if (((Integer)this._charge.get(cur)).intValue() % 5 == 0) {
                    UtilPlayer.message(cur, F.main(GetClassType().name(), "Barrage: " + F.elem(new StringBuilder("+").append(this._charge.get(cur)).append(" Arrows").toString())));
                  }
                  
                  cur.playSound(cur.getLocation(), Sound.CLICK, 0.4F, 1.0F + 0.05F * ((Integer)this._charge.get(cur)).intValue());
                } } } }
        } } }
  }
  
  @EventHandler
  public void FireBow(EntityShootBowEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._charge.containsKey(player)) {
      return;
    }
    
    this._firing.add(player);
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Skill(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._firing)
    {
      if ((!this._charge.containsKey(cur)) || (!this._chargeLast.containsKey(cur)))
      {
        remove.add(cur);


      }
      else if (!UtilGear.isBow(cur.getItemInHand()))
      {
        remove.add(cur);
      }
      else
      {
        int arrows = ((Integer)this._charge.get(cur)).intValue();
        if (arrows <= 0)
        {
          remove.add(cur);


        }
        else
        {


          this._charge.put(cur, Integer.valueOf(arrows - 1));
          

          Vector random = new Vector((Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D);
          Projectile arrow = cur.launchProjectile(Arrow.class);
          arrow.setVelocity(cur.getLocation().getDirection().add(random).multiply(3));
          this._arrows.add(arrow);
          cur.getWorld().playSound(cur.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
        }
      } }
    for (Player cur : remove)
    {
      this._charge.remove(cur);
      this._chargeLast.remove(cur);
      this._firing.remove(cur);
    }
  }
  
  @EventHandler
  public void ProjectileHit(ProjectileHitEvent event)
  {
    if (this._arrows.remove(event.getEntity())) {
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void Clean(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Projectile> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Projectile arrow = (Projectile)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._chargeLast.remove(player);
    this._firing.remove(player);
  }
}
