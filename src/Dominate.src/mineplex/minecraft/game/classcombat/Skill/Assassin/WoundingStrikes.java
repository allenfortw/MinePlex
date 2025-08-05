package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilGear;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class WoundingStrikes extends Skill
{
  private NautHashMap<LivingEntity, NautHashMap<Player, ArrayList<Integer>>> _active = new NautHashMap();
  private NautHashMap<LivingEntity, NautHashMap<Player, Long>> _last = new NautHashMap();
  

  public WoundingStrikes(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Your axe attacks deal 4 less damage, but", 
      "do an addition 1 damage per second for", 
      "6 seconds. Damage over time can stack", 
      "up to 3 times." });
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
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    int level = GetLevel(damager);
    if (level == 0) { return;
    }
    if (!UtilGear.isAxe(damager.getItemInHand())) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    
    event.AddMod(damager.getName(), GetName(), -4.0D, true);
    

    int ticks = 6;
    
    if (!this._active.containsKey(damagee)) {
      this._active.put(damagee, new NautHashMap());
    }
    if (!((NautHashMap)this._active.get(damagee)).containsKey(damager)) {
      ((NautHashMap)this._active.get(damagee)).put(damager, new ArrayList());
    }
    ((ArrayList)((NautHashMap)this._active.get(damagee)).get(damager)).add(Integer.valueOf(ticks));
    
    while (((ArrayList)((NautHashMap)this._active.get(damagee)).get(damager)).size() > 3) {
      ((ArrayList)((NautHashMap)this._active.get(damagee)).get(damager)).remove(0);
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Map.Entry<LivingEntity, NautHashMap<Player, ArrayList<Integer>>>> damageeIterator = this._active.entrySet().iterator();
    Iterator<Map.Entry<Player, ArrayList<Integer>>> damagerIterator;
    for (; damageeIterator.hasNext(); 
        










        damagerIterator.hasNext())
    {
      Map.Entry<LivingEntity, NautHashMap<Player, ArrayList<Integer>>> damageeEntrySet = (Map.Entry)damageeIterator.next();
      LivingEntity damagee = (LivingEntity)damageeEntrySet.getKey();
      NautHashMap<Player, ArrayList<Integer>> damagers = (NautHashMap)damageeEntrySet.getValue();
      

      if (!this._last.containsKey(damagee)) {
        this._last.put(damagee, new NautHashMap());
      }
      damagerIterator = damagers.entrySet().iterator();
      
      continue;
      
      Map.Entry<Player, ArrayList<Integer>> damagerEntrySet = (Map.Entry)damagerIterator.next();
      Player damager = (Player)damagerEntrySet.getKey();
      ArrayList<Integer> damagerTicks = (ArrayList)damagerEntrySet.getValue();
      

      if (!((NautHashMap)this._last.get(damagee)).containsKey(damager))
      {
        ((NautHashMap)this._last.get(damagee)).put(damager, Long.valueOf(System.currentTimeMillis()));



      }
      else if (mineplex.core.common.util.UtilTime.elapsed(((Long)((NautHashMap)this._last.get(damagee)).get(damager)).longValue(), 1000L))
      {

        ((NautHashMap)this._last.get(damagee)).put(damager, Long.valueOf(System.currentTimeMillis()));
        

        while ((!damagerTicks.isEmpty()) && (((Integer)damagerTicks.get(0)).intValue() <= 0))
        {
          damagerTicks.remove(0);
        }
        

        for (int i = 0; i < damagerTicks.size(); i++)
        {
          int ticks = ((Integer)damagerTicks.get(i)).intValue();
          damagerTicks.set(i, Integer.valueOf(ticks - 1));
        }
        

        int stacks = damagerTicks.size();
        
        if (stacks == 0)
        {
          damagerIterator.remove();
          
          if (damagers.isEmpty())
          {
            damageeIterator.remove();
          }
        }
        else
        {
          this.Factory.Damage().NewDamageEvent(damagee, damager, null, EntityDamageEvent.DamageCause.CUSTOM, stacks, false, true, false, null, null);
        }
      }
    }
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
    this._last.remove(player);
  }
}
