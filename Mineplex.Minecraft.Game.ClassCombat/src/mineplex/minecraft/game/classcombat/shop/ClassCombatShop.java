package mineplex.minecraft.game.classcombat.shop;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPage;

public class ClassCombatShop extends ShopBase<ClassShopManager>
{
	private NautHashMap<String, ItemStack[]> _inventoryStorage = new NautHashMap<String, ItemStack[]>();
	private NautHashMap<String, ItemStack[]> _armorStorage = new NautHashMap<String, ItemStack[]>();
	
	protected boolean Purchasing = false;
	protected boolean Customizing = false;
	
	public ClassCombatShop(ClassShopManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
	{
		super(plugin, clientManager, donationManager, name, CurrencyType.Gems);
	}

	@Override
	protected ShopPageBase<ClassShopManager, ClassCombatShop> BuildPagesFor(Player player)
	{
		return new ArmorPage(Plugin, this, ClientManager, DonationManager, player, Purchasing);
	}
	
	@Override
	protected ShopPageBase<ClassShopManager, ? extends ShopBase<ClassShopManager>> GetOpeningPageForPlayer(Player player)
	{
		return new ArmorPage(Plugin, this, ClientManager, DonationManager, player, Purchasing);
	}
	
	@Override
	protected void OpenShopForPlayer(Player player)
	{
		if (Purchasing || Customizing)
		{
			_inventoryStorage.put(player.getName(), player.getInventory().getContents());
			_armorStorage.put(player.getName(), player.getInventory().getArmorContents());
			
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			
			((CraftPlayer)player).getHandle().updateInventory(((CraftPlayer)player).getHandle().defaultContainer);
		}
	}
	
	@Override
	protected void CloseShopForPlayer(Player player)
	{
		ClientClass clientClass = Plugin.GetClassManager().Get(player);
		
		if (clientClass != null && clientClass.IsSavingCustomBuild())
		{
			clientClass.SaveActiveCustomBuild();			
		}
		
		if (player.isOnline())
		{
			if (Purchasing || Customizing)
			{
				player.getInventory().setContents(_inventoryStorage.get(player.getName()));			
				player.getInventory().setArmorContents(_armorStorage.get(player.getName()));
			}
			
			((CraftPlayer)player).getHandle().updateInventory(((CraftPlayer)player).getHandle().defaultContainer);
		}
		
		_inventoryStorage.remove(player.getName());
		_armorStorage.remove(player.getName());
	}
}
