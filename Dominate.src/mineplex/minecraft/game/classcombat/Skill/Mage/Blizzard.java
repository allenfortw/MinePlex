package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class Blizzard extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  private WeakHashMap<Projectile, Player> _snowball = new WeakHashMap();
  










  public Blizzard(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hold Block to release a Blizzard.", 
      "Target the ground to create snow,", 
      "or target air to create a torrent", 
      "of snowballs, to push players away." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 24 per Second";
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
    this._active.add(player);
  }
  
  @EventHandler
  public void Snow(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);

        }
        else
        {
          int level = GetLevel(cur);
          if (level == 0)
          {
            this._active.remove(cur);



          }
          else if (!this.Factory.Energy().Use(cur, GetName(), 4.0D, true, true))
          {
            this._active.remove(cur);

          }
          else
          {
            HashSet<Byte> ignore = new HashSet();
            ignore.add(Byte.valueOf((byte)0));
            ignore.add(Byte.valueOf((byte)78));
            ignore.add(Byte.valueOf((byte)80));
            
            Block target = cur.getTargetBlock(ignore, 7);
            
            double x;
            if ((target == null) || (target.getType() == Material.AIR) || (UtilMath.offset(target.getLocation(), cur.getLocation()) > 5.0D)) {
              for (int i = 0; i < 4; i++)
              {
                Projectile snow = cur.launchProjectile(Snowball.class);
                x = 0.2D - UtilMath.r(40) / 100.0D;
                double y = UtilMath.r(20) / 100.0D;
                double z = 0.2D - UtilMath.r(40) / 100.0D;
                snow.setVelocity(cur.getLocation().getDirection().add(new Vector(x, y, z)).multiply(2));
                this._snowball.put(snow, cur);
              }
            }
            if ((target != null) && (target.getType() != Material.AIR))
            {

              if (UtilMath.offset(target.getLocation(), cur.getLocation()) <= 7.0D)
              {

                HashMap<Block, Double> blocks = mineplex.core.common.util.UtilBlock.getInRadius(target.getLocation(), Double.valueOf(2.0D), 1.0D);
                for (Block block : blocks.keySet())
                {
                  this.Factory.BlockRestore().Snow(block, (byte)(1 + (int)(2.0D * ((Double)blocks.get(block)).doubleValue())), (byte)7, 2500L, 250L, 3);
                }
                

                target.getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, 80);
                cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.STEP_SNOW, 0.1F, 0.5F);
              } }
          }
        } } }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Snowball(CustomDamageEvent event) { if (event.GetCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile proj = event.GetProjectile();
    if (proj == null) { return;
    }
    if (!(proj instanceof Snowball)) {
      return;
    }
    if (!this._snowball.containsKey(proj)) {
      return;
    }
    LivingEntity damagee = event.GetDamageeEntity();
    if (damagee == null) { return;
    }
    event.SetCancelled(GetName());
    damagee.setVelocity(proj.getVelocity().multiply(0.1D).add(new Vector(0.0D, 0.15D, 0.0D)));
  }
  
  @EventHandler
  public void SnowballForm(ProjectileHitEvent event)
  {
    if (!(event.getEntity() instanceof Snowball)) {
      return;
    }
    if (this._snowball.remove(event.getEntity()) == null) {
      return;
    }
    this.Factory.BlockRestore().Snow(event.getEntity().getLocation().getBlock(), (byte)1, (byte)7, 2000L, 250L, 0);
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
