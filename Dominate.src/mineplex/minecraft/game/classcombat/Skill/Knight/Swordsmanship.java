package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import mineplex.core.common.util.UtilGear;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Swordsmanship
  extends Skill
{
  private HashMap<Player, Integer> _charges = new HashMap();
  
  public Swordsmanship(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Prepare a powerful sword attack;", 
      "You gain 1 Charge every 3 seconds.", 
      "You can store a maximum of 3 Charges.", 
      "", 
      "When you attacked, your damage is", 
      "increased by the number of your Charges,", 
      "and your Charges reset to 0.", 
      "", 
      "This only applies for Swords." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void IncreaseDamage(CustomDamageEvent event)
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
    if (!this._charges.containsKey(damager)) {
      return;
    }
    if (!UtilGear.isSword(damager.getItemInHand())) {
      return;
    }
    event.AddMod(damager.getName(), GetName(), ((Integer)this._charges.remove(damager)).intValue(), false);
  }
  
  @EventHandler
  public void Charge(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (Recharge.Instance.use(cur, GetName(), 3000L, false))
      {

        int charge = 1;
        if (this._charges.containsKey(cur)) {
          charge += ((Integer)this._charges.get(cur)).intValue();
        }
        charge = Math.min(3, charge);
        
        this._charges.put(cur, Integer.valueOf(charge));
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._charges.remove(player);
  }
}
