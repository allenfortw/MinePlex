package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class Longshot extends Skill
{
  private HashSet<Player> _zoomed = new HashSet();
  private HashMap<Entity, Location> _arrows = new HashMap();
  
  public Longshot(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Arrows do an additional", 
      "1 damage per 3 Blocks travelled,", 
      "however, their base damage is", 
      "reduced by 3.", 
      "", 
      "Maximum of 20 additional damage." });
  }
  

  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    int level = GetLevel((Player)event.getEntity());
    if (level == 0) { return;
    }
    
    this._arrows.put(event.getProjectile(), event.getProjectile().getLocation());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
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
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    Location loc = (Location)this._arrows.remove(projectile);
    double length = mineplex.core.common.util.UtilMath.offset(loc, projectile.getLocation());
    

    double damage = Math.min(20.0D, length / 3.0D - 3.0D);
    
    event.AddMod(damager.getName(), GetName(), damage, damage > 0.0D);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC)
      return;
    HashSet<Entity> remove = new HashSet();
    
    for (Entity cur : this._arrows.keySet()) {
      if ((cur.isDead()) || (!cur.isValid()))
        remove.add(cur);
    }
    for (Entity cur : remove)
    {
      this._arrows.remove(cur);
    }
  }
  

  public void ToggleZoom(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (GetLevel(player) == 0) {
      return;
    }
    if (UtilGear.isWeapon(player.getItemInHand())) {
      return;
    }
    event.setCancelled(true);
    

    if (this._zoomed.contains(player))
    {
      this._zoomed.remove(player);
      UtilPlayer.message(player, F.main(GetClassType().name(), "Hawks Eye: " + F.oo("Disabled", false)));
      this.Factory.Condition().EndCondition(player, Condition.ConditionType.SLOW, GetName());

    }
    else
    {
      this._zoomed.add(event.getPlayer());
      UtilPlayer.message(event.getPlayer(), F.main(GetClassType().name(), "Hawks Eye: " + F.oo("Enabled", true)));
    }
  }
  

  public void Zoom(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> zoomIterator = this._zoomed.iterator();
    
    while (zoomIterator.hasNext())
    {
      Player cur = (Player)zoomIterator.next();
      
      if (!cur.isOnline())
      {
        zoomIterator.remove();


      }
      else if (GetLevel(cur) > 0) {
        this.Factory.Condition().Factory().Slow(GetName(), cur, cur, 1.9D, 1 + GetLevel(cur), false, true, false, true);
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._zoomed.remove(player);
  }
}
