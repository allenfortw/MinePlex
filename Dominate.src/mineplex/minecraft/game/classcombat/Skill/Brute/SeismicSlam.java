package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class SeismicSlam extends SkillActive
{
  private HashMap<LivingEntity, Long> _live = new HashMap();
  private HashMap<LivingEntity, Double> _height = new HashMap();
  










  public SeismicSlam(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Jump up and slam back into the ground.", 
      "Players within 6 Blocks take up to", 
      "6 damage and are thrown into the air.", 
      "", 
      "You receive Slow 2 for 6 seconds." });
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
    Vector vec = player.getLocation().getDirection();
    if (vec.getY() < 0.0D) {
      vec.setY(vec.getY() * -1.0D);
    }
    UtilAction.velocity(player, vec, 0.6D, false, 0.0D, 0.6D, 0.6D, true);
    

    this._live.put(player, Long.valueOf(System.currentTimeMillis()));
    this._height.put(player, Double.valueOf(player.getLocation().getY()));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @EventHandler
  public void Slam(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
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

              this._live.remove(player);
              

              double mult = 1.0D;
              if (this._height.containsKey(player))
              {
                mult += (((Double)this._height.remove(player)).doubleValue() - player.getLocation().getY()) / 20.0D;
                mult = Math.min(mult, 2.0D);
                UtilPlayer.message(player, F.main(GetClassType().name(), 
                  GetName() + ": " + F.elem(new StringBuilder(String.valueOf((int)(mult * 100.0D))).append("% Effectiveness").toString())));
              }
              


              int damage = 6;
              double range = 6.0D * mult;
              HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(player.getLocation(), range);
              for (LivingEntity cur : targets.keySet())
              {
                if (!cur.equals(player))
                {

                  if (UtilEnt.isGrounded(player))
                  {


                    this.Factory.Damage().NewDamageEvent(cur, player, null, 
                      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, damage * ((Double)targets.get(cur)).doubleValue() + 0.5D, false, true, false, 
                      player.getName(), GetName());
                    

                    UtilAction.velocity(cur, 
                      mineplex.core.common.util.UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector()), 
                      1.8D * ((Double)targets.get(cur)).doubleValue() * mult, true, 0.0D, 0.4D + 1.0D * ((Double)targets.get(cur)).doubleValue() * mult, 1.6D * mult, true);
                    

                    this.Factory.Condition().Factory().Falling(GetName(), cur, player, 10.0D, false, true);
                    

                    if ((cur instanceof Player))
                      UtilPlayer.message((Player)cur, F.main(GetClassType().name(), F.name(player.getName()) + " hit you with " + F.skill(GetName(level)) + "."));
                  }
                }
              }
              this.Factory.Condition().Factory().Slow(GetName(), player, player, 6.0D, 1, false, true, false, true);
              

              player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ZOMBIE_WOOD, 2.0F, 0.2F);
              for (Block cur : UtilBlock.getInRadius(player.getLocation(), Double.valueOf(4.0D)).keySet()) {
                if ((UtilBlock.airFoliage(cur.getRelative(BlockFace.UP))) && (!UtilBlock.airFoliage(cur))) {
                  cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, cur.getTypeId());
                }
              }
              UtilServer.getServer().getPluginManager().callEvent(new mineplex.minecraft.game.classcombat.Skill.event.SkillEvent(player, GetName(), IPvpClass.ClassType.Brute, targets.keySet()));
            } }
        } }
    }
  }
  
  public void Reset(Player player) {
    this._live.remove(player);
    this._height.remove(player);
  }
}
