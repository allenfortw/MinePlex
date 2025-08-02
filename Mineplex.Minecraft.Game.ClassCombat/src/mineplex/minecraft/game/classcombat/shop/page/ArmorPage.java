package mineplex.minecraft.game.classcombat.shop.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.Sound;

import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.SelectClassButton;
import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.core.common.util.C;

public class ArmorPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
	private boolean _purchasing;
	
	public ArmorPage(ClassShopManager shopManager, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, boolean purchasing)
	{        
		super(shopManager, shop, clientManager, donationManager, "       Armor", player);
		
		_purchasing = purchasing;
		
		BuildPage();
	}
	
	public void SelectClass(Player player, IPvpClass pvpClass)
	{			
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, .6f);
		
		if (_purchasing)
			Shop.OpenPageForPlayer(Player, new SkillPage(Plugin, Shop, ClientManager, DonationManager, Player, pvpClass, true));
		else
		{
			ClientClass clientClass = Plugin.GetClassManager().Get(player);
			
			player.getInventory().clear();
			
			clientClass.SetGameClass(pvpClass);
			pvpClass.ApplyArmor(player);
			clientClass.ClearDefaults();
			
			Shop.OpenPageForPlayer(Player, new CustomBuildPage(Plugin, Shop, ClientManager, DonationManager, player, pvpClass));
		}
	}

	@Override
	protected void BuildPage()
	{
        int slot = 9;
        
        for (IPvpClass gameClass : Plugin.GetClassManager().GetGameClasses())
        {
        	BuildArmorSelectPackage(gameClass, slot);
            
            slot += 2;
        }
	}
	
    private void BuildArmorSelectPackage(IPvpClass gameClass, int slot)
    {
    	List<String> lockedClassDesc = new ArrayList<String>();
    	List<String> unlockedClassDesc = new ArrayList<String>();
    	
    	lockedClassDesc.add(C.cBlack);
    	unlockedClassDesc.add(C.cBlack);
    	
    	lockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    	unlockedClassDesc.addAll(Arrays.asList(gameClass.GetDesc()));
    	
    	for (int i = 1; i < lockedClassDesc.size(); i++)
    	{
    		lockedClassDesc.set(i, C.cGray + lockedClassDesc.get(i));
    	}
    	
    	for (int i = 1; i < unlockedClassDesc.size(); i++)
    	{
    		unlockedClassDesc.set(i, C.cGray + unlockedClassDesc.get(i));
    	}
    	
    	AddButton(slot, new ShopItem(gameClass.GetHead(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    	AddButton(slot + 9, new ShopItem(gameClass.GetChestplate(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    	AddButton(slot + 18, new ShopItem(gameClass.GetLeggings(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    	AddButton(slot + 27, new ShopItem(gameClass.GetBoots(), gameClass.GetName(), 1, false), new SelectClassButton(this, gameClass));
    }
}
