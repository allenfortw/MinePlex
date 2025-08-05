package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilMath;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class Shadowmeld extends Skill
{
  private WeakHashMap<Player, Long> _crouchTime = new WeakHashMap();
  
  private HashSet<Player> _active = new HashSet();
  
  public Shadowmeld(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Crouch for 2 seconds to meld into", 
      "the shadows, turning invisible,", 
      "and receive Vulnerability II.", 
      "", 
      "Shadowmeld ends if you stop crouching,", 
      "interact or another player comes within", 
      "4 Blocks of you." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 8 per Second";
  }
  
  @EventHandler
  public void Skill(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = GetLevel(cur);
      if (level != 0)
      {

        if ((this._active.contains(cur)) || (this._crouchTime.containsKey(cur))) {
          for (Player other : cur.getWorld().getEntitiesByClass(Player.class))
          {
            if (!other.equals(cur))
            {

              if (UtilMath.offset(cur, other) <= 4.0D)
              {

                End(cur);
                break;
              } }
          }
        }
        if (this._active.contains(cur))
        {
          if (!cur.isSneaking()) {
            End(cur);
          } else {
            this.Factory.Condition().Factory().Cloak(GetName(), cur, cur, 1.9D, false, true);
          }
        } else if (cur.isSneaking())
        {

          if (!this._crouchTime.containsKey(cur))
          {
            this._crouchTime.put(cur, Long.valueOf(System.currentTimeMillis()));

          }
          else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._crouchTime.get(cur)).longValue(), 2000L))
          {
            this._crouchTime.remove(cur);
            this._active.add(cur);
            this.Factory.Condition().Factory().Cloak(GetName(), cur, cur, 1.9D, false, true);
          }
        }
      }
    }
  }
  
  @EventHandler
  public void Energy(UpdateEvent event) {
    if (event.getType() == UpdateType.TICK) {
      for (Player cur : this._active)
        if (!this.Factory.Energy().Use(cur, GetName(), -0.4D, true, true))
          End(cur);
    }
    if (event.getType() == UpdateType.FAST) {
      for (Player cur : this._active)
        this.Factory.Condition().Factory().Vulnerable(GetName(), cur, cur, 2.9D, 1, false, true, true);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamage(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    End(damagee);
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event)
  {
    End(event.getPlayer());
  }
  
  public void End(Player player)
  {
    if (this._active.remove(player)) {
      this.Factory.Condition().EndCondition(player, mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, GetName());
    }
    this._crouchTime.remove(player);
  }
  

  public void Reset(Player player)
  {
    End(player);
  }
}
