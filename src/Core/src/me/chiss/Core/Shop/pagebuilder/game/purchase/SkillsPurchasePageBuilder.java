package me.chiss.Core.Shop.pagebuilder.game.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chiss.Core.Class.IClassFactory;
import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.purchase.SkillsPurchasePage;
import me.chiss.Core.Shop.pagebuilder.game.SkillsPageBuilder;
import me.chiss.Core.Shop.salespackage.DonationPackage;
import me.chiss.Core.Shop.salespackage.SkillPackage;
import me.chiss.Core.Skill.ISkill;
import me.chiss.Core.Skill.ISkillFactory;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.shop.item.ISalesPackage;

public class SkillsPurchasePageBuilder extends SkillsPageBuilder
{
    public SkillsPurchasePageBuilder(Shop shop, String title, ISkillFactory skillFactory, IClassFactory gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
    	super(shop, title, skillFactory, gameClassFactory, currencyHandlers);
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        IShopPage category = new SkillsPurchasePage(Shop, Title, CurrencyHandlers, 
        										ClassUnlockedSalesPackageMap, GlobalUnlockedSalesPackageMap,
        										ClassLockedSalesPackageMap, GlobalLockedSalesPackageMap);
        
        return category;
    }

    @Override
    protected void BuildSkillPackages(ISkill skill, List<ISalesPackage> unlockedSkillPackages, List<ISalesPackage> lockedSkillPackages)
    {
    	List<String> skillLore = new ArrayList<String>();
    	
    	skillLore.add(C.cYellow + skill.GetCreditCost() + "c" + C.cWhite + " or " + C.cYellow + skill.GetPointCost() + "p " + C.cWhite + "per Level");
    	skillLore.add(C.cWhite + "You own " + C.cYellow + "0/" + skill.GetMaxLevel() + C.cWhite + " Levels");
    	skillLore.add(C.cBlack);
    	
    	skillLore.addAll(Arrays.asList(skill.GetDesc()));
    	
    	for (int i = 2; i < skillLore.size(); i++)
    	{
    		skillLore.set(i, C.cGray + skillLore.get(i));
    	}
    	
        lockedSkillPackages.add(new DonationPackage(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), false, 1, true)));
        unlockedSkillPackages.add(new DonationPackage(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), false, 1, false)));
        
        for (int i = 1; i <= skill.GetMaxLevel(); i++)
        {
        	skillLore.set(1, C.cWhite + "You own " + C.cYellow + i + "/" + skill.GetMaxLevel() + C.cWhite + " Levels");
            lockedSkillPackages.add(new DonationPackage(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), true, i, false)));
            unlockedSkillPackages.add(new DonationPackage(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), true, i, false)));
        }
    }
}
