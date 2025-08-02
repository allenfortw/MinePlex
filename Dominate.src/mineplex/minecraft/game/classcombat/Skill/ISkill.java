package mineplex.minecraft.game.classcombat.Skill;

import org.bukkit.entity.Player;

public abstract interface ISkill
{
  public abstract String GetName();
  
  public abstract mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType GetClassType();
  
  public static enum SkillType
  {
    Axe, 
    Bow, 
    Sword, 
    PassiveA, 
    PassiveB, 
    GlobalPassive, 
    Class;
  }
  
  public abstract SkillType GetSkillType();
  
  public abstract int GetCost();
  
  public abstract boolean IsFree();
  
  public abstract String[] GetDesc();
  
  public abstract void Reset(Player paramPlayer);
  
  public abstract java.util.List<Player> GetUsers();
  
  public abstract void AddUser(Player paramPlayer);
  
  public abstract void RemoveUser(Player paramPlayer);
  
  public abstract Integer GetSalesPackageId();
}
