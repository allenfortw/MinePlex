package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

public class Rampage extends SkillActive
{
  private WeakHashMap<Player, Long> _rampageStart = new WeakHashMap();
  private WeakHashMap<Player, Long> _rampageCharge = new WeakHashMap();
  private WeakHashMap<Player, Integer> _rampageBonus = new WeakHashMap();
  










  public Rampage(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Go into a rampage", 
      "* Slow I for 6 + 2pL seconds", 
      "* +1 Damage per 2 seconds you've been in Rampage", 
      "Rampage ends;", 
      "* If you don't attack/get attacked for 2 + 1pL seconds", 
      "* After 6 + 2pL seconds" });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    this._rampageStart.put(player, Long.valueOf(System.currentTimeMillis()));
    this._rampageCharge.put(player, Long.valueOf(System.currentTimeMillis()));
    this._rampageBonus.put(player, Integer.valueOf(0));
    

    this.Factory.Condition().Factory().Slow(GetName(), player, player, 6 + level * 2, 0, false, true, false, true);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2.0F, 0.5F);
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this._rampageBonus.containsKey(damager)) {
      return;
    }
    org.bukkit.entity.LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    event.AddMod(damager.getName(), GetName(), ((Integer)this._rampageBonus.get(damager)).intValue(), true);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._rampageStart.keySet())
    {
      int level = GetLevel(cur);
      if (level == 0)
      {
        remove.add(cur);

      }
      else
      {
        if (UtilTime.elapsed(this.Factory.Combat().Get(cur).LastCombat, 2000 + 1000 * level))
        {
          if ((this._rampageStart.get(cur) != null) && (UtilTime.elapsed(((Long)this._rampageStart.get(cur)).longValue(), 2000 + 1000 * level)))
          {
            remove.add(cur);
            continue;
          }
        }
        

        if (UtilTime.elapsed(((Long)this._rampageStart.get(cur)).longValue(), 6000 + level * 2000))
        {
          remove.add(cur);



        }
        else if (UtilTime.elapsed(((Long)this._rampageCharge.get(cur)).longValue(), 2000L))
        {
          this._rampageCharge.put(cur, Long.valueOf(System.currentTimeMillis()));
          this._rampageBonus.put(cur, Integer.valueOf(((Integer)this._rampageBonus.get(cur)).intValue() + 1));
          UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.elem(new StringBuilder("+").append(this._rampageBonus.get(cur)).append(" Damage").toString())));
          

          cur.getWorld().playSound(cur.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 0.5F, 0.5F + ((Integer)this._rampageBonus.get(cur)).intValue() * 0.1F);
        }
      }
    }
    for (Player cur : remove)
    {
      this._rampageStart.remove(cur);
      this._rampageCharge.remove(cur);
      this._rampageBonus.remove(cur);
      

      UtilPlayer.message(cur, F.main(GetClassType().name(), "Your " + F.skill(GetName()) + " has ended."));
    }
  }
  

  public void Reset(Player player)
  {
    this._rampageStart.remove(player);
    this._rampageCharge.remove(player);
    this._rampageBonus.remove(player);
  }
}
