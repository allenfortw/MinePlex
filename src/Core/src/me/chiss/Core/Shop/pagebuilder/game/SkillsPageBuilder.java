package me.chiss.Core.Shop.pagebuilder.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Shop.Shop;
import me.chiss.Core.Shop.currency.ICurrencyHandler;
import me.chiss.Core.Shop.page.IShopPage;
import me.chiss.Core.Shop.page.game.SkillsPage;
import me.chiss.Core.Shop.pagebuilder.IPageBuilder;
import me.chiss.Core.Shop.salespackage.ISalesPackage;
import me.chiss.Core.Shop.salespackage.SkillPackage;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.classcombat.Class.IClassFactory;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.core.classcombat.Skill.ISkillFactory;
import mineplex.minecraft.game.core.classcombat.Skill.SkillFactory;

public class SkillsPageBuilder implements IPageBuilder
{
    private SkillFactory _skillFactory;
    private ClassManager _gameClassFactory;
    
	protected Shop Shop;
    protected String Title;

    protected HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> ClassLockedSalesPackageMap;
    protected HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>> ClassUnlockedSalesPackageMap;
    
    protected HashMap<Integer, List<ISalesPackage>> GlobalLockedSalesPackageMap;
    protected HashMap<Integer, List<ISalesPackage>> GlobalUnlockedSalesPackageMap;  
    
    protected List<ICurrencyHandler> CurrencyHandlers;
    
    public SkillsPageBuilder(Shop shop, String title, SkillFactory skillFactory, ClassManager gameClassFactory, ICurrencyHandler...currencyHandlers)
    {
        Shop = shop;
        CurrencyHandlers = Arrays.asList(currencyHandlers);
        Title = title;
        _skillFactory = skillFactory;
        _gameClassFactory = gameClassFactory;
        
        ClassLockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>>();
        ClassUnlockedSalesPackageMap = new HashMap<IPvpClass, HashMap<Integer, List<ISalesPackage>>>();
        
        GlobalLockedSalesPackageMap = new HashMap<Integer, List<ISalesPackage>>();
        GlobalUnlockedSalesPackageMap = new HashMap<Integer, List<ISalesPackage>>();
        
        BuildClassSkills();
        BuildGlobalSkills();
    }

    @Override
    public IShopPage BuildForPlayer(CoreClient player)
    {
        IShopPage category = new SkillsPage(Shop, Title, CurrencyHandlers, 
        										ClassUnlockedSalesPackageMap, GlobalUnlockedSalesPackageMap,
        										ClassLockedSalesPackageMap, GlobalLockedSalesPackageMap);
        
        return category;
    }
    
    private void BuildClassSkills()
    {
        int slotNumber = 53;
        
        for (IPvpClass gameClass : _gameClassFactory.GetGameClasses())
        {            
            HashMap<Integer, List<ISalesPackage>> lockedClassMap = new HashMap<Integer, List<ISalesPackage>>();
            HashMap<Integer, List<ISalesPackage>> unlockedClassMap = new HashMap<Integer, List<ISalesPackage>>();            
            
            int swordSlotNumber = 10;
            int axeSlotNumber = 19;
            int bowSlotNumber = 28;
            int passiveASlotNumber = 37;
            int passiveBSlotNumber = 46;
            
            for (ISkill skill : _skillFactory.GetSkillsFor(gameClass))
            {                
                switch (skill.GetSkillType())
                {
                    case Sword:
                        slotNumber = swordSlotNumber;
                        swordSlotNumber++;
                        break;
                    case Axe:
                        slotNumber = axeSlotNumber;
                        axeSlotNumber++;
                        break;
                    case Bow:
                        slotNumber = bowSlotNumber;
                        bowSlotNumber++;
                        break;
                    case PassiveA:
                        slotNumber = passiveASlotNumber;
                        passiveASlotNumber++;
                        break;
                    case PassiveB:
                        slotNumber = passiveBSlotNumber;
                        passiveBSlotNumber++;
                        break;

                    default:
                        continue;
                }
                
                List<ISalesPackage> unlockedskillPackages = new ArrayList<ISalesPackage>(skill.GetMaxLevel());
                List<ISalesPackage> lockedSkillPackages = new ArrayList<ISalesPackage>(skill.GetMaxLevel());
                
                BuildSkillPackages(skill, unlockedskillPackages, lockedSkillPackages);
                
                lockedClassMap.put(slotNumber, lockedSkillPackages);
                unlockedClassMap.put(slotNumber, unlockedskillPackages);
            }
            
            ClassLockedSalesPackageMap.put(gameClass, lockedClassMap);
            ClassUnlockedSalesPackageMap.put(gameClass, unlockedClassMap);
        }
    }
    
    private void BuildGlobalSkills()
    {
        int slotNumber = 53;  
        
        int passiveCSlotNumber = 10;
        int passiveDSlotNumber = 19;
        int passiveESlotNumber = 28;
            
        for (ISkill skill : _skillFactory.GetGlobalSkills())
        {                
            switch (skill.GetSkillType())
            {
                case PassiveC:
                    slotNumber = passiveCSlotNumber;
                    passiveCSlotNumber++;
                    break;
                case PassiveD:
                    slotNumber = passiveDSlotNumber;
                    passiveDSlotNumber++;
                    break;
                case PassiveE:
                    slotNumber = passiveESlotNumber;
                    passiveESlotNumber++;
                    break;

                default:
                    continue;
            }
            
            List<ISalesPackage> unlockedskillPackages = new ArrayList<ISalesPackage>(skill.GetMaxLevel());
            List<ISalesPackage> lockedSkillPackages = new ArrayList<ISalesPackage>(skill.GetMaxLevel());
                        
            BuildSkillPackages(skill, unlockedskillPackages, lockedSkillPackages);
            
            GlobalLockedSalesPackageMap.put(slotNumber, lockedSkillPackages);
            GlobalUnlockedSalesPackageMap.put(slotNumber, unlockedskillPackages); 
        }
    }
    
    protected void BuildSkillPackages(ISkill skill, List<ISalesPackage> unlockedSkillPackages, List<ISalesPackage> lockedSkillPackages)
    {	
    	List<String> skillLore = new ArrayList<String>();
    	
    	skillLore.add(C.cYellow + skill.GetTokenCost() + " Tokens" + C.cWhite + " per Level");
    	skillLore.add(C.cWhite + "Equipped " + C.cYellow + "0/" + skill.GetMaxLevel() + C.cWhite + " Levels");
    	// skillLore.add(C.cWhite + "You own " + C.cYellow + levelOwned + "/" + skill.GetMaxLevel() + C.cWhite + " Levels");
    	skillLore.add(C.cBlack);
    	
    	skillLore.addAll(Arrays.asList(skill.GetDesc()));
    	
    	for (int i = 2; i < skillLore.size(); i++)
    	{
    		skillLore.set(i, C.cGray + skillLore.get(i));
    	}
    	
        lockedSkillPackages.add(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), false, 1, true));
        unlockedSkillPackages.add(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), false, 1, false));
        
        for (int i = 1; i <= skill.GetMaxLevel(); i++)
        {
        	skillLore.set(1, C.cWhite + "Equipped " + C.cYellow + i + "/" + skill.GetMaxLevel() + C.cWhite + " Levels");
        	// skillLore.add(C.cWhite + "You own " + C.cYellow + levelOwned + "/" + skill.GetMaxLevel() + C.cWhite + " Levels");
        	lockedSkillPackages.add(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), true, i, true));
            unlockedSkillPackages.add(new SkillPackage(skill, skillLore.toArray(new String[skillLore.size()]), true, i, false));
        }
    }
}
