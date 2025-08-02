package mineplex.minecraft.game.classcombat.Class.repository.token;

import java.io.PrintStream;
import java.util.List;


public class CustomBuildToken
{
  public int CustomBuildId;
  public String PlayerName;
  public String Name;
  public boolean Active;
  public Integer CustomBuildNumber = Integer.valueOf(0);
  
  public int PvpClassId = -1;
  
  public int SwordSkillId = -1;
  
  public int AxeSkillId = -1;
  
  public int BowSkillId = -1;
  
  public int ClassPassiveASkillId = -1;
  public int ClassPassiveBSkillId = -1;
  
  public int GlobalPassiveSkillId = -1;
  
  public List<SlotToken> Slots;
  
  public void printInfo()
  {
    System.out.println("CustomBuildId : " + this.CustomBuildId);
    System.out.println("PlayerName : " + this.PlayerName);
    System.out.println("Name : " + this.Name);
    System.out.println("Active : " + this.Active);
    
    System.out.println("CustomBuildNumber : " + this.CustomBuildNumber);
    
    System.out.println("PvpClassId : " + this.PvpClassId);
    
    System.out.println("SwordSkillId : " + this.SwordSkillId);
    
    System.out.println("AxeSkillId : " + this.AxeSkillId);
    
    System.out.println("BowSkillId : " + this.BowSkillId);
    
    System.out.println("ClassPassiveASkillId : " + this.ClassPassiveASkillId);
    System.out.println("ClassPassiveBSkillId : " + this.ClassPassiveBSkillId);
    
    System.out.println("GlobalPassiveSkillId : " + this.GlobalPassiveSkillId);
    
    for (SlotToken token : this.Slots)
    {
      token.printInfo();
    }
  }
}
