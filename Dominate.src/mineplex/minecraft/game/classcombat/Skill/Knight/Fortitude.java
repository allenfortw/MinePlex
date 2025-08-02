package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilPlayer;
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

public class Fortitude
  extends Skill
{
  private WeakHashMap<Player, Integer> _health = new WeakHashMap();
  private WeakHashMap<Player, Long> _last = new WeakHashMap();
  
  public Fortitude(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "After taking damage, you slowly", 
      "regenerate up to 5 health, at a", 
      "rate of 1 health per 1.5 seconds.", 
      "", 
      "This does not stack, and is reset", 
      "if you are hit again." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void RegisterLast(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    int damage = (int)(event.GetDamage() / 2.0D);
    if (damage <= 0) { return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    int level = GetLevel(damagee);
    if (level == 0) { return;
    }
    this._health.put(damagee, Integer.valueOf(Math.min(5, damage)));
    this._last.put(damagee, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._health.keySet())
    {
      int level = GetLevel(cur);
      if (level != 0)
      {
        if (UtilTime.elapsed(((Long)this._last.get(cur)).longValue(), 1500L))
        {
          this._health.put(cur, Integer.valueOf(((Integer)this._health.get(cur)).intValue() - 1));
          this._last.put(cur, Long.valueOf(System.currentTimeMillis()));
          
          if (((Integer)this._health.get(cur)).intValue() <= 0) {
            remove.add(cur);
          }
          
          UtilPlayer.health(cur, 1.0D);
        }
      }
    }
    for (Player cur : remove)
    {
      this._health.remove(cur);
      this._last.remove(cur);
    }
  }
  

  public void Reset(Player player)
  {
    this._health.remove(player);
    this._last.remove(player);
  }
}
