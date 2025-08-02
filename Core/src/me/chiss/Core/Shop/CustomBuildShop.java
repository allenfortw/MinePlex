package me.chiss.Core.Shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.actions.ChangeCurrency;
import me.chiss.Core.Shop.actions.NextPage;
import me.chiss.Core.Shop.actions.PreviousPage;
import me.chiss.Core.Shop.actions.Purchase;
import me.chiss.Core.Shop.actions.Reset;
import me.chiss.Core.Shop.actions.Return;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.currency.ItemTokenHandler;
import me.chiss.Core.Shop.currency.SkillTokenHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.pagebuilder.game.CustomBuildPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.ItemPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.SkillsPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.WeaponPageBuilder;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPageBuilder;
import mineplex.minecraft.game.core.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.classcombat.Class.ClientClass;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.classcombat.Class.event.ClassSetupEvent;
import mineplex.minecraft.game.core.classcombat.Class.event.ClassSetupEvent.SetupType;
import mineplex.minecraft.game.core.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;

public class CustomBuildShop extends Shop 
{
	private IWeaponFactory _weaponFactory;
	private IItemFactory _itemFactory;
	
    public CustomBuildShop(IPlugin plugin, CoreClientManager manager, ClassManager gameClassFactory, SkillFactory skillFactory, IWeaponFactory weaponFactory, IItemFactory itemFactory) 
    {
        super(plugin, manager);
        
        _weaponFactory = weaponFactory;
        _itemFactory = itemFactory;
        
        ShopBlockType = Material.ENCHANTMENT_TABLE;
        
        ICurrencyHandler itemTokenHandler = new ItemTokenHandler();
        ICurrencyHandler skillTokenHandler = new SkillTokenHandler();
        
        AddPageBuilder(new ArmorPageBuilder(this, "-----------Class-------Skills", gameClassFactory));
        AddPageBuilder(new CustomBuildPageBuilder(this, "Class------Custom------Skills", gameClassFactory));
        AddPageBuilder(new SkillsPageBuilder(this,"Class-------Skills-----------", skillFactory, gameClassFactory));
        
        CreateShopActions();
    }
    
    @Override
    protected void CreateShopActions()
    {
        new NextPage(Plugin.GetPlugin(), this, ClientManager);
        new PreviousPage(Plugin.GetPlugin(), this, ClientManager);
        new Reset(Plugin.GetPlugin(), this, ClientManager);
        new Return(Plugin.GetPlugin(), this, ClientManager, _weaponFactory, _itemFactory);
        new Purchase(Plugin.GetPlugin(), this, ClientManager);
        new ChangeCurrency(Plugin.GetPlugin(), this, ClientManager);
    }
    
    @Override
    public void ShowSkillHotBarForPlayer(CoreClient player) { }    
    
    @Override
    public void OpenShopForPlayer(Player player)
    {
    	super.OpenShopForPlayer(player);
    	
    	ClientClass clientClass = ClassManager.Get(player);
    	
		ClassType classType = null;
		if (clientClass.GetGameClass() != null)
			classType = clientClass.GetGameClass().GetType();
		
		ClassSetupEvent event = new ClassSetupEvent(player, SetupType.OpenMain, classType, 0, null);
		
		UtilServer.getServer().getPluginManager().callEvent(event);
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
        
        PageMap.remove(key);

        if (player.Donor().IsSavingCustomBuild())
        	player.Donor().SaveActiveCustomBuild(true);
        
    	if (InventoryMap.containsKey(player.GetPlayerName()))
    	{
    		player.GetPlayer().getInventory().setContents(InventoryMap.get(player.GetPlayerName()));
    		player.GetPlayer().getInventory().setArmorContents(ArmorMap.get(player.GetPlayerName()));
    		player.Class().SetGameClass(null);    			
    		InventoryMap.remove(key);
    		ArmorMap.remove(key);
    	}
    	
    	player.GetPlayer().updateInventory();
    }
}
