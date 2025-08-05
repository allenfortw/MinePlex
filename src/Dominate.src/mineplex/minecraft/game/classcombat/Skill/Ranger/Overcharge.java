package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent;
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
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class Overcharge extends Skill
{
  private HashMap<Entity, Integer> _arrows = new HashMap();
  private WeakHashMap<Player, Integer> _charge = new WeakHashMap();
  private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap();
  
  public Overcharge(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Draw back harder on your bow, giving", 
      "2 bonus damage per 0.8 seconds", 
      "", 
      "Maximum of 8 bonus damage" });
  }
  

  @EventHandler
  public void Interact(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(event.getItem(), Material.BOW)) {
      return;
    }
    if (!UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R)) {
      return;
    }
    if (!player.getInventory().contains(Material.ARROW)) {
      return;
    }
    if (event.getClickedBlock() != null)
    {
      if (UtilBlock.usable(event.getClickedBlock())) {
        return;
      }
    }
    
    int level = GetLevel(player);
    if (level == 0) { return;
    }
    
    this._charge.put(player, Integer.valueOf(0));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (this._charge.containsKey(cur))
      {


        if (((Integer)this._charge.get(cur)).intValue() < 4)
        {


          if (((Integer)this._charge.get(cur)).intValue() == 0 ? 
          
            UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 1200L) : 
            



            UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 800L))
          {



            if (!UtilGear.isMat(cur.getItemInHand(), Material.BOW))
            {
              this._charge.remove(cur);
              this._chargeLast.remove(cur);



            }
            else if (this.Factory.Energy().Use(cur, GetName(), 10.0D, true, false))
            {


              this._charge.put(cur, Integer.valueOf(((Integer)this._charge.get(cur)).intValue() + 1));
              this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
              

              UtilPlayer.message(cur, F.main(GetClassType().name(), "Overcharge: " + F.elem(new StringBuilder("+").append(((Integer)this._charge.get(cur)).intValue() * 2).append(" Damage").toString())));
              

              for (int i = ((Integer)this._charge.get(cur)).intValue(); i > 0; i--)
                cur.playEffect(cur.getLocation(), Effect.CLICK2, 0);
            } } } }
    }
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event) {
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
    
    this._arrows.put(event.getProjectile(), (Integer)this._charge.get(player));
    

    this._charge.remove(player);
    this._chargeLast.remove(player);
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void ArrowHit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile projectile = event.GetProjectile();
    if (projectile == null) { return;
    }
    if (!this._arrows.containsKey(projectile)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    int damage = ((Integer)this._arrows.remove(projectile)).intValue() * 2;
    

    event.AddMod(damager.getName(), GetName(), damage, true);
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.HURT_FLESH, 1.0F, 0.5F);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    HashSet<Entity> remove = new HashSet();
    
    for (Entity cur : this._arrows.keySet()) {
      if ((cur.isDead()) || (!cur.isValid()))
        remove.add(cur);
    }
    for (Entity cur : remove) {
      this._arrows.remove(cur);
    }
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._chargeLast.remove(player);
  }
}
