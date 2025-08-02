package mineplex.minecraft.game.classcombat.shop.salespackage;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.minecraft.game.classcombat.Skill.ISkill;

public class SkillSalesPackage extends SalesPackageBase
{
	public SkillSalesPackage(ISkill skill)
	{
		super(skill.GetName(), Material.BOOK, skill.GetDesc());
		SalesPackageId = skill.GetSalesPackageId();
		Free = skill.IsFree();
	}

	@Override
	public void Sold(Player player, CurrencyType currencyType)
	{
		
	}
}
