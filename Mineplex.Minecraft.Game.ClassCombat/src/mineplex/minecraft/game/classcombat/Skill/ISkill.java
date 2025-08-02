package mineplex.minecraft.game.classcombat.Skill;

import java.util.List;

import org.bukkit.entity.Player;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;

public interface ISkill
{
	public enum SkillType
	{ 
		Axe,
		Bow,
		Sword,
		PassiveA,
		PassiveB,
		GlobalPassive,
		Class,
	}
	
    String GetName();
    ClassType GetClassType();
    SkillType GetSkillType();
    int GetCost();
    boolean IsFree();
    String[] GetDesc();
    void Reset(Player player);
    
    List<Player> GetUsers();
    void AddUser(Player player);
    void RemoveUser(Player player);
    
	Integer GetSalesPackageId();
	
}
