package nautilus.game.arcade.game.games.wizards;

import org.bukkit.entity.Player;

import mineplex.core.account.CoreClientManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import nautilus.game.arcade.ArcadeManager;

public class WizardSpellMenuShop extends ShopBase<WizardSpellMenu>
{
	private Wizards _wizards;

	public WizardSpellMenuShop(WizardSpellMenu plugin, CoreClientManager clientManager,
			ArcadeManager arcadeManager, Wizards wizards)
	{
		super(plugin, clientManager, "Kit Evolve Menu");
		
		_wizards = wizards;
	}

	@Override
	protected ShopPageBase<WizardSpellMenu, ? extends ShopBase<WizardSpellMenu>> buildPagesFor(Player player)
	{
		return new SpellMenuPage(getPlugin(), this, getClientManager(), player, _wizards);
	}

	public void update()
	{
		for (ShopPageBase<WizardSpellMenu, ? extends ShopBase<WizardSpellMenu>> shopPage : getPlayerPageMap().values())
		{
			shopPage.refresh();
		}
	}
}
