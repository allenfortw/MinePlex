package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.core.common.util.UtilPlayer;
import mineplex.core.movement.ClientMovement;
import mineplex.core.movement.Movement;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Rations extends Skill
{
  public Rations(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "After not moving for 3 seconds,", 
      "you snack on rations, slowly", 
      "replenishing your hunger." });
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {
      int level = GetLevel(cur);
      
      if (level > 0)
      {
        if (mineplex.core.common.util.UtilTime.elapsed(((ClientMovement)this.Factory.Movement().Get(cur)).LastMovement, 3000L))
        {
          cur.setSaturation(0.0F);
          cur.setExhaustion(0.0F);
          UtilPlayer.hunger(cur, 1);
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
