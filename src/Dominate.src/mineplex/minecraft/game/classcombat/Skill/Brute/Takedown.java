package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class Takedown extends SkillActive
{
  private HashMap<LivingEntity, Long> _live = new HashMap();
  










  public Takedown(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hurl yourself towards an opponent.", 
      "If you collide with them, you " + mineplex.core.common.util.C.cWhite + "both", 
      "take 10 damage and receive Slow 4", 
      "for 6 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (UtilEnt.isGrounded(player))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " while grounded."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Vector vec = player.getLocation().getDirection();
    
    UtilAction.velocity(player, vec, 1.2D, false, 0.0D, 0.2D, 0.4D, false);
    

    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @org.bukkit.event.EventHandler
  public void End(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    
    for (Player player : GetUsers())
    {
      if (UtilEnt.isGrounded(player))
      {

        if (this._live.containsKey(player))
        {

          int level = GetLevel(player);
          if (level != 0)
          {
            if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._live.get(player)).longValue(), 1000L))
            {

              this._live.remove(player); } }
        }
      }
    }
    for (Player player : GetUsers()) {
      if (this._live.containsKey(player)) {
        for (Player other : player.getWorld().getPlayers()) {
          if ((other.getGameMode() == GameMode.SURVIVAL) && 
            (!other.equals(player)) && 
            (this.Factory.Relation().CanHurt(player, other)) && 
            (UtilMath.offset(player, other) < 2.0D))
          {
            DoTakeDown(player, other);
            this._live.remove(player);
            return;
          }
        }
      }
    }
  }
  





























  public void DoTakeDown(Player damager, LivingEntity damagee)
  {
    int level = GetLevel(damager);
    int damage = 10;
    

    this.Factory.Damage().NewDamageEvent(damager, damagee, null, 
      EntityDamageEvent.DamageCause.CUSTOM, damage, false, true, false, 
      damager.getName(), GetName() + " Recoil");
    

    this.Factory.Damage().NewDamageEvent(damagee, damager, null, 
      EntityDamageEvent.DamageCause.CUSTOM, damage, false, true, false, 
      damager.getName(), GetName());
    

    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, 6.0D, 3, false, true, true, true);
    this.Factory.Condition().Factory().Slow(GetName(), damager, damager, 6.0D, 3, false, true, true, true);
    

    UtilPlayer.message(damager, F.main(GetClassType().name(), "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
  }
  

  public void Reset(Player player)
  {
    this._live.remove(player);
  }
}
