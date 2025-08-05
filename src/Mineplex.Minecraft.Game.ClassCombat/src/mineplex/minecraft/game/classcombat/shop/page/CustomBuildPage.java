package mineplex.minecraft.game.classcombat.shop.page;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.event.ClassSetupEvent;
import mineplex.minecraft.game.classcombat.Class.event.ClassSetupEvent.SetupType;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.DeleteCustomBuildButton;
import mineplex.minecraft.game.classcombat.shop.button.EditAndDontSaveCustomBuildButton;
import mineplex.minecraft.game.classcombat.shop.button.EditAndSaveCustomBuildButton;
import mineplex.minecraft.game.classcombat.shop.button.SelectCustomBuildButton;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;

public class CustomBuildPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
	private IPvpClass _pvpClass;
	
	protected boolean equipItems = true;
	protected boolean equipDefaultArmor = true;
	protected boolean saveActiveCustomBuild = false;
	
	public CustomBuildPage(ClassShopManager shopManager, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, IPvpClass pvpClass)
	{        
		super(shopManager, shop, clientManager, donationManager, "       Custom Build", player);
		_pvpClass = pvpClass;
				
		BuildPage();
	}

	@Override
	protected void BuildPage()
	{
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
	
					if (!Client.GetRank().Has(Rank.ULTRA) && !DonationManager.Get(Player.getName()).OwnsUnknownPackage("Competitive ULTRA"))
					{
						locked = true;
						lockedText = new String[] { "§rGet Ultra rank to access this slot" };
					}
					break;
				case 3:
					itemData = 2;
	
					if (!Client.GetRank().Has(Rank.ULTRA) && !DonationManager.Get(Player.getName()).OwnsUnknownPackage("Competitive ULTRA"))
					{
						locked = true;
						lockedText = new String[] { "§rGet Ultra rank to access this slot" };
					}
					break;
				default:
					itemData = 4;
	
					if (!Client.GetRank().Has(Rank.ULTRA) && !DonationManager.Get(Player.getName()).OwnsUnknownPackage("Competitive ULTRA"))
					{
						locked = true;
						lockedText = new String[] { "§rGet Ultra rank to access this slot" };
					}
					break;
			}

			ClientClass clientClass = Plugin.GetClassManager().Get(Player);
			
			CustomBuildToken customBuild = clientClass.GetCustomBuilds(_pvpClass).get(i);
			
			if (customBuild != null)
			{
				AddButton(slot, new ShopItem(Material.INK_SACK, itemData, customBuild.Name, lockedText, 1, locked, true), new SelectCustomBuildButton(this, customBuild));
			}
			else
			{
				getInventory().setItem(slot, new ShopItem(Material.INK_SACK, (byte)8, locked ? "Locked Build" : "Unsaved Build", lockedText, 1, locked, true).getHandle());
			}
			
			if (!locked)
			{
				if (customBuild == null)
				{
					customBuild = new CustomBuildToken();
					customBuild.CustomBuildNumber = i;
					customBuild.Name = "Build " + (i + 1);
					customBuild.PvpClassId = _pvpClass.GetSalesPackageId();
				}
				
		        AddButton(slot + 9, new ShopItem(Material.ANVIL, "Edit & Save Build", new String[] { }, 1, locked, true), new EditAndSaveCustomBuildButton(this, customBuild));
		        AddButton(slot + 18, new ShopItem(Material.WORKBENCH, "Edit & Don't Save Build", new String[] { }, 1, locked, true), new EditAndDontSaveCustomBuildButton(this, customBuild));
		        AddButton(slot + 36, new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..."}, 1, locked, true), new DeleteCustomBuildButton(this, customBuild));
			}
			else
			{
		        getInventory().setItem(slot + 9, new ShopItem(Material.ANVIL, "Edit & Save Build", new String[] { }, 1, locked, true).getHandle());
		        getInventory().setItem(slot + 18, new ShopItem(Material.WORKBENCH, "Edit & Don't Save Build", new String[] { }, 1, locked, true).getHandle());
		        getInventory().setItem(slot + 36, new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..."}, 1, locked, true).getHandle());
			}

			slot += 2;
		}
	}
	
	public void EditAndSaveCustomBuild(CustomBuildToken customBuild)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(Player);
		clientClass.SetActiveCustomBuild(_pvpClass, customBuild);
		
		ClassSetupEvent event = new ClassSetupEvent(Player, SetupType.SaveEditCustomBuild, _pvpClass.GetType(), customBuild.CustomBuildNumber, customBuild);				
		Plugin.GetPlugin().getServer().getPluginManager().callEvent(event);

		if (event.IsCancelled())
			return;

		clientClass.EquipCustomBuild(customBuild, false);
		clientClass.SetSavingCustomBuild(_pvpClass, customBuild);
		
		Shop.OpenPageForPlayer(Player, new SkillPage(Plugin, Shop, ClientManager, DonationManager, Player, _pvpClass, false));
	}
	
	public void EditAndDontSaveCustomBuild(CustomBuildToken customBuild)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(Player);
		clientClass.SetActiveCustomBuild(_pvpClass, customBuild);
		
		ClassSetupEvent event = new ClassSetupEvent(Player, SetupType.EditCustomBuild, _pvpClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
		Plugin.GetPlugin().getServer().getPluginManager().callEvent(event);
		
		if (event.IsCancelled())
			return;
		
		clientClass.EquipCustomBuild(customBuild, false);

		Shop.OpenPageForPlayer(Player, new SkillPage(Plugin, Shop, ClientManager, DonationManager, Player, _pvpClass, false));
	}
	
	public void SelectCustomBuild(CustomBuildToken customBuild)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(Player);
		clientClass.SetActiveCustomBuild(_pvpClass, customBuild);

		ClassSetupEvent event = new ClassSetupEvent(Player, SetupType.ApplyCustomBuild, _pvpClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
		Plugin.GetPluginManager().callEvent(event);
		
		if (event.IsCancelled())
			return;
		
		clientClass.EquipCustomBuild(customBuild);

		if (saveActiveCustomBuild)
			clientClass.SetSavingCustomBuild(_pvpClass, customBuild);

		Player.closeInventory();
	}
	
	@SuppressWarnings("deprecation")
	public void DeleteCustomBuild(CustomBuildToken customBuild)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(Player);
		
		//Event
		ClassSetupEvent event = new ClassSetupEvent(Player, SetupType.DeleteCustomBuild, _pvpClass.GetType(), customBuild.CustomBuildNumber + 1, customBuild);
		Plugin.GetPlugin().getServer().getPluginManager().callEvent(event);
		
		if (event.IsCancelled())
			return;
		
		clientClass.GetCustomBuilds(_pvpClass).remove(customBuild.CustomBuildNumber);

		BuildPage();
		Player.updateInventory();
	}
}
