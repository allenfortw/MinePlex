package mineplex.minecraft.game.classcombat.Skill.Ranger;

import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.entity.Player;

public class VitalitySpores extends Skill
{
  public VitalitySpores(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "After 10 seconds of not taking damage,", 
      "forest spores surround you, giving", 
      "you Regeneration 1 for 6 seconds.", 
      "", 
      "This remains until you take damage." });
  }
  

  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = GetLevel(cur);
      if (level != 0)
      {
        if (mineplex.core.common.util.UtilTime.elapsed(this.Factory.Combat().Get(cur).LastDamaged, 10000L))
        {
          this.Factory.Condition().Factory().Regen(GetName(), cur, cur, 6.9D, 0, false, true, true);
          UtilPlayer.health(cur, 1.0D);
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
