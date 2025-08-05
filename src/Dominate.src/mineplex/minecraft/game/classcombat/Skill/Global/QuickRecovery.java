package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;
import org.bukkit.entity.Player;

public class QuickRecovery extends Skill
{
  public QuickRecovery(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You are adept at recovering from Slow.", 
      "Slow durations on you are 50% shorter", 
      "and level is decreased by 1." });
  }
  

  @org.bukkit.event.EventHandler
  public void Resist(ConditionApplyEvent event)
  {
    if (event.GetCondition().GetType() != Condition.ConditionType.SLOW) {
      return;
    }
    event.GetCondition().ModifyTicks((int)(event.GetCondition().GetTicksTotal() * -0.5D));
    event.GetCondition().ModifyMult(-1);
  }
  
  public void Reset(Player player) {}
}
