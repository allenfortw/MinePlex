package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.events.ConditionApplyEvent;
import org.bukkit.entity.Player;

public class Resistance extends Skill
{
  public Resistance(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your body and mind is exceptionally resistant.", 
      "Durations on you are 50% shorter for;", 
      "Fire, Shock, Confusion, Poison, Blindness." });
  }
  

  @org.bukkit.event.EventHandler
  public void Resist(ConditionApplyEvent event)
  {
    if ((event.GetCondition().GetType() != Condition.ConditionType.BURNING) && 
      (event.GetCondition().GetType() != Condition.ConditionType.SHOCK) && 
      (event.GetCondition().GetType() != Condition.ConditionType.CONFUSION) && 
      (event.GetCondition().GetType() != Condition.ConditionType.POISON) && 
      (event.GetCondition().GetType() != Condition.ConditionType.BLINDNESS)) {
      return;
    }
    event.GetCondition().ModifyTicks((int)(event.GetCondition().GetTicksTotal() * 0.5D));
  }
  
  public void Reset(Player player) {}
}
