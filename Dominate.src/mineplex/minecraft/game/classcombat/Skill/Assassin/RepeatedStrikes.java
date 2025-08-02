package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilTime;
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

public class RepeatedStrikes
  extends Skill
{
  private WeakHashMap<Player, Integer> _repeat = new WeakHashMap();
  private WeakHashMap<Player, Long> _last = new WeakHashMap();
  
  public RepeatedStrikes(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Each time you attack, your damage", 
      "increases by 1. You can get up to", 
      "3 bonus damage.", 
      "", 
      "Not attacking for 2 seconds clears", 
      "your bonus damage." });
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
    
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    if (!this._repeat.containsKey(damager)) {
      this._repeat.put(damager, Integer.valueOf(0));
    }
    
    event.AddMod(damager.getName(), GetName(), ((Integer)this._repeat.get(damager)).intValue(), true);
    

    this._repeat.put(damager, Integer.valueOf(Math.min(3, ((Integer)this._repeat.get(damager)).intValue() + 1)));
    this._last.put(damager, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._repeat.keySet()) {
      if (UtilTime.elapsed(((Long)this._last.get(cur)).longValue(), 2000L))
        remove.add(cur);
    }
    for (Player cur : remove)
    {
      this._repeat.remove(cur);
      this._last.remove(cur);
    }
  }
  

  public void Reset(Player player)
  {
    this._repeat.remove(player);
    this._last.remove(player);
  }
}
