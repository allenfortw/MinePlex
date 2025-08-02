package mineplex.minecraft.game.classcombat.shop.button;

import org.bukkit.entity.Player;

import mineplex.core.shop.item.IButton;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.shop.page.CustomBuildPage;

public class EditAndDontSaveCustomBuildButton implements IButton
{
	private CustomBuildPage _page;
	private CustomBuildToken _customBuild;
	
	public EditAndDontSaveCustomBuildButton(CustomBuildPage page, CustomBuildToken customBuild)
	{
		_page = page;
		_customBuild = customBuild;
	}

	@Override
	public void Clicked(Player player)
	{
		_page.EditAndDontSaveCustomBuild(_customBuild);
	}
}
