package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

public class Stampede extends Skill
{
  private WeakHashMap<Player, Long> _sprintTime = new WeakHashMap();
  private WeakHashMap<Player, Integer> _sprintStr = new WeakHashMap();
  
  public Stampede(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You slowly build up speed as you", 
      "sprint. You gain a level of Speed", 
      "for every 3 seconds, up to a max", 
      "of Speed 3.", 
      "", 
      "Attacking during stampede deals", 
      "2 bonus damage per speed level,", 
      "as well as large knockback." });
  }
  

  @EventHandler
  public void Skill(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = GetLevel(cur);
      if (level != 0)
      {

        if (this._sprintTime.containsKey(cur))
        {

          if (!cur.isSprinting())
          {
            this._sprintTime.remove(cur);
            this._sprintStr.remove(cur);
            cur.removePotionEffect(PotionEffectType.SPEED);
          }
          else
          {
            long time = ((Long)this._sprintTime.get(cur)).longValue();
            int str = ((Integer)this._sprintStr.get(cur)).intValue();
            

            if (str > 0) {
              this.Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9D, str - 1, false, true, true);
            }
            
            if (mineplex.core.common.util.UtilTime.elapsed(time, 3000L))
            {

              this._sprintTime.put(cur, Long.valueOf(System.currentTimeMillis()));
              
              if (str < 3)
              {
                this._sprintStr.put(cur, Integer.valueOf(str + 1));
                

                cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_IDLE, 2.0F, 0.2F * str + 1.0F);
              }
              

              UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(cur, GetName(), IPvpClass.ClassType.Brute));
            }
          } } else if (cur.isSprinting())
        {

          if (!this._sprintTime.containsKey(cur))
          {
            this._sprintTime.put(cur, Long.valueOf(System.currentTimeMillis()));
            this._sprintStr.put(cur, Integer.valueOf(0));
          }
        }
      }
    }
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
    if (!this._sprintStr.containsKey(damager)) {
      return;
    }
    if (((Integer)this._sprintStr.get(damager)).intValue() == 0) {
      return;
    }
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    org.bukkit.entity.LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    this._sprintTime.remove(damager);
    int str = ((Integer)this._sprintStr.remove(damager)).intValue();
    damager.removePotionEffect(PotionEffectType.SPEED);
    

    event.AddMod(damager.getName(), GetName(), str * 2, true);
    event.SetKnockback(false);
    

    if (mineplex.core.common.util.UtilEnt.isGrounded(damagee)) {
      UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 3.0D, true, 0.0D, 0.8D, 1.0D, true);
    } else {
      UtilAction.velocity(damagee, UtilAlg.getTrajectory2d(damager, damagee), 1.5D, true, 0.0D, 0.8D, 1.0D, true);
    }
    
    UtilPlayer.message(damager, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    

    damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_WOOD, 1.0F, 0.4F * str);
    

    UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(damager, GetName(), IPvpClass.ClassType.Brute, damagee));
  }
  

  public void Reset(Player player)
  {
    this._sprintTime.remove(player);
    this._sprintStr.remove(player);
  }
}
