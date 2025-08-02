package me.chiss.Core.Shop.salespackage;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import me.chiss.Core.ClientData.IClientClass;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.game.core.classcombat.Skill.ISkill;
import net.minecraft.server.v1_6_R2.IInventory;

public class SkillPackage implements ISalesPackage
{
    private ShopItem _skillItem;
    private ISkill _skill;
    private int _level;
    private boolean _active;
    private boolean _locked;
    
    public SkillPackage(ISkill skill, String[] lore, boolean active, int level, boolean locked)
    {
        _skill = skill;
        _active = active;
        _level = level;
        _locked = locked;

        _skillItem = new ShopItem((_locked ? Material.BOOK : _active ? Material.WRITTEN_BOOK : Material.BOOK), _skill.GetName(), null, _level, _locked);    
        _skillItem.SetLore(lore);
    }

    @Override
    public String GetName()
    {
    	return _skillItem.GetName();
    }
    
    @Override
    public int GetSalesPackageId()
    {
    	return _skill.GetSalesPackageId();
    }
    
    public ISkill GetSkill()
    {
        return _skill;
    }
    
    public int GetCreditCost()
    {
        return _skill.GetCreditCost();
    }
    
    public int GetPointCost()
    {
        return _skill.GetPointCost();
    }
    
    @Override
    public boolean IsFree()
    {
    	return _skill.IsFree(_level);
    }
    
    @Override
    public boolean CanFitIn(CoreClient player)
    {
        if (_locked && !IsFree())
            return false;
        
        ISkill existingSkill = player.Class().GetSkillByType(_skill.GetSkillType());
        
        if (existingSkill == _skill)
        {
            return player.Class().GetSkillLevel(existingSkill) < _skill.GetMaxLevel();
        }
        
        return true;
    }

    @Override 
    public void DeliverTo(IClientClass player)
    {
    }
    
    @Override 
    public void DeliverTo(IClientClass player, int slot)
    {
        player.GetInventory().setItem(slot, _skillItem);
    }
    
    @Override
    public void PurchaseBy(CoreClient player)
    {
        ISkill skill = GetSkill();
        
        ISkill existingSkill = player.Class().GetSkillByType(skill.GetSkillType());
        
        if (existingSkill != null)
        {
            player.Class().RemoveSkill(existingSkill);
        }
        
        player.Class().AddSkill(skill, _skillItem.getAmount());
        
        DeliverTo(player.Class());
    }
    
    public int ReturnAllLevels(CoreClient player)
    {
        if (player.Class().GetSkillByType(GetSkill().GetSkillType()) != null && player.Class().GetSkillByType(GetSkill().GetSkillType()) == GetSkill())
        {
            player.Class().RemoveSkill(GetSkill());
            
            return _skillItem.getAmount();
        }
        
        return 0;
    }

    @Override
    public int ReturnFrom(CoreClient player)
    {
        if (player.Class().GetSkillByType(GetSkill().GetSkillType()) != null && player.Class().GetSkillByType(GetSkill().GetSkillType()) == GetSkill())
        {
            player.Class().RemoveSkill(GetSkill());
            
            if (_skillItem.getAmount() > 1)
                player.Class().AddSkill(GetSkill(), _skillItem.getAmount() - 1);
            
            return 1;
        }
        
        return 0;
    }

    @Override
    public List<Integer> AddToCategory(IInventory inventory, int slot)
    {
        inventory.setItem(slot, _skillItem.getHandle());
        return Arrays.asList(slot);
    }

    public int GetLevel()
    {
        return _level;
    }

    public boolean IsActive()
    {
        return _active;
    }
}
