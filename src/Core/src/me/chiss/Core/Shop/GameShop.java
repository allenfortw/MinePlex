package me.chiss.Core.Shop;

import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.actions.Return;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.currency.ItemTokenHandler;
import me.chiss.Core.Shop.currency.SkillTokenHandler;
import me.chiss.Core.Shop.pagebuilder.game.CustomBuildPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.ItemPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.SkillsPageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.WeaponPageBuilder;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.minecraft.game.classcombat.shop.page.ArmorPageBuilder;
import mineplex.minecraft.game.core.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GameShop extends Shop 
{
    public GameShop(JavaPlugin plugin, CoreClientManager manager, ClassManager gameClassFactory, SkillFactory skillFactory) 
    {
        super(plugin, manager);
 
        ICurrencyHandler itemTokenHandler = new ItemTokenHandler();
        ICurrencyHandler skillTokenHandler = new SkillTokenHandler();
        
        AddPageBuilder(new ArmorPageBuilder(this, "-----------Class-------Skills", gameClassFactory));
        AddPageBuilder(new CustomBuildPageBuilder(this, "Class------Custom------Skills", gameClassFactory));
        AddPageBuilder(new SkillsPageBuilder(this,"Class-------Skills----Weapons", skillFactory, gameClassFactory));
        
        CreateShopActions();
    }

    public void GiveDefaultBuild(Player player)
    {
        CoreClient gamePlayer = ClientManager.Get(player);
        String key = gamePlayer.GetPlayerName();
        InventoryMap.put(key, player.getInventory().getContents());
        
        if (!PageMap.containsKey(key))
        {
            BuildPagesForPlayer(gamePlayer);
        }
        
        ShowSkillHotBarForPlayer(gamePlayer);
        
        /* HARDCODE FTW */
        GetPage(gamePlayer).PrepSlotsForPlayer(gamePlayer);
        GetPage(gamePlayer).PlayerWants(gamePlayer, 11);
    }
    
    @Override
    protected void CreateShopActions()
    {
        super.CreateShopActions();
        new Return(Plugin.GetPlugin(), this, ClientManager, _weaponFactory, _itemFactory);
    }
    
    @Override
    public boolean ShouldOpenShop(Block clickedBlock) 
    {
		return super.ShouldOpenShop(clickedBlock) || clickedBlock.getType() == Material.ENCHANTMENT_TABLE;
	}
}