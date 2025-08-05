package mineplex.minecraft.game.classcombat.shop.salespackage;

import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillSalesPackage
  extends SalesPackageBase
{
  public SkillSalesPackage(ISkill skill)
  {
    super(skill.GetName(), Material.BOOK, skill.GetDesc());
    this.SalesPackageId = skill.GetSalesPackageId().intValue();
    this.Free = skill.IsFree();
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
