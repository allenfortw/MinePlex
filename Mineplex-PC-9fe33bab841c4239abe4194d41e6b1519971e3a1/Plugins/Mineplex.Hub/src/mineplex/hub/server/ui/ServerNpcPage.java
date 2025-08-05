package mineplex.hub.server.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerInfo;
import mineplex.hub.server.ServerManager;
import mineplex.hub.server.ServerSorter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerNpcPage extends ShopPageBase<ServerManager, ServerNpcShop>
{
	private String _serverNpcKey;
	
	public ServerNpcPage(ServerManager plugin, ServerNpcShop shop, CoreClientManager clientManager,	DonationManager donationManager, String name, Player player, String serverNpcKey)
	{
		super(plugin, shop, clientManager, donationManager, name, player, 54);
		
		_serverNpcKey = serverNpcKey;
		
		BuildPage();
	}

	@Override
	protected void BuildPage()
	{
		List<ServerInfo> serverList = Plugin.GetServerList(_serverNpcKey);		
		Collections.sort(serverList, new ServerSorter());
		
		int slot = 9;
		int greenCount = 0;
		int yellowCount = 0;
		
		for (ServerInfo serverInfo : serverList)
		{
			Material status = Material.REDSTONE_BLOCK;
			List<String> lore = new ArrayList<String>();
			
			if (slot >= 53)
				break;
			
			if ((serverInfo.MOTD.contains("Recruiting") || serverInfo.MOTD.contains("Waiting") || serverInfo.MOTD.contains("Cup")) && slot < 15)
			{
				slot += 2;
				status = Material.EMERALD_BLOCK;
				lore.add(ChatColor.RESET + serverInfo.MOTD);
				lore.add(ChatColor.RESET + "" + serverInfo.CurrentPlayers + "/" + serverInfo.MaxPlayers);
				lore.add(ChatColor.RESET + "");
				
				if (serverInfo.CurrentPlayers >= serverInfo.MaxPlayers)
				{
					if (!Client.GetRank().Has(Rank.ULTRA))
						lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "Get Ultra to join full servers!");
					else
						lore.add(ChatColor.RESET + "" + ChatColor.GREEN + "Click to join!");
				}
				else
					lore.add(ChatColor.RESET + "" + ChatColor.GREEN + "Click to join!");
				
				greenCount++;
			}
			else if (serverInfo.MOTD.contains("In") || serverInfo.MOTD.contains("Restarting") || serverInfo.MOTD.contains("Starting"))
			{
				if (slot <= 15)
					slot = 27;
				else
					slot++;
					
				status = Material.GOLD_BLOCK;
				lore.add(ChatColor.RESET + serverInfo.MOTD);
				lore.add(ChatColor.RESET + "" + serverInfo.CurrentPlayers + "/" + serverInfo.MaxPlayers);
				lore.add(ChatColor.RESET + "");
				
				if (serverInfo.MOTD.contains("Restarting"))
				{
					lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "Get Ultra to spectate full servers!");
					status = Material.IRON_BLOCK;
				}
				else if (serverInfo.CurrentPlayers >= serverInfo.MaxPlayers)
				{
					if (!Client.GetRank().Has(Rank.ULTRA))
						lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "Get Ultra to spectate full servers!");
					else
						lore.add(ChatColor.RESET + "" + ChatColor.GREEN + "Click to spectate!");
				}
				else
					lore.add(ChatColor.RESET + "" + ChatColor.GREEN + "Click to spectate!");
				
				yellowCount++;
			}
			else
				continue;
			
			AddButton(slot, new ShopItem(status, ChatColor.UNDERLINE + "" + ChatColor.BOLD + "" + ChatColor.WHITE + "Server " + serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1), lore.toArray(new String[lore.size()]), Math.max(1, serverInfo.CurrentPlayers), false), new JoinServerButton(this, serverInfo.Name));
		}
		
		while (greenCount < 3)
		{
			setItem(9 + ((greenCount + 1) * 2), null);
			greenCount++;
		}
		
		while (yellowCount < 18)
		{
			setItem(yellowCount + 27, null);
			yellowCount++;
		}
	}

	public void Update()
	{
		ButtonMap.clear();
		BuildPage();
	}

	public void SelectServer(Player player, String serverName)
	{
		Plugin.SelectServer(player, serverName);
	}
}
