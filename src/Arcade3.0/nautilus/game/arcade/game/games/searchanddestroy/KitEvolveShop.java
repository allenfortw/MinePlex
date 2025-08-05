package nautilus.game.arcade.game.games.searchanddestroy;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import nautilus.game.arcade.game.games.searchanddestroy.KitManager.UpgradeKit;

public class KitEvolveShop extends ShopBase<KitEvolve>
{

    private SearchAndDestroy _arcadeManager;
    private ArrayList<UpgradeKit> _kits;

    public KitEvolveShop(KitEvolve plugin, SearchAndDestroy arcadeManager, CoreClientManager clientManager, ArrayList<UpgradeKit> kits, CurrencyType... currencyTypes)
    {
        super(plugin, clientManager, "Kit Evolve Menu", currencyTypes);
        _arcadeManager = arcadeManager;
        _kits = kits;
    }

    @Override
    protected ShopPageBase<KitEvolve, ? extends ShopBase<KitEvolve>> buildPagesFor(Player player)
    {
        return new KitEvolvePage(getPlugin(), _arcadeManager, this, getClientManager(), player, _kits);
    }

    public void update()
    {
        for (ShopPageBase<KitEvolve, ? extends ShopBase<KitEvolve>> shopPage : getPlayerPageMap().values())
        {
            shopPage.refresh();
        }
    }
}
