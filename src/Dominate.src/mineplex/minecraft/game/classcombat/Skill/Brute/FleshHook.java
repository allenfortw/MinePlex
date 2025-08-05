package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;

public class FleshHook extends SkillActive implements mineplex.core.projectile.IThrown
{
  private WeakHashMap<Player, Integer> _charge = new WeakHashMap();
  private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap();
  










  public FleshHook(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hold Block to charge Flesh Hook.", 
      "Release Block to release it.", 
      "", 
      "If Flesh Hook hits a player, it", 
      "deals up to 12 damage, and rips them", 
      "towards you with high velocity." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 20 + (5 per 20% Strength)";
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
    this._charge.put(player, Integer.valueOf(0));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void ChargeRelease(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (this._charge.containsKey(cur))
      {


        int level = GetLevel(cur);
        if (level == 0) { return;
        }
        
        if (cur.isBlocking())
        {

          if (((Integer)this._charge.get(cur)).intValue() < 4)
          {


            if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 400L))
            {


              if (this.Factory.Energy().Use(cur, GetName(), 5.0D, true, false))
              {


                this._charge.put(cur, Integer.valueOf(((Integer)this._charge.get(cur)).intValue() + 1));
                this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
                

                UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem(new StringBuilder("+").append(((Integer)this._charge.get(cur)).intValue() * 25).append("% Strength").toString())));
                

                for (int i = ((Integer)this._charge.get(cur)).intValue(); i > 0; i--) {
                  cur.playEffect(cur.getLocation(), Effect.CLICK2, 0);
                }
              }
            }
          }
        } else {
          double base = 0.8D;
          

          Item item = cur.getWorld().dropItem(cur.getEyeLocation().add(cur.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(131));
          UtilAction.velocity(item, cur.getLocation().getDirection(), 
            base + ((Integer)this._charge.remove(cur)).intValue() * (0.25D * base), false, 0.0D, 0.2D, 20.0D, false);
          
          this.Factory.Projectile().AddThrow(item, cur, this, -1L, true, true, true, 
            Sound.FIRE_IGNITE, 1.4F, 0.8F, null, 0, UpdateType.TICK, 1.5D);
          

          UtilPlayer.message(cur, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
          

          item.getWorld().playSound(item.getLocation(), Sound.IRONGOLEM_THROW, 2.0F, 0.8F);
        }
      }
    }
  }
  

  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._chargeLast.remove(player);
  }
  


  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    double velocity = data.GetThrown().getVelocity().length();
    data.GetThrown().remove();
    
    if (!(data.GetThrower() instanceof Player)) {
      return;
    }
    Player player = (Player)data.GetThrower();
    

    int level = GetLevel(player);
    if (level == 0) { return;
    }
    if (target == null) {
      return;
    }
    
    UtilAction.velocity(target, 
      UtilAlg.getTrajectory(target.getLocation(), player.getLocation()), 
      2.0D, false, 0.0D, 0.8D, 1.5D, true);
    

    this.Factory.Condition().Factory().Falling(GetName(), target, player, 10.0D, false, true);
    

    this.Factory.Damage().NewDamageEvent(target, player, null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, velocity * 8.0D, false, true, false, 
      player.getName(), GetName());
    


    if (target != null) {
      UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(player, GetName(), IPvpClass.ClassType.Brute, target));
    }
    
    UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " pulled you with " + F.skill(GetName(level)) + "."));
  }
  


  public void Idle(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
  


  public void Expire(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
}
