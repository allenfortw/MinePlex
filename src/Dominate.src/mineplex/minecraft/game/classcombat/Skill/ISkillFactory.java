package mineplex.minecraft.game.classcombat.Skill;

import java.util.HashMap;
import java.util.List;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;

public abstract interface ISkillFactory
{
  public abstract List<ISkill> GetGlobalSkills();
  
  public abstract List<ISkill> GetSkillsFor(IPvpClass paramIPvpClass);
  
  public abstract HashMap<ISkill, Integer> GetDefaultSkillsFor(IPvpClass paramIPvpClass);
}
