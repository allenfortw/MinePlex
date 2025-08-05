package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;




public class FireBlast
  extends SkillActive
{
  public FireBlast(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Launch an explosive fireball;", 
      "Explosion gives large knockback", 
      "and ignites enemies for 8 seconds.", 
      "", 
      "Effects scale down with distance", 
      "from explosion." });
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
    LargeFireball ball = (LargeFireball)player.launchProjectile(LargeFireball.class);
    ball.setShooter(player);
    ball.setIsIncendiary(false);
    ball.setYield(0.0F);
    ball.setBounce(false);
    ball.teleport(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)));
    ball.setVelocity(new Vector(0, 0, 0));
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, 0.8F);
  }
  
  @EventHandler
  public void Collide(ProjectileHitEvent event)
  {
    Projectile proj = event.getEntity();
    
    if (!(proj instanceof LargeFireball)) {
      return;
    }
    if (proj.getShooter() == null) {
      return;
    }
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }
    Player player = (Player)proj.getShooter();
    

    int level = GetLevel(player);
    if (level == 0) { return;
    }
    
    HashMap<Player, Double> hitMap = UtilPlayer.getInRadius(proj.getLocation(), 8.0D);
    for (Player cur : hitMap.keySet())
    {
      double range = ((Double)hitMap.get(cur)).doubleValue();
      

      this.Factory.Condition().Factory().Ignite(GetName(), cur, player, 2.0D + 6.0D * range, false, false);
      

      UtilAction.velocity(cur, UtilAlg.getTrajectory(proj.getLocation().add(0.0D, -0.5D, 0.0D), cur.getEyeLocation()), 
        0.5D + 1.5D * range, false, 0.0D, 0.2D + 0.4D * range, 1.2D, true);
    }
    

    for (int i = 0; i < 60; i++)
    {
      Item fire = player.getWorld().dropItem(proj.getLocation().add(0.0D, 0.5D, 0.0D), ItemStackFactory.Instance.CreateStack(Material.FIRE));
      fire.setVelocity(new Vector((Math.random() - 0.5D) / 2.0D, Math.random() / 2.0D + 0.5D, (Math.random() - 0.5D) / 2.0D));
      this.Factory.Fire().Add(fire, player, 8.0D, 2.0D, 3.0D, 0, GetName());
    }
  }
  
  public void Reset(Player player) {}
}
