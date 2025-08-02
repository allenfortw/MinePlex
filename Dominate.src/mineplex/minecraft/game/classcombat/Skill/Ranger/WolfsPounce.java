package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.energy.Energy;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class WolfsPounce extends Skill
{
  private NautHashMap<Player, Integer> _charge = new NautHashMap();
  private NautHashMap<Player, Long> _chargeLast = new NautHashMap();
  private NautHashMap<Player, Long> _pounceTime = new NautHashMap();
  private NautHashMap<Player, Integer> _pounceCharge = new NautHashMap();
  

  public WolfsPounce(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Hold Block to charge pounce.", 
      "Release Block to pounce.", 
      "", 
      "Attacking while airborne tackles players,", 
      "giving Slow 3 for up to 5 seconds." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 10 per 20% Velocity";
  }
  
  @EventHandler
  public void Pounce(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {

      int level = GetLevel(cur);
      if (level != 0)
      {
        if (cur.isBlocking())
        {
          if (UtilEnt.isGrounded(cur))
          {

            if (!cur.getLocation().getBlock().isLiquid())
            {


              if (!this._charge.containsKey(cur))
              {
                if (this.Factory.Energy().Use(cur, GetName(level), 10.0D, false, false))
                {

                  SkillTriggerEvent triggerEvent = new SkillTriggerEvent(cur, GetName(), GetClassType());
                  UtilServer.getServer().getPluginManager().callEvent(triggerEvent);
                  
                  if (!triggerEvent.IsCancelled())
                  {

                    if (Recharge.Instance.use(cur, GetName(level), 500L, false))
                    {

                      this.Factory.Energy().Use(cur, GetName(level), 10.0D, true, false);
                      

                      this._charge.put(cur, Integer.valueOf(0));
                      this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
                      

                      UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem("20% Velocity")));
                    }
                  }
                }
              }
              else if (((Integer)this._charge.get(cur)).intValue() < 4)
              {


                if (UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 1000 - level * 180))
                {

                  if (this.Factory.Energy().Use(cur, GetName(level), 10.0D, true, false))
                  {


                    this._charge.put(cur, Integer.valueOf(((Integer)this._charge.get(cur)).intValue() + 1));
                    this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
                    

                    UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem(new StringBuilder(String.valueOf(((Integer)this._charge.get(cur)).intValue() * 20 + 20)).append("% Velocity").toString())));
                    

                    for (int i = ((Integer)this._charge.get(cur)).intValue(); i > 0; i--)
                      cur.playEffect(cur.getLocation(), org.bukkit.Effect.CLICK2, 0);
                  } } }
            }
          }
        } else if (this._charge.containsKey(cur))
        {

          int charge = ((Integer)this._charge.remove(cur)).intValue();
          mineplex.core.common.util.UtilAction.velocity(cur, 0.4D + 0.4D * charge, 0.2D, 0.6D + 0.1D * charge, true);
          this._chargeLast.remove(cur);
          this._pounceCharge.put(cur, Integer.valueOf(charge));
          this._pounceTime.put(cur, Long.valueOf(System.currentTimeMillis()));
          

          cur.getWorld().playSound(cur.getLocation(), Sound.WOLF_BARK, 1.0F, 1.2F + level * 0.2F);
        }
      }
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Hit(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    org.bukkit.entity.LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    if ((!this._pounceTime.containsKey(damager)) || (UtilTime.elapsed(((Long)this._pounceTime.get(damager)).longValue(), 250L))) {
      return;
    }
    int charge = ((Integer)this._pounceCharge.get(damager)).intValue();
    

    event.SetKnockback(false);
    event.AddMod(damager.getName(), GetName(), 0.0D, true);
    

    this.Factory.Condition().Factory().Slow(GetName(), damagee, damager, 1 + charge, 2, false, true, true, true);
    

    damager.getWorld().playSound(damager.getLocation(), Sound.WOLF_BARK, 0.5F, 0.5F);
    
    this._pounceTime.remove(damager);
  }
  
  @EventHandler
  public void Grounded(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> expired = new HashSet();
    
    for (Player cur : this._pounceTime.keySet()) {
      if ((UtilEnt.isGrounded(cur)) && 
        (UtilTime.elapsed(((Long)this._pounceTime.get(cur)).longValue(), 250L)))
        expired.add(cur);
    }
    for (Player cur : expired) {
      this._pounceTime.remove(cur);
    }
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._chargeLast.remove(player);
    this._pounceTime.remove(player);
    this._pounceCharge.remove(player);
  }
}
