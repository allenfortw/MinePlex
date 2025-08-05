package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class LevelField extends Skill
{
  public LevelField(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Even the battlefield with courage!", 
      "You deal X more damage.", 
      "You take X less damage.", 
      "X = (Nearby Enemies) - (Nearby Allies)", 
      "Players within 8 Blocks are considered.", 
      "", 
      "Damage can be altered a maximum of 3.", 
      "You can not deal less damage, or take", 
      "more damage via this." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Decrease(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    org.bukkit.entity.LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    
    int level = GetLevel(damagee);
    if (level == 0) { return;
    }
    int alt = 0;
    
    for (Player cur : UtilPlayer.getNearby(damagee.getLocation(), 8.0D))
    {
      if (cur.equals(damagee)) {
        alt++;
      }
      else if (this.Factory.Relation().CanHurt(damagee, cur)) {
        alt--;
      }
      else {
        alt++;
      }
    }
    if (alt > 3) alt = 3;
    if (alt < -3) { alt = -3;
    }
    if (alt >= 0) {
      return;
    }
    
    event.AddMod(damagee.getName(), GetName(), alt, false);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Increase(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    int alt = 0;
    
    for (Player cur : UtilPlayer.getNearby(damager.getLocation(), 8.0D))
    {
      if (cur.equals(damager)) {
        alt--;
      }
      else if (this.Factory.Relation().CanHurt(damager, cur)) {
        alt++;
      }
      else {
        alt--;
      }
    }
    if (alt > 3) alt = 3;
    if (alt < -3) { alt = -3;
    }
    if (alt <= 0) {
      return;
    }
    
    event.AddMod(damager.getName(), GetName(), alt, false);
  }
  
  public void Reset(Player player) {}
}
