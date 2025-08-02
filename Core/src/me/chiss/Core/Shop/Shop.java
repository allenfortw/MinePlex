package me.chiss.Core.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Class.event.ClassSetupEvent;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import me.chiss.Core.ClientData.IClientClass;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.actions.ChangeCurrency;
import me.chiss.Core.Shop.actions.NextPage;
import me.chiss.Core.Shop.actions.PreviousPage;
import me.chiss.Core.Shop.actions.Purchase;
import me.chiss.Core.Shop.actions.Reset;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.pagebuilder.IPageBuilder;
import me.chiss.Core.Shop.salespackage.ShopItem;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilServer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Shop implements Listener
{
	private HashMap<String, IPvpClass> _classMap;
	private HashMap<String, Long> _errorThrottling;
	private HashMap<String, Long> _purchaseBlock;

	protected IPlugin Plugin;
	protected CoreClientManager ClientManager;
	protected List<IPageBuilder> PageBuilders;
	protected Material ShopBlockType;
	protected HashMap<String, ItemStack[]> InventoryMap;
	protected HashMap<String, ItemStack[]> ArmorMap;

	protected HashMap<String, IShopPage> PageMap;
	
	protected boolean RestoreInventory;

	public Shop(IPlugin plugin, CoreClientManager accountManager)
	{
		Plugin = plugin;
		ClientManager = accountManager;
		PageBuilders = new ArrayList<IPageBuilder>();
		ShopBlockType = Material.AIR;
		PageMap = new HashMap<String, IShopPage>();
		InventoryMap = new HashMap<String, ItemStack[]>();
		ArmorMap = new HashMap<String, ItemStack[]>();
		_classMap = new HashMap<String, IPvpClass>();
		_errorThrottling = new HashMap<String, Long>();
		_purchaseBlock = new HashMap<String, Long>();

		Plugin.GetPlugin().getServer().getPluginManager().registerEvents(this, Plugin.GetPlugin());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void OnPlayerClickItem(InventoryClickEvent event)
	{
		if (ClickedCurrentPage(event))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void OnPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;

		if (GetPage(event.getPlayer().getName()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void OnPlayerCloseInventory(InventoryCloseEvent event)
	{
		if (ClickedCurrentPage(event.getPlayer(), event.getInventory()))
		{
			CloseShopForPlayer(ClientManager.Get((Player)event.getPlayer()));
		}
	}
	
	@EventHandler
	public void OnPlayerQuit(PlayerQuitEvent event)
	{
		IShopPage page = GetPage(event.getPlayer().getName());
		
		if (page != null)
		{
			CloseShopForPlayer(ClientManager.Get((Player)event.getPlayer()));
			event.getPlayer().closeInventory();
		}
	}

	@EventHandler
	public void OnPlayerOpenInventory(InventoryOpenEvent event)
	{
		if (event.isCancelled() && ClickedCurrentPage(event.getPlayer(), event.getInventory()))
		{
			CloseShopForPlayer(ClientManager.Get((Player)event.getPlayer()));
		}
	}

	public boolean ShouldOpenShop(Block clickedBlock) 
	{
		return clickedBlock.getType() == ShopBlockType;
	}

	public boolean HasNextPage(CoreClient gamePlayer)
	{
		String key = gamePlayer.GetPlayerName();
		return PageMap.get(key).HasNextPage();
	}

	public boolean HasPreviousPage(CoreClient gamePlayer)
	{
		String key = gamePlayer.GetPlayerName();
		return PageMap.get(key).HasPreviousPage();
	}

	public void OpenShopForPlayer(Player player)
	{
		CoreClient gamePlayer = ClientManager.Get(player);
		String key = gamePlayer.GetPlayerName();
		InventoryMap.put(key, player.getInventory().getContents().clone());
		ArmorMap.put(key, player.getInventory().getArmorContents());

		if (!PageMap.containsKey(key))
		{
			BuildPagesForPlayer(gamePlayer);
		}

		ShowSkillHotBarForPlayer(gamePlayer);

		GetPage(gamePlayer).OpenForPlayer(gamePlayer);
	}

	public void TurnToNextPage(CoreClient gamePlayer)
	{
		gamePlayer.GetPlayer().playSound(gamePlayer.GetPlayer().getLocation(), Sound.SILVERFISH_KILL, .6f, .9f);

		GetPage(gamePlayer).CloseForPlayer(gamePlayer);

		String key = gamePlayer.GetPlayerName();
		PageMap.put(key, PageMap.get(key).GetNextPage());

		GetPage(gamePlayer).OpenForPlayer(gamePlayer);
	}

	public void TurnToPreviousPage(CoreClient gamePlayer)
	{
		gamePlayer.GetPlayer().playSound(gamePlayer.GetPlayer().getLocation(), Sound.SILVERFISH_KILL, .6f, .9f);

		GetPage(gamePlayer).CloseForPlayer(gamePlayer);

		String key = gamePlayer.GetPlayerName();
		PageMap.put(key, PageMap.get(key).GetPreviousPage());

		GetPage(gamePlayer).OpenForPlayer(gamePlayer);
	}

	protected IShopPage GetPage(String playerName)
	{
		return PageMap.get(playerName);
	}

	public IShopPage GetPage(CoreClient gamePlayer)
	{
		String key = gamePlayer.GetPlayerName();
		return PageMap.get(key);
	}

	public void SetPage(CoreClient gamePlayer, IShopPage category)
	{
		String key = gamePlayer.GetPlayerName();
		PageMap.put(key, category);
	}

	public boolean ClickedCurrentPage(InventoryClickEvent event)
	{
		return ClickedCurrentPage(event.getWhoClicked(), event.getInventory());
	}

	public boolean ClickedCurrentPage(HumanEntity clicker, Inventory inventory)
	{
		return PageMap.containsKey(clicker.getName()) && inventory.getTitle().equalsIgnoreCase(GetPage(ClientManager.Get((Player)clicker)).GetTitle());
	}

	protected void CreateShopActions()
	{
		new NextPage(Plugin.GetPlugin(), this, ClientManager);
		new PreviousPage(Plugin.GetPlugin(), this, ClientManager);
		new Purchase(Plugin.GetPlugin(), this, ClientManager);
		new Reset(Plugin.GetPlugin(), this, ClientManager);
		new ChangeCurrency(Plugin.GetPlugin(), this, ClientManager);
	}

	protected void AddPageBuilder(IPageBuilder pageBuilder)
	{        
		PageBuilders.add(pageBuilder);
	}

	protected void BuildPagesForPlayer(CoreClient player)
	{
		String key = player.GetPlayerName();
		int pageNumber = 1;
		IShopPage previousPage = null;

		for (IPageBuilder page : PageBuilders)
		{
			if (!PageMap.containsKey(key))
			{
				IShopPage newPage = page.BuildForPlayer(player);
				newPage.SetPageNumber(pageNumber);

				PageMap.put(key, newPage);
			}
			else
			{
				previousPage = PageMap.get(key);

				while (previousPage.HasNextPage())
				{
					previousPage = previousPage.GetNextPage();
				}

				IShopPage newPage = page.BuildForPlayer(player);
				newPage.SetPageNumber(pageNumber);

				previousPage.SetNextPage(newPage);
				newPage.SetPreviousPage(previousPage);
			}

			pageNumber++;
		}
	}

	@SuppressWarnings("deprecation")
	public void CloseShopForPlayer(CoreClient player)
	{
    	String key = player.GetPlayerName();
        GetPage(player).CloseForPlayer(player);        
        
        IShopPage page = GetPage(player);
        
        while (page.HasPreviousPage())
        {
        	page = page.GetPreviousPage();
        }
        
        IShopPage nextPage;
        
        while (page.HasNextPage())
        {
        	nextPage = page.GetNextPage();
        	page.SetNextPage(null);
        	nextPage.SetPreviousPage(null);
        	page = nextPage;
        }        
        
        if (RestoreInventory)
        {
	    	if (InventoryMap.containsKey(player.GetPlayerName()))
	    		player.GetPlayer().getInventory().setContents(InventoryMap.get(player.GetPlayerName()));
	    	
	    	if (ArmorMap.containsKey(player.GetPlayerName()))
	    		player.GetPlayer().getInventory().setArmorContents(ArmorMap.get(player.GetPlayerName()));
        }
        
        player.GetPlayer().updateInventory();
    	
        PageMap.remove(key);
        InventoryMap.remove(key);
        ArmorMap.remove(key);

        if (player.Donor().IsSavingCustomBuild())
        	player.Donor().SaveActiveCustomBuild(true);
        
    	if (player.GetPlayer().isOnline())
    	{
    		player.GetPlayer().sendMessage(ChatColor.BLUE + "Shop>" + ChatColor.GRAY + " Visit " + ChatColor.YELLOW + Plugin.GetWebServerAddress() + "Store" + ChatColor.GRAY + " to purchase Credits!");
    	}
	}

	public void ResetShopFor(Player player)
	{
		String key = player.getName();

		if (PageMap.containsKey(key))
		{
			IShopPage currentPage = PageMap.get(key);

			while (currentPage.HasNextPage())
			{
				currentPage = currentPage.GetNextPage();
			}

			currentPage.ResetVisuals();

			while (currentPage.HasPreviousPage())
			{
				currentPage = currentPage.GetPreviousPage();
				currentPage.ResetVisuals();
			}

			PageMap.put(key, currentPage);
		}
	}

	public void SetClassForPlayer(CoreClient player, IPvpClass gameClass)
	{
		_classMap.put(player.GetPlayerName(), gameClass);
	}

	public IPvpClass GetClassForPlayer(CoreClient player)
	{
		return _classMap.get(player.GetPlayerName());
	}

	public void ShowSkillHotBarForPlayer(CoreClient player)
	{
		PlayerInventory playerInv = player.Class().GetInventory();

		for (int i = 9; i < 18; i++)
		{
			playerInv.setItem(i, null);
		}

		for (int i = 27; i < 36; i++)
		{
			playerInv.setItem(i, null);
		}

		String[] skillList = new String[3];

		ISkill swordSkill = player.Class().GetSkillByType(SkillType.Sword);
		ISkill axeSkill = player.Class().GetSkillByType(SkillType.Axe);
		ISkill bowSkill = player.Class().GetSkillByType(SkillType.Bow);
		ISkill classPassiveASkill = player.Class().GetSkillByType(SkillType.PassiveA);
		ISkill classPassiveBSkill = player.Class().GetSkillByType(SkillType.PassiveB);
		ISkill globalPassiveASkill = player.Class().GetSkillByType(SkillType.PassiveC);
		ISkill globalPassiveBSkill = player.Class().GetSkillByType(SkillType.PassiveD);
		ISkill globalPassiveCSkill = player.Class().GetSkillByType(SkillType.PassiveE);

		String swordString = (swordSkill == null ? "None" : swordSkill.GetName() + " " + player.Class().GetSkillLevel(swordSkill));
		String axeString = (axeSkill == null ? "None" : axeSkill.GetName() + " " + player.Class().GetSkillLevel(axeSkill));
		String bowString = (bowSkill == null ? "None" : bowSkill.GetName() + " " + player.Class().GetSkillLevel(bowSkill));
		String classPassiveAString = (classPassiveASkill == null ? "None" : classPassiveASkill.GetName() + " " + player.Class().GetSkillLevel(classPassiveASkill));
		String classPassiveBString = (classPassiveBSkill == null ? "None" : classPassiveBSkill.GetName() + " " + player.Class().GetSkillLevel(classPassiveBSkill));
		String globalPassiveAString = (globalPassiveASkill == null ? "None" : globalPassiveASkill.GetName() + " " + player.Class().GetSkillLevel(globalPassiveASkill));
		String globalPassiveBString = (globalPassiveBSkill == null ? "None" : globalPassiveBSkill.GetName() + " " + player.Class().GetSkillLevel(globalPassiveBSkill));
		String globalPassiveCString = (globalPassiveCSkill == null ? "None" : globalPassiveCSkill.GetName() + " " + player.Class().GetSkillLevel(globalPassiveCSkill));

		skillList[0] = C.cGray + "Weapon skills: " +  swordString + ", " + axeString + ", " + bowString;
		skillList[1] = C.cGray + "Class Passive Skills: " + classPassiveAString + ", " + classPassiveBString;
		skillList[2] = C.cGray + "Global Passive Skills: " + globalPassiveAString + ", " + globalPassiveBString + ", " + globalPassiveCString;

		ShopItem sword = new ShopItem(Material.IRON_SWORD, "Sword Skill:", GetLore(player.Class(), swordSkill), 1, true, true);
		ShopItem axe = new ShopItem(Material.IRON_AXE, "Axe Skill:", GetLore(player.Class(), axeSkill), 1, true, true);
		ShopItem bow = new ShopItem(Material.BOW, "Bow Skill:", GetLore(player.Class(), bowSkill), 1, true, true);
		ShopItem classPassiveA = new ShopItem(Material.BOOK, "Class Passive A Skills:", GetLore(player.Class(), classPassiveASkill), 1, true, true);
		ShopItem classPassiveB = new ShopItem(Material.BOOK, "Class Passive B Skills:", GetLore(player.Class(), classPassiveBSkill), 1, true, true);
		ShopItem globalPassiveA = new ShopItem(Material.BOOK, "Global Passive A Skill:", GetLore(player.Class(), globalPassiveASkill), 1, true, true);
		ShopItem globalPassiveB = new ShopItem(Material.BOOK, "Global Passive B Skill:", GetLore(player.Class(), globalPassiveBSkill), 1, true, true);
		ShopItem globalPassiveC = new ShopItem(Material.BOOK, "Global Passive C Skill:", GetLore(player.Class(), globalPassiveCSkill), 1, true, true);

		playerInv.setItem(18, new ShopItem(Material.WRITTEN_BOOK, "Your Skills:", skillList, 1, true, true));
		playerInv.setItem(19, sword);
		playerInv.setItem(20, axe);
		playerInv.setItem(21, bow);
		playerInv.setItem(22, classPassiveA);
		playerInv.setItem(23, classPassiveB);
		playerInv.setItem(24, globalPassiveA);
		playerInv.setItem(25, globalPassiveB);
		playerInv.setItem(26, globalPassiveC);
	}

	protected String[] GetLore(IClientClass playerClass, ISkill skill)
	{
		if (skill == null)
			return new String[] { ChatColor.GRAY + "None" };

		int skillLevel = skill.;
		String[] lore = new String[2 + skill.GetDesc().length];
		lore[0] = ChatColor.GRAY + "§l" + (skillLevel == 0 ? "None" : (skill.GetName() + " " + skillLevel));

		if (skillLevel != 0)
			lore[1] = " ";

		for (int i=2; i <= skill.GetDesc().length; i++)
		{
			lore[i] = ChatColor.GRAY + "" + skill.GetDesc()[i-1];
		}

		return lore;
	}

	public JavaPlugin GetPlugin() 
	{
		return Plugin.GetPlugin();
	}

	public void AddPlayerProcessError(CoreClient player) 
	{
		if (_errorThrottling.containsKey(player.GetPlayerName()) && (System.currentTimeMillis() - _errorThrottling.get(player.GetPlayerName()) <= 5000))
			_purchaseBlock.put(player.GetPlayerName(), System.currentTimeMillis());

		_errorThrottling.put(player.GetPlayerName(), System.currentTimeMillis());
	}

	public boolean CanPlayerAttemptPurchase(CoreClient player)
	{
		return !_purchaseBlock.containsKey(player.GetPlayerName()) || (System.currentTimeMillis() - _purchaseBlock.get(player.GetPlayerName()) > 10000);
	}
	
	public Material GetBlockType()
	{
		return ShopBlockType;
	}
}
