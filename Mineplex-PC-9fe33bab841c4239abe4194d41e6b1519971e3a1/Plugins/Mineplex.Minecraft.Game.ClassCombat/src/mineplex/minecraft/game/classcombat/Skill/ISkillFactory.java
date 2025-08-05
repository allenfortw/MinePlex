package mineplex.minecraft.game.classcombat.Skill;

import java.util.HashMap;
import java.util.List;

import mineplex.minecraft.game.classcombat.Class.IPvpClass;

public interface ISkillFactory
{
    List<ISkill> GetGlobalSkills();
    List<ISkill> GetSkillsFor(IPvpClass gameClass);
    HashMap<ISkill, Integer> GetDefaultSkillsFor(IPvpClass gameClass);
}
