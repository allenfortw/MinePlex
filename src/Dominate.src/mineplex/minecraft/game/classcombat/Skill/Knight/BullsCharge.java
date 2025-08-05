package mineplex.minecraft.game.classcombat.Skill.Knight;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;





public class BullsCharge
  extends SkillActive
{
  public BullsCharge(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Charge forwards with Speed II for", 
      "8 seconds. If you attack during this", 
      "time, your target receives Slow 4", 
      "for 4 seconds, as well as no knockback." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this.Factory.Condition().Factory().Speed(GetName(), player, player, 8.0D, 1, false, true, true);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_SCREAM, 1.5F, 0.0F);
    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 49);
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
    Player damager = event.GetDamagerPlayer(true);
    LivingEntity damagee = event.GetDamageeEntity();
    
    if ((damager == null) || (damagee == null)) {
      return;
    }
    
    if (!this.Factory.Condition().HasCondition(damager, Condition.ConditionType.SPEED, GetName())) {
      return;
    }
    
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    
    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, 4.0D, 3, false, true, true, true);
    this.Factory.Condition().EndCondition(damager, Condition.ConditionType.SPEED, GetName());
    


    event.SetKnockback(false);
    

    damager.getWorld().playSound(damager.getLocation(), Sound.ENDERMAN_SCREAM, 1.5F, 0.0F);
    damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_METAL, 1.5F, 0.5F);
    

    UtilPlayer.message(damagee, F.main(GetClassType().name(), 
      F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
    
    UtilPlayer.message(damager, F.main(GetClassType().name(), 
      "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill(GetName(level)) + "."));
  }
  
  public void Reset(Player player) {}
}
