package mineplex.minecraft.game.classcombat.shop.button;

import org.bukkit.entity.Player;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.shop.page.SkillPage;

public class SelectSkillButton implements IButton
{
	private SkillPage _page;
	private ISkill _skill;
	
	public SelectSkillButton(SkillPage page, ISkill skill)
	{
		_page = page;
		_skill = skill;
	}
	
	@Override
	public void Clicked(Player player)
	{
		_page.SelectSkill(player, _skill);
	}
}
