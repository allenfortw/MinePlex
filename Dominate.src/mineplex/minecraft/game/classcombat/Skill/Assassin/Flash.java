package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.teleport.Teleport;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Flash extends SkillActive
{
  private HashMap<Player, Integer> _flash = new HashMap();
  










  public Flash(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Teleport forwards 6 Blocks.", 
      "Store up to 3 Flash Charges.", 
      "Cannot be used while Slowed." });
  }
  


  public String GetRechargeString()
  {
    return "Recharge: 8 Seconds per Charge";
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    if (player.hasPotionEffect(org.bukkit.potion.PotionEffectType.SLOW))
    {
      UtilPlayer.message(player, F.main(GetClassType().name(), "You cannot use " + F.skill(GetName()) + " while Slowed."));
      return false;
    }
    
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    

    if ((!this._flash.containsKey(player)) || (((Integer)this._flash.get(player)).intValue() == 0))
    {
      UtilPlayer.message(player, F.main("Skill", "You have no " + F.skill(new StringBuilder(String.valueOf(GetName())).append(" Charges").toString()) + "."));
      return false;
    }
    
    return true;
  }
  
  @EventHandler
  public void Recharge(UpdateEvent event)
  {
    for (Player cur : GetUsers()) {
      if (!this._flash.containsKey(cur))
      {
        this._flash.put(cur, Integer.valueOf(0));
      }
      else
      {
        int charges = ((Integer)this._flash.get(cur)).intValue();
        
        if (charges < 3)
        {

          if (Recharge.Instance.use(cur, "Flash Recharge", 8000L, false))
          {

            this._flash.put(cur, Integer.valueOf(charges + 1));
            

            UtilPlayer.message(cur, F.main(GetClassType().name(), "Flash Charges: " + F.elem(new StringBuilder(String.valueOf(charges + 1)).toString())));
          }
        }
      }
    }
  }
  
  public void Skill(Player player, int level) {
    Recharge.Instance.use(player, "Flash Recharge", 8000L, false);
    
    this._flash.put(player, Integer.valueOf(((Integer)this._flash.get(player)).intValue() - 1));
    

    Block lastSmoke = player.getLocation().getBlock();
    
    double maxRange = 6.0D;
    double curRange = 0.0D;
    while (curRange <= maxRange)
    {
      Location newTarget = player.getLocation().add(new Vector(0.0D, 0.2D, 0.0D)).add(player.getLocation().getDirection().multiply(curRange));
      
      if ((!UtilBlock.airFoliage(newTarget.getBlock())) || 
        (!UtilBlock.airFoliage(newTarget.getBlock().getRelative(org.bukkit.block.BlockFace.UP)))) {
        break;
      }
      
      curRange += 0.2D;
      

      if (!lastSmoke.equals(newTarget.getBlock())) {
        lastSmoke.getWorld().playEffect(lastSmoke.getLocation(), Effect.SMOKE, 4);
      }
      lastSmoke = newTarget.getBlock();
    }
    

    curRange -= 0.4D;
    if (curRange < 0.0D) {
      curRange = 0.0D;
    }
    
    Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0.0D, 0.4D, 0.0D)));
    
    if (curRange > 0.0D) {
      this.Factory.Teleport().TP(player, loc);
    }
    player.setFallDistance(0.0F);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "Flash Charges: " + F.elem(new StringBuilder().append(this._flash.get(player)).toString())));
    

    player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 0.4F, 1.2F);
    player.getWorld().playSound(player.getLocation(), Sound.SILVERFISH_KILL, 1.0F, 1.6F);
  }
  


  public void Reset(Player player)
  {
    this._flash.remove(player);
  }
}
