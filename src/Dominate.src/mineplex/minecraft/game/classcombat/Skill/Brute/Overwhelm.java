package mineplex.minecraft.game.classcombat.Skill.Brute;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Overwhelm extends Skill
{
  public Overwhelm(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You deal 1 bonus damage for every", 
      "2 more health you have than your", 
      "target. You can deal a maximum of", 
      "3 bonus damage." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    double diff = (damager.getHealth() - damagee.getHealth()) / 2.0D;
    
    if (diff <= 0.0D) {
      return;
    }
    
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    diff = Math.min(diff, 3.0D);
    

    event.AddMod(damager.getName(), GetName(), diff, true);
  }
  
  public void Reset(Player player) {}
}
