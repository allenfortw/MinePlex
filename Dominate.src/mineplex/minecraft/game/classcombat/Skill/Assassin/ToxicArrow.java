package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ToxicArrow extends SkillActive
{
  private HashSet<Entity> _arrows = new HashSet();
  private HashSet<Player> _poison = new HashSet();
  










  public ToxicArrow(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Your next arrow will give Confusion", 
      "to target for 20 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._poison.add(player);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.BREATH, 2.5F, 2.0F);
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._poison.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You fired " + F.skill(GetName(GetLevel(player))) + "."));
    
    this._arrows.add(event.getProjectile());
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    org.bukkit.entity.Projectile projectile = event.GetProjectile();
    if (projectile == null) { return;
    }
    if (!this._arrows.contains(projectile)) {
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
    
    double confuseDur = 20.0D;
    this.Factory.Condition().Factory().Confuse(GetName(), damagee, damager, confuseDur, 0, true, true, true);
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.BREATH, 2.5F, 2.0F);
    

    UtilPlayer.message(event.GetDamageePlayer(), F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(mineplex.core.common.util.UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
    

    event.AddMod(damager.getName(), GetName(), 0.0D, true);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC)
      return;
    HashSet<Entity> remove = new HashSet();
    
    for (Entity cur : this._arrows) {
      if (cur.isDead())
        remove.add(cur);
    }
    for (Entity cur : remove) {
      this._arrows.remove(cur);
    }
  }
  
  public void Reset(Player player)
  {
    this._poison.remove(player);
  }
}
