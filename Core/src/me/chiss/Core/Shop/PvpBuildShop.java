package me.chiss.Core.Shop;


import org.bukkit.Material;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.actions.Return;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.currency.ItemTokenHandler;
import me.chiss.Core.Shop.currency.SkillTokenHandler;
import me.chiss.Core.Shop.pagebuilder.game.PvpCustomBuildPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.SkillsPageBuilder;
import me.chiss.Core.Skill.ISkillFactory;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.server.IRepository;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;

public class PvpBuildShop extends Shop 
{
    private IWeaponFactory _weaponFactory;
    private IItemFactory _itemFactory;
    
    public PvpBuildShop(IPlugin plugin, IRepository repository, CoreClientManager manager, IClassFactory gameClassFactory, ISkillFactory skillFactory) 
    {
        super(plugin, repository, manager);

        ShopBlockType = Material.ENCHANTMENT_TABLE;
        
        ICurrencyHandler itemTokenHandler = new ItemTokenHandler();
        ICurrencyHandler skillTokenHandler = new SkillTokenHandler();
        
        AddPageBuilder(new PvpCustomBuildPageBuilder(this, "----------Builds------Skills", gameClassFactory, itemTokenHandler));
        AddPageBuilder(new SkillsPageBuilder(this,"Builds-------Skills---------", skillFactory, gameClassFactory, skillTokenHandler));
        
        CreateShopActions();
    }
    
    @Override
    public void ShowSkillHotBarForPlayer(CoreClient player) { }
    
    @Override
    protected void CreateShopActions()
    {
        super.CreateShopActions();
        new Return(Plugin.GetPlugin(), this, ClientManager, _weaponFactory, _itemFactory);
    }
    
    public void CloseShopForPlayer(CoreClient player)
    {
    	String key = player.GetPlayerName();
        GetPage(player).CloseForPlayer(player);        
        
        PageMap.remove(key);

        if (player.Donor().IsSavingCustomBuild())
        	player.Donor().SaveActiveCustomBuild(false);
        
    	if (InventoryMap.containsKey(player.GetPlayerName()))
    	{
    		player.Class().GetInventory().setContents(InventoryMap.get(player.GetPlayerName()));
    		player.Class().GetInventory().setArmorContents(ArmorMap.get(player.GetPlayerName()));			
    		player.Donor().SetTokens(0, 0);
    		player.Donor().SetDefaultTokens(0, 0);
    		InventoryMap.remove(key);
    		ArmorMap.remove(key);
    	}
    	
    	player.Class().UpdateInventory();
    }
}