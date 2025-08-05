package mineplex.core.punish.UI;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.punish.Category;
import mineplex.core.punish.Punish;
import mineplex.core.punish.PunishClient;
import mineplex.core.punish.PunishTrackUtil;
import mineplex.core.punish.Punishment;
import mineplex.core.punish.PunishmentResponse;
import mineplex.core.punish.PunishmentSorter;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ShopItem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryCustom;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PunishPage extends CraftInventoryCustom implements Listener
{
	private Punish _plugin;
	private NautHashMap<Integer, IButton> _buttonMap;
	private Player _player;
	private String _target;
	private String _reason;
	private ShopItem _chatOffenseButton;
	private ShopItem _advertisingButton;
	private ShopItem _exploitingButton;
	private ShopItem _hackingButton;
	private ShopItem _otherButton;
	
	public PunishPage(Punish plugin, Player player, String target, String reason) 
	{
		super(null, 54, "            Punish");
		
		_plugin = plugin;
		_buttonMap = new NautHashMap<Integer, IButton>();
		
		_player = player;
		_target = target;
		_reason = reason;
		
		BuildPage();
		
		_player.openInventory(this);
		_plugin.RegisterEvents(this);
	}
	
	private void BuildPage()
	{
		// Player head
		getInventory().setItem(4, new ShopItem(Material.SKULL_ITEM, (byte)3, _target, new String[] { ChatColor.RESET + _reason }, 1, false, true).getHandle());
		
		PunishClient client = _plugin.GetClient(_target);
		
		int chatOffenseCount = 0;
		int advertisingCount = 0;
		int exploitingCount = 0;
		int hackingCount = 0;
		int otherCount = 0;
		
		List<Entry<Category, Punishment>> punishments = new ArrayList<Entry<Category, Punishment>>();
		
		for (Category category : client.GetPunishments().keySet())
		{
			for (Punishment punishment : client.GetPunishments().get(category))
			{
				punishments.add(new AbstractMap.SimpleEntry<Category, Punishment>(category, punishment));
			}
			
			switch (category)
			{
				case ChatOffense:
					chatOffenseCount = client.GetPunishments().get(category).size();
					break;
				case Advertisement:
					advertisingCount = client.GetPunishments().get(category).size();
					break;
				case Exploiting:
					exploitingCount = client.GetPunishments().get(category).size();
					break;
				case Hacking:
					hackingCount = client.GetPunishments().get(category).size();
					break;
				case Other:
					otherCount = client.GetPunishments().get(category).size();
					break;
				default:
					break;
				
			}
		}
		
		String examplePrefix = ChatColor.RESET + "" + ChatColor.GRAY;
		
		_chatOffenseButton = new ShopItem(Material.BOOK_AND_QUILL, (byte)0, "Chat Offense", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + chatOffenseCount, examplePrefix + "Verbal Abuse, Spam, Harassment, Trolling, etc" }, 1, false, true);
		_advertisingButton = new ShopItem(Material.SIGN, (byte)0, "Advertising", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + advertisingCount, examplePrefix + "Broadcasting another server ip." }, 1, false, true);
		_exploitingButton = new ShopItem(Material.HOPPER, (byte)0, "Gameplay Offense", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + exploitingCount, examplePrefix + "Commmand/Map/Class/Skill exploits, etc" }, 1, false, true);
		_hackingButton = new ShopItem(Material.IRON_SWORD, (byte)0, "Hacking", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + hackingCount, examplePrefix + "X-ray, Forcefield, Speed, Fly, Inventory Hacks, etc" }, 1, false, true);
		_otherButton = new ShopItem(Material.BEACON, (byte)0, "Other", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + otherCount, examplePrefix + "Permanent punishments for unique cases." }, 1, false, true);
		
		getInventory().setItem(9, _chatOffenseButton.getHandle());
		getInventory().setItem(11, _advertisingButton.getHandle());
		getInventory().setItem(13, _exploitingButton.getHandle());
		getInventory().setItem(15, _hackingButton.getHandle());
		getInventory().setItem(17, _otherButton.getHandle());
				
		long punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.ChatOffense, 1);
		AddButton(18, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + F.time(UtilTime.convertString(punishTime * 3600000, 1, TimeUnit.FIT)), examplePrefix + "Spamming LOL LOLO LOLOL", examplePrefix + "F U Bob after bob tnt'd his base", examplePrefix + "Harassing admin to revoke a punishment" }, 1, false, true), new PunishButton(this, Category.ChatOffense, 1, false, punishTime));
		punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.ChatOffense, 2);
		AddButton(27, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + F.time(UtilTime.convertString(punishTime * 3600000, 1, TimeUnit.FIT)), examplePrefix + "Spamming 'I LIKE CATS I LIKE CATS I LIKE CATS' 10 times.", examplePrefix + "General rudeness between players.", examplePrefix + "'go fucking cry, you baby'.", examplePrefix + "Does not necessarily require swearing.", examplePrefix + "Shit admins are shit" }, 1, false, true), new PunishButton(this, Category.ChatOffense, 2, false, punishTime));
		punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.ChatOffense, 3);
		AddButton(36, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + F.time(UtilTime.convertString(punishTime * 3600000, 1, TimeUnit.FIT)), examplePrefix + "Spam bot", examplePrefix + "Severe abuse between players, back and forth argument in chat.", examplePrefix + "'fuck off, you admins are fucking awful', 'you should go and fucking die'" }, 1, false, true), new PunishButton(this, Category.ChatOffense, 3, false, punishTime));
		
		punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.Advertisement, 1);
		AddButton(20, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + F.time(UtilTime.convertString(punishTime * 3600000, 1, TimeUnit.FIT)), examplePrefix + "Talking about another server, though asked not to." }, 1, false, true), new PunishButton(this, Category.Advertisement, 1, false, punishTime));
		punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.Advertisement, 2);
		AddButton(29, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Joining and asking other players to come check out another server." }, 1, false, true), new PunishButton(this, Category.Advertisement, 2, false, -1));
		AddButton(38, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Joining the server and spamming 'www.crapexampleserver.com'"}, 1, false, true), new PunishButton(this, Category.Advertisement, 3, true, -1));
		
		punishTime = PunishTrackUtil.GetPunishTime(_plugin.GetClient(_target), Category.Exploiting, 1);
		AddButton(22, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + F.time(UtilTime.convertString(punishTime * 3600000, 1, TimeUnit.FIT)), examplePrefix + "Hearing about a new exploit, and trying it out once.", examplePrefix + "For example, a command to start a world event." }, 1, false, true), new PunishButton(this, Category.Exploiting, 1, true, punishTime));
		AddButton(31, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Something like... abusing a ban command.", examplePrefix + "Intentionally ruining experience for others via exploit." }, 1, false, true),  new PunishButton(this, Category.Exploiting, 2, true, -1));
		AddButton(40, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Ruins map economy completely by exploiting a dupe bug.", examplePrefix + "Crashing server via exploit." }, 1, false, true), new PunishButton(this, Category.Exploiting, 3, true, -1));
		
		AddButton(24, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Using XRay on server" }, 1, false, true), new PunishButton(this, Category.Hacking, 1, true, -1));
		AddButton(33, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Fly Hacks, Derp, Nuker, Speed." }, 1, false, true), new PunishButton(this, Category.Hacking, 2, true, -1));
		AddButton(42, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Forcefield Hacks, V/No-Clip, Impersonating accounts, etc" }, 1, false, true), new PunishButton(this, Category.Hacking, 3, true, -1));
		
		AddButton(26, new ShopItem(Material.REDSTONE_BLOCK, (byte)0, "Permanent Ban", new String[] { ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Better have a good reason for using this." }, 1, false, true), new PunishButton(this, Category.Other, 1, true, -1));
		AddButton(35, new ShopItem(Material.EMERALD_BLOCK, (byte)0, "Permanent Mute", new String[] { ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "PERMANENT", examplePrefix + "Better have a good reason for using this." }, 1, false, true), new PunishButton(this, Category.Other, 1, false, -1));
		
		Collections.sort(punishments, new PunishmentSorter());
		
		int slot = 45;
		
		for (Entry<Category, Punishment> punishmentEntry : punishments)
		{
			if (slot >= 54)
				break;
			
			ShopItem button = null;
			
			switch (punishmentEntry.getKey())
			{
				case ChatOffense:
					button = _chatOffenseButton.clone();
					break;
				case Advertisement:
					button = _advertisingButton.clone();
					break;
				case Exploiting:
					button = _exploitingButton.clone();
					break;
				case Hacking:
					button = _hackingButton.clone();
					break;
				case Other:
					button = _otherButton.clone();
					break;
				default:
					break;				
			}
			
			Punishment punishment = punishmentEntry.getValue();
			
			button.SetLore(new String[] 
			{
				ChatColor.RESET + "Punishment Type: " + ChatColor.YELLOW + punishment.GetCategory().toString(),
				ChatColor.RESET + "Severity: " + ChatColor.YELLOW + punishment.GetSeverity(),
				ChatColor.RESET + "Reason: " + ChatColor.YELLOW + punishment.GetReason(),
				ChatColor.RESET + "Admin: " + ChatColor.YELLOW + punishment.GetAdmin(),
				ChatColor.RESET + "Date of Ban: " + ChatColor.YELLOW + UtilTime.when(punishment.GetTime()),
				ChatColor.RESET + "Ban Duration: " + (punishment.GetCategory() == Category.Other ? ChatColor.RED + "PERMANENT" : (ChatColor.RED + "" + F.time(UtilTime.convertString((long)(punishment.GetHours() * 3600000), 1, TimeUnit.FIT)))),
				ChatColor.RESET + "Expires In: " + ChatColor.RED + (punishment.GetCategory() == Category.Other ? "NEVER" : (punishment.GetRemaining() > 0 ? F.time(UtilTime.convertString((long)(punishment.GetRemaining()), 1, TimeUnit.FIT)) : ChatColor.GREEN + "EXPIRED")),
				ChatColor.RESET + "Removed by: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveAdmin() : ChatColor.RED + "Not Removed"),
				ChatColor.RESET + "Remove Reason: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveReason() : ChatColor.RED + "Not Removed"),
			});
			
			if ((punishment.GetHours() == -1 || punishment.GetRemaining() > 0) && !punishment.GetRemoved() && punishment.GetActive())
			{
				button.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				AddButton(slot, button, new RemovePunishmentButton(this, punishment, button));
			}
			else
			{
				getInventory().setItem(slot, button.getHandle());
			}
			
			slot++;
		}
	}
	
	@EventHandler
	public void OnInventoryClick(InventoryClickEvent event)
	{
		if (inventory.getName().equalsIgnoreCase(event.getInventory().getTitle()) && event.getWhoClicked() == _player)
		{
			if (_buttonMap.containsKey(event.getRawSlot()))
			{
				if (event.getWhoClicked() instanceof Player)
				{
					_buttonMap.get(event.getRawSlot()).Clicked((Player)event.getWhoClicked());
				}
			}
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnInventoryClose(InventoryCloseEvent event)
	{
		if (inventory.getName().equalsIgnoreCase(event.getInventory().getTitle()) && event.getPlayer() == _player)
		{
			ClosePunish();
		}
	}

	private void AddButton(int slot, ShopItem item, IButton button)
	{
		getInventory().setItem(slot, item.getHandle());
		_buttonMap.put(slot, button);
	}

	public void AddInfraction(Category category, int severity, boolean ban, long punishTime)
	{
		_plugin.AddPunishment(_target, category, _reason, _player, severity, ban, punishTime);
		_player.closeInventory();
		ClosePunish();
	}
	
	private void ClosePunish()
	{
		HandlerList.unregisterAll(this);
	}

	public void RemovePunishment(final Punishment punishment, final ItemStack item)
	{
		_plugin.RemovePunishment(punishment.GetPunishmentId(), _target, _player, _reason, new Callback<String>()
		{
			@Override
			public void run(String result)
			{
				PunishmentResponse punishResponse = PunishmentResponse.valueOf(result);
				
				if (punishResponse != PunishmentResponse.PunishmentRemoved)
				{
					_player.sendMessage(F.main(_plugin.GetName(), "There was a problem removing the punishment."));
				}
				else
				{
					punishment.Remove(_player.getName(), _reason);
					_player.closeInventory();
					ClosePunish();
				}
			}
		});
	}
}
