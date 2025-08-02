package mineplex.minecraft.game.classcombat.Class;

import mineplex.minecraft.game.classcombat.Skill.ISkill;
import org.bukkit.Material;

public abstract interface IPvpClass
{
  public abstract int GetSalesPackageId();
  
  public abstract String GetName();
  
  public abstract ClassType GetType();
  
  public static enum ClassType
  {
    Global, 
    Knight, 
    Ranger, 
    Assassin, 
    Mage, 
    Brute, 
    Shifter;
  }
  
  public abstract Material GetHead();
  
  public abstract Material GetChestplate();
  
  public abstract Material GetLeggings();
  
  public abstract Material GetBoots();
  
  public abstract java.util.HashSet<ISkill> GetSkills();
  
  public abstract java.util.HashMap<ISkill, Integer> GetDefaultSkills();
  
  public abstract void checkEquip();
  
  public abstract Integer GetCost();
  
  public abstract boolean IsFree();
  
  public abstract void Update(mineplex.minecraft.game.classcombat.Class.repository.token.ClassToken paramClassToken);
  
  public abstract String[] GetDesc();
  
  public abstract void ApplyArmor(org.bukkit.entity.Player paramPlayer);
}
