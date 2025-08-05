package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HiltSmash extends SkillActive
{
  private HashSet<Player> _used = new HashSet();
  










  public HiltSmash(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Smash the hilt of your sword into", 
      "your opponent, dealing 6 damage", 
      "and Slow 5 for 0.5 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if (this._used.contains(player)) {
      return false;
    }
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Miss(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    this._used.clear();
  }
  
  public boolean CanUse(Player player)
  {
    int level = GetLevel(player);
    if (level == 0) {
      return false;
    }
    
    if (!this._itemSet.contains(player.getItemInHand().getType())) {
      return false;
    }
    
    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return false;
    }
    
    if (!EnergyRechargeCheck(player, level)) {
      return false;
    }
    
    return true;
  }
  
  @EventHandler
  public void Hit(PlayerInteractEntityEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    

    int level = GetLevel(player);
    if (level == 0) { return;
    }
    if (!CanUse(player)) {
      return;
    }
    org.bukkit.entity.Entity ent = event.getRightClicked();
    
    if (ent == null) {
      return;
    }
    if (!(ent instanceof LivingEntity)) {
      return;
    }
    if (UtilMath.offset(player, ent) > 2.2D)
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You missed " + F.skill(GetName()) + "."));
      return;
    }
    

    this._used.add(player);
    

    this.Factory.Damage().NewDamageEvent((LivingEntity)ent, player, null, 
      EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5.0D, false, true, false, 
      player.getName(), GetName());
  }
  
  @EventHandler
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
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    
    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, 0.5D, 4, false, true, true, true);
    

    damagee.getWorld().playSound(damagee.getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 1.0F, 1.2F);
    

    UtilPlayer.message(damager, F.main(GetClassType().name(), "You used " + F.skill(GetName()) + "."));
    UtilPlayer.message(damagee, F.main(GetClassType().name(), F.name(damager.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
  }
  

  public void Reset(Player player)
  {
    this._used.remove(player);
  }
}
