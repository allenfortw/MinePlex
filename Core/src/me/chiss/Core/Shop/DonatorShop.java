package me.chiss.Core.Shop;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Plugin.IPlugin;
import me.chiss.Core.Shop.actions.ChangeCurrency;
import me.chiss.Core.Shop.actions.NextPage;
import me.chiss.Core.Shop.actions.PreviousPage;
import me.chiss.Core.Shop.actions.Purchase;
import me.chiss.Core.Shop.currency.CreditHandler;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.currency.PointHandler;
import me.chiss.Core.Shop.pagebuilder.game.purchase.ArmorPurchasePageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.purchase.ItemPurchasePageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.purchase.SkillsPurchasePageBuilder;
import me.chiss.Core.Shop.pagebuilder.game.purchase.WeaponPurchasePageBuilder;
import me.chiss.Core.Skill.ISkillFactory;
import me.chiss.Core.Weapon.IWeaponFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.server.IRepository;
import mineplex.minecraft.game.core.classcombat.item.IItemFactory;

import org.bukkit.Material;

public class DonatorShop extends Shop 
{    
    public DonatorShop(IPlugin plugin, IRepository repository, CoreClientManager manager, IClassFactory gameClassFactory, ISkillFactory skillFactory, IWeaponFactory weaponFactory, IItemFactory itemFactory) 
    {
        super(plugin, repository, manager);
        
        ShopBlockType = Material.ENDER_CHEST;
        
        ICurrencyHandler creditHandler = new CreditHandler();
        ICurrencyHandler pointHandler = new PointHandler();
        
        AddPageBuilder(new ArmorPurchasePageBuilder(this, "-----------Class-------Skills", gameClassFactory, creditHandler, pointHandler));
        AddPageBuilder(new SkillsPurchasePageBuilder(this, "Class-------Skills----Weapons", skillFactory, gameClassFactory, creditHandler, pointHandler));
        AddPageBuilder(new WeaponPurchasePageBuilder(this, "Skills------Weapons-----Items", weaponFactory, gameClassFactory, creditHandler, pointHandler));
        AddPageBuilder(new ItemPurchasePageBuilder(this, "Weapons-----Items----------", itemFactory, creditHandler, pointHandler));
        
        CreateShopActions();
        
        RestoreInventory = true;
    }
    
    @Override
    protected void CreateShopActions()
    {
        new NextPage(Plugin.GetPlugin(), this, ClientManager);
        new PreviousPage(Plugin.GetPlugin(), this, ClientManager);
        new Purchase(Plugin.GetPlugin(), this, ClientManager);
        new ChangeCurrency(Plugin.GetPlugin(), this, ClientManager);
    }
    
    @Override
    public void ShowSkillHotBarForPlayer(CoreClient player) { }
}
