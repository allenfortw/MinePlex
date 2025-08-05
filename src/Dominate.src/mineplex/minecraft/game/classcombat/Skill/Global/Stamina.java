package mineplex.minecraft.game.classcombat.Skill.Global;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;

public class Stamina extends Skill
{
  public Stamina(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "You have exceptional Stamina;", 
      "Swinging weapons uses 75% less Energy." });
  }
  


  public void OnPlayerAdd(Player player)
  {
    this.Factory.Energy().AddEnergySwingMod(player, GetName(), -3);
  }
  

  public void Reset(Player player)
  {
    this.Factory.Energy().RemoveEnergySwingMod(player, GetName());
  }
}
