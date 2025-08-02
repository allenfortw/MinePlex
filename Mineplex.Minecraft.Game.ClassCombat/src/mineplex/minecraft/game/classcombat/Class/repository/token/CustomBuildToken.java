package mineplex.minecraft.game.classcombat.Class.repository.token;

import java.util.List;

public class CustomBuildToken 
{	
    public int CustomBuildId;

    public String PlayerName;
    public String Name;
    public boolean Active;
    
    public Integer CustomBuildNumber = 0;
    
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
    	System.out.println("CustomBuildId : " + CustomBuildId);
    	System.out.println("PlayerName : " + PlayerName);
    	System.out.println("Name : " + Name);
    	System.out.println("Active : " + Active);
        
    	System.out.println("CustomBuildNumber : " + CustomBuildNumber);
        
    	System.out.println("PvpClassId : " + PvpClassId);

    	System.out.println("SwordSkillId : " + SwordSkillId);

    	System.out.println("AxeSkillId : " + AxeSkillId);

    	System.out.println("BowSkillId : " + BowSkillId);

    	System.out.println("ClassPassiveASkillId : " + ClassPassiveASkillId);
    	System.out.println("ClassPassiveBSkillId : " + ClassPassiveBSkillId);

    	System.out.println("GlobalPassiveSkillId : " + GlobalPassiveSkillId);
    	
    	for (SlotToken token : Slots)
    	{
    		token.printInfo();
    	}
    }
}
