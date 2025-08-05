package me.chiss.Core.Shop.page.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import me.chiss.Core.Class.IPvpClass;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.minecraft.game.core.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.core.classcombat.events.ClassSetupEvent;
import mineplex.minecraft.game.core.classcombat.events.ClassSetupEvent.SetupType;
import mineplex.minecraft.shop.item.ISalesPackage;
import mineplex.minecraft.shop.item.ItemPackage;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.ShopPageBase;
import me.chiss.Core.Shop.salespackage.ShopItem;

public class CustomBuildPage extends ShopPageBase
{
	protected IPvpClass _currentClass;

	protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> UnlockedClassMap;
	protected HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> LockedClassMap;

	protected boolean equipItems = true;
	protected boolean equipDefaultArmor = true;
	protected boolean saveActiveCustomBuild = false;

	public CustomBuildPage(Shop shop, String title, List<ICurrencyHandler> currencyHandlers, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> unlockedClassMap, HashMap<IPvpClass, HashMap<Integer, ISalesPackage>> lockedClassMap)
	{
		super(shop, title, currencyHandlers, null, null);

		UnlockedClassMap = unlockedClassMap;
		LockedClassMap = lockedClassMap;
	}

	@Override
	public void PlayerWants(CoreClient player, int slot) 
	{
		ISalesPackage shopItem = GetItem(slot);
		if (shopItem != null)
		{
			ItemStack item = getItem(slot);

			if (shopItem.CanFitIn(player))
			{
				int customBuildIndex = (slot % 9) / 2;				
				CustomBuildToken customBuild;

				if ((customBuildIndex > 1 && !player.Rank().Has(Rank.EMERALD, false))
						|| (customBuildIndex > 2 && !player.Rank().Has(Rank.DIAMOND, false)))
				{
					player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
				}
				else
				{
					if (player.Donor().GetCustomBuilds(_currentClass).containsKey(customBuildIndex))
					{
						customBuild = player.Donor().GetCustomBuilds(_currentClass).get(customBuildIndex);
						player.Donor().SetTokens(customBuild.SkillTokensBalance, customBuild.ItemTokensBalance);
						player.Donor().SetDefaultTokens(120, 120);
					}
					else
					{
						customBuild = new CustomBuildToken();
						customBuild.Name = "Build " + (customBuildIndex + 1);
						customBuild.PvpClassId = _currentClass.GetSalesPackageId();
						customBuild.SkillTokensBalance = 120;
						customBuild.ItemTokensBalance = 120;
						player.Donor().SetDefaultTokens(120, 120);	
					}

					player.Donor().SetActiveCustomBuild(customBuildIndex, _currentClass, customBuild);

					if (item.getType() == Material.INK_SACK && item.getData().getData() != 8)
					{
						//Event
						ClassSetupEvent event = new ClassSetupEvent(player.GetPlayer(), SetupType.ApplyCustomBuild, 
								_currentClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
						Shop.GetPlugin().getServer().getPluginManager().callEvent(event);
						
						if (event.IsCancelled())
							return;
						
						player.Class().EquipCustomBuild(customBuild, equipItems, equipDefaultArmor);

						if (saveActiveCustomBuild)
							player.Donor().SetSavingCustomBuild(customBuildIndex, _currentClass, customBuild);

						Shop.CloseShopForPlayer(player);
					}
					else if (item.getType() == Material.ANVIL)
					{
						//Event
						ClassSetupEvent event = new ClassSetupEvent(player.GetPlayer(), SetupType.SaveEditCustomBuild, 
								_currentClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);				
						Shop.GetPlugin().getServer().getPluginManager().callEvent(event);

						if (event.IsCancelled())
							return;
		
						player.Class().EquipCustomBuild(customBuild, equipItems, equipDefaultArmor);
						player.Donor().SetSavingCustomBuild(customBuildIndex, _currentClass, customBuild);
						Shop.TurnToNextPage(player);
					}
					else if (item.getType() == Material.WORKBENCH && customBuild.CustomBuildNumber != null)
					{
						//Event
						ClassSetupEvent event = new ClassSetupEvent(player.GetPlayer(), SetupType.EditCustomBuild, 
								_currentClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
						Shop.GetPlugin().getServer().getPluginManager().callEvent(event);
						
						if (event.IsCancelled())
							return;
						
						player.Class().EquipCustomBuild(customBuild, equipItems, equipDefaultArmor);
						Shop.TurnToNextPage(player);
					}
					else if (item.getType() == Material.FIRE && customBuild.CustomBuildNumber != null)
					{
						//Event
						ClassSetupEvent event = new ClassSetupEvent(player.GetPlayer(), SetupType.DeleteCustomBuild, 
								_currentClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
						Shop.GetPlugin().getServer().getPluginManager().callEvent(event);
						
						if (event.IsCancelled())
							return;
						
						player.Donor().GetCustomBuilds(_currentClass).remove(customBuildIndex);
						ReconstructPageForPlayer(player);
						player.Class().UpdateInventory();
					}
				}
			}
			else
			{
				player.GetPlayer().playSound(player.GetPlayer().getLocation(), Sound.ITEM_BREAK, 1f, .6f);
			}
		}
	}

	@Override
	public void UpdateBalance(CoreClient player)
	{

	}

	@Override
	public void Reset(CoreClient player)
	{

	}

	@Override
	public void OpenForPlayer(CoreClient player)
	{        
		if (_currentClass == null || _currentClass != player.Class().GetGameClass())
		{
			_currentClass = player.Class().GetGameClass();

			if (_currentClass == null)
				_currentClass = Shop.GetClassForPlayer(player);
		}
		else
		{
			if (player.Donor().IsSavingCustomBuild())
				player.Donor().SaveActiveCustomBuild(!equipItems);
		}

		ReconstructPageForPlayer(player);

		UpdateBalance(player);

		player.Class().OpenInventory(this);
	}

	private void ReconstructPageForPlayer(CoreClient player)
	{
		SalesPackageMap.clear();
		HashMap<Integer, ISalesPackage> packageMap = UnlockedClassMap.get(_currentClass);

		if (_currentClass != null)
		{
			for (Entry<Integer, ISalesPackage> entry : packageMap.entrySet())
			{            
				UpdateClassSlot(player, entry.getKey());
			}

			int slot = 9;

			for (int i=0; i < 5; i++)
			{
				byte itemData;
				String[] lockedText = new String[] { };
				boolean locked = false;

				switch (i)
				{
				case 0:
					itemData = 1;
					break;
				case 1:
					itemData = 14;
					break;
				case 2:
					itemData = 11;

					if (!player.Rank().Has(Rank.EMERALD, false))
					{
						locked = true;
						lockedText = new String[] { "§rGet " + (i < 3 ? "Silver" : "Gold") + " rank to access this slot" };
					}
					break;
				case 3:
					itemData = 2;

					if (!player.Rank().Has(Rank.DIAMOND, false))
					{
						locked = true;
						lockedText = new String[] { "§rGet " + (i < 3 ? "GOLD" : "DIAMOND") + " rank to access this slot" };
					}
					break;
				default:
					itemData = 4;

					if (!player.Rank().Has(Rank.DIAMOND, false))
					{
						locked = true;
						lockedText = new String[] { "§rGet " + (i < 3 ? "GOLD" : "DIAMOND") + " rank to access this slot" };
					}
					break;
				}

				if (player.Donor().GetCustomBuilds(_currentClass).containsKey(i))
				{
					CustomBuildToken customBuild = player.Donor().GetCustomBuilds(_currentClass).get(i);

					AddItem(new ItemPackage(new ShopItem(Material.INK_SACK, itemData, customBuild.Name, lockedText, 1, locked, true), 0, 0, 0, 0, false, -1), slot);
				}
				else
				{
					AddItem(new ItemPackage(new ShopItem(Material.INK_SACK, (byte)8, locked ? "Locked Build" : "Unsaved Build", lockedText, 1, locked, true), 0, 0, 0, 0, false, -1), slot);
				}

				slot += 2;
			}
		}
	}

	protected void UpdateClassSlot(CoreClient player, int slot)
	{
		if (UnlockedClassMap.get(_currentClass).get(slot).IsFree()
				|| (player.Rank().Has(Rank.EMERALD, false) && slot % 9 == 4)
				|| (player.Rank().Has(Rank.DIAMOND, false) && (slot % 9 == 6 || slot % 9 == 8)))
		{
			AddItem(UnlockedClassMap.get(_currentClass).get(slot), slot);
		}
		else
		{
			AddItem(LockedClassMap.get(_currentClass).get(slot), slot);
		}
	}
}
