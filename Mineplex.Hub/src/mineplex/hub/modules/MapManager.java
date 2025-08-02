package mineplex.hub.modules;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import mineplex.core.MiniPlugin;
import mineplex.core.map.Map;
import mineplex.core.common.Rank;
import mineplex.core.common.util.UtilGear;
import mineplex.hub.HubManager;

public class MapManager extends MiniPlugin
{
	private HubManager Manager;
	private Map Map;
	
	public MapManager(HubManager manager) 
	{
		super("Map Manager", manager.GetPlugin());
		Map = new Map(manager.GetPlugin());
		Manager = manager;
	}

	//@EventHandler
	public void PlayerJoin(PlayerJoinEvent event)
	{
		Map.SetDefaultUrl("http://chivebox.com/img/mc/news.png");
		event.getPlayer().setItemInHand(Map.GetMap());
	}

	@EventHandler
	public void FrameInteract(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof ItemFrame))
			return;

		if (!Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.OWNER))
		{
			event.setCancelled(true);
			return;
		}

		if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.DIAMOND_AXE))
			return;

		event.getRightClicked().getWorld().playEffect(event.getRightClicked().getLocation(), Effect.STEP_SOUND, 5);
		event.getRightClicked().remove();
	}
}
