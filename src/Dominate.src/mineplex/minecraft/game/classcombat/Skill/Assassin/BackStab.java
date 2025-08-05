package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.core.energy.Energy;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class BackStab extends Skill
{
  public BackStab(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Attacks from behind opponents", 
      "deals 4 additional damage." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 10";
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    Vector look = damagee.getLocation().getDirection();
    look.setY(0);
    look.normalize();
    
    Vector from = damager.getLocation().toVector().subtract(damagee.getLocation().toVector());
    from.setY(0);
    from.normalize();
    
    Vector check = new Vector(look.getX() * -1.0D, 0.0D, look.getZ() * -1.0D);
    if (check.subtract(from).length() < 0.8D)
    {
      if (!this.Factory.Energy().Use(damager, GetName(), 10.0D, true, false)) {
        return;
      }
      
      event.AddMod(damager.getName(), GetName(), 1 + level, true);
      

      damagee.getWorld().playSound(damagee.getLocation(), org.bukkit.Sound.HURT, 1.0F, 2.0F);
      return;
    }
  }
  
  public void Reset(Player player) {}
}
