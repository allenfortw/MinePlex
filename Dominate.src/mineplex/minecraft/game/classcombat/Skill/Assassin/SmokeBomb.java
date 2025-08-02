package mineplex.minecraft.game.classcombat.Skill.Assassin;

import mineplex.core.common.util.F;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.Condition;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SmokeBomb extends Skill
{
  public SmokeBomb(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Use.", 
      "", 
      "Create a blast of smoke, turning", 
      "you Invisible for 8 seconds. You ", 
      "also receive Vulnerability 4 for", 
      "6 seconds.", 
      "", 
      "You leave a trail of smoke while", 
      "you are Invisible." });
  }
  


  public String GetRechargeString()
  {
    return "Recharge: 1 Minute";
  }
  
  @EventHandler
  public void Use(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    int level = GetLevel(player);
    if (level == 0) { return;
    }
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, GetName(), GetName(level), 120000 - level * 30000, true)) {
      return;
    }
    
    this.Factory.Condition().Factory().Cloak(GetName(), player, player, 8.0D, false, true);
    this.Factory.Condition().Factory().Vulnerable(GetName(), player, player, 6.0D, 3, false, true, true);
    





    for (int i = 0; i < 3; i++)
    {
      player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.FIZZ, 2.0F, 0.5F);
      player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 80);
    }
    


    mineplex.core.common.util.UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamagee(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (GetLevel(damagee) == 0) {
      return;
    }
    
    this.Factory.Condition().EndCondition(damagee, null, GetName());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void EndDamager(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (GetLevel(damager) == 0) {
      return;
    }
    
    this.Factory.Condition().EndCondition(damager, null, GetName());
  }
  
  @EventHandler
  public void EndInteract(PlayerInteractEvent event)
  {
    if (GetLevel(event.getPlayer()) == 0) {
      return;
    }
    this.Factory.Condition().EndCondition(event.getPlayer(), null, GetName());
  }
  
  @EventHandler
  public void Smoke(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      Condition cond = this.Factory.Condition().GetActiveCondition(cur, Condition.ConditionType.CLOAK);
      if (cond != null)
      {
        if (cond.GetReason().equals(GetName()))
        {


          cur.getWorld().playEffect(cur.getLocation(), Effect.SMOKE, 4);
        }
      }
    }
  }
  
  public void Reset(Player player)
  {
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
}
