package nautilus.game.lobby.ServerMenu;

import me.chiss.Core.Shop.salespackage.ShopItem;
import me.chiss.Core.Shopv2.page.ShopPageBase;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerMenuPage extends ShopPageBase<ServerInfoManager, ServerMenu>
{
	public ServerMenuPage(ServerInfoManager plugin, ServerMenu shop, Player player)
	{
		super(plugin, shop, "Server Info", player);
		
		BuildPage();
	}

	@Override
	protected void BuildPage()
	{
		UpdateServerCounts();
		
		getInventory().setItem(28, new ShopItem(Material.BOOK, (byte)0, "Dominate Information", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "KitPvp:", ChatColor.RESET + "" + ChatColor.WHITE + "Capture and hold control points", ChatColor.RESET + "" + ChatColor.WHITE + "Lead your team to victory!" }, 1, false, true).getHandle());
		getInventory().setItem(30, new ShopItem(Material.BOOK, (byte)0, "Survival PvP Information", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "KitPvp:", ChatColor.RESET + "" + ChatColor.WHITE + "Build your base", ChatColor.RESET + "" + ChatColor.WHITE + "Fight unique bosses", ChatColor.RESET + "" + ChatColor.WHITE + "Become top clan!" }, 1, false, true).getHandle());
		getInventory().setItem(32, new ShopItem(Material.BOOK, (byte)0, "MineKart 64 Information", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Mario Kart 64 Racing", ChatColor.RESET + "" + ChatColor.WHITE + "Nine different racing karts", ChatColor.RESET + "" + ChatColor.WHITE + "Three Race Cups and Battle Mode!" }, 1, false, true).getHandle());
		getInventory().setItem(34, new ShopItem(Material.BOOK, (byte)0, "Tutorial Information", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Learn about KitPvp and Dominate" }, 1, false, true).getHandle());
	}
	
	public void UpdateServerCounts()
	{
		AddButton(19, new ShopItem(Material.IRON_SWORD, (byte)0, "Dominate " + ChatColor.WHITE + "(" + ChatColor.YELLOW + Plugin.GetDomPlayerCount() + "/100" + ChatColor.WHITE + ")", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Click to join!" }, 1, false, true), new ServerConnectButton(Shop, "dom"));
		AddButton(21, new ShopItem(Material.GRASS, (byte)0, "Survival PvP " + ChatColor.WHITE + "(" + ChatColor.YELLOW + Plugin.GetPvpPlayerCount() + "/100" + ChatColor.WHITE + ")", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Click to join!" }, 1, false, true), new ServerConnectButton(Shop, "pvp"));
		AddButton(23, new ShopItem(Material.WATCH, (byte)0, "MineKart 64 " + ChatColor.WHITE + "(" + ChatColor.YELLOW + Plugin.GetMK64PlayerCount() + "/100" + ChatColor.WHITE + ")", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Click to join!" }, 1, false, true), new ServerConnectButton(Shop, "mk64"));
		AddButton(25, new ShopItem(Material.BOOK_AND_QUILL, (byte)0, "Tutorial " + ChatColor.WHITE + "(" + ChatColor.YELLOW + Plugin.GetTutPlayerCount() + "/100" + ChatColor.WHITE + ")", new String[] { ChatColor.RESET + "" + ChatColor.WHITE + "Click to join!" }, 1, false, true), new ServerConnectButton(Shop, "tut"));
	}
}
