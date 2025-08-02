package mineplex.minecraft.game.classcombat.Skill.Ranger;

import mineplex.core.common.util.UtilAction;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class HeavyArrows extends Skill
{
  public HeavyArrows(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your arrows are extremely heavy,", 
      "moving 20% slower, also pushing", 
      "you back upon firing them." });
  }
  

  @org.bukkit.event.EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void ShootBow(EntityShootBowEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    

    int level = GetLevel(player);
    if (level == 0) { return;
    }
    
    double vel = event.getProjectile().getVelocity().length() / 3.0D;
    UtilAction.velocity(player, player.getLocation().getDirection().multiply(-1), vel, 
      false, 0.0D, 0.2D, 0.8D, true);
    

    event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(0.8D));
  }
  
  public void Reset(Player player) {}
}
