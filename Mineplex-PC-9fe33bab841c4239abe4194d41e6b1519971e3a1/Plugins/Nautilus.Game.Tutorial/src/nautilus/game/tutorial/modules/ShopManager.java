package nautilus.game.tutorial.modules;

import me.chiss.Core.Module.AModule;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.DonatorShop;
import me.chiss.Core.Shop.GameShop;
import me.chiss.Core.Weapon.WeaponFactory;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.server.RemoteRepository;
import mineplex.minecraft.game.core.classcombat.item.ItemFactory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopManager extends AModule
{
	private DonatorShop _donatorShop;
	private GameShop _buildShop;
	
	public ShopManager(JavaPlugin plugin, RemoteRepository _repository) 
	{
		super("Shop Manager", plugin);

		_donatorShop = new DonatorShop((IPlugin)plugin, _repository, Clients(), Classes(), Skills(), WeaponFactory.Instance, ItemFactory.Instance);
		_buildShop = new GameShop((IPlugin)plugin, _repository, Clients(), Classes(), Skills(), WeaponFactory.Instance, ItemFactory.Instance);
	}

	@Override
	public void enable() 
	{
 
	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config()
	{

	}

	@Override
	public void commands() 
	{

	}

	@Override
	public void command(Player caller, String cmd, String[] args)
	{

	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		if (!Util().Event().isAction(event, ActionType.R_BLOCK))
			return;

		if (_donatorShop.ShouldOpenShop(event.getClickedBlock()))
		{
			_donatorShop.OpenShopForPlayer(player);
			event.setCancelled(true);
		}
		
		if (_buildShop.ShouldOpenShop(event.getClickedBlock()))
		{
			_buildShop.OpenShopForPlayer(player);
			event.setCancelled(true);
		}
	}
}
