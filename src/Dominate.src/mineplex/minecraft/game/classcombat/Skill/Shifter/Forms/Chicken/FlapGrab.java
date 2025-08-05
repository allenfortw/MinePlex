package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FlapGrab
{
  public Flap Host;
  private HashMap<Player, LivingEntity> _clutch = new HashMap();
  
  public FlapGrab(Flap host)
  {
    this.Host = host;
  }
  
  public void Grab(Player player, LivingEntity ent)
  {
    if (this._clutch.containsKey(player)) {
      return;
    }
    if (ent == null) {
      return;
    }
    
    if ((ent.getPassenger() != null) && 
      ((ent.getPassenger() instanceof LivingEntity))) {
      return;
    }
    
    if (player.getVehicle() != null) {
      return;
    }
    
    this.Host.Factory.Condition().SetIndicatorVisibility(ent, false);
    

    player.leaveVehicle();
    ent.eject();
    ent.setPassenger(player);
    this._clutch.put(player, ent);
    

    UtilPlayer.message(player, F.main(this.Host.GetClassType().name(), "You picked up " + F.name(UtilEnt.getName(ent)) + "."));
    UtilPlayer.message(ent, F.main(this.Host.GetClassType().name(), F.name(player.getName()) + " picked you up."));
    

    ent.playEffect(EntityEffect.HURT);
    

    player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_HURT, 2.0F, 1.5F);
  }
  
  public void Release(Player player)
  {
    LivingEntity ent = (LivingEntity)this._clutch.remove(player);
    if (ent == null) { return;
    }
    player.leaveVehicle();
    ent.eject();
    

    this.Host.Factory.Condition().SetIndicatorVisibility(ent, true);
    

    UtilPlayer.message(player, F.main(this.Host.GetClassType().name(), "You released " + F.name(UtilEnt.getName(ent)) + "."));
    UtilPlayer.message(ent, F.main(this.Host.GetClassType().name(), F.name(player.getName()) + " released you."));
    

    ent.playEffect(EntityEffect.HURT);
  }
  
  public void DamageRelease(CustomDamageEvent event)
  {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    Release(damagee);
  }
  
  public void Reset(Player player)
  {
    LivingEntity ent = (LivingEntity)this._clutch.remove(player);
    if (ent != null)
    {
      ent.eject();
      this.Host.Factory.Condition().SetIndicatorVisibility(ent, true);
    }
  }
}
