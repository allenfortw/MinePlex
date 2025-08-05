package me.chiss.Core.ClientData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.Skill.ISkill;
import me.chiss.Core.Skill.ISkill.SkillType;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface IClientClass
{
    String GetName();
    
    void SetGameClass(IPvpClass gameClass);
    void SetGameClass(IPvpClass gameClass, boolean addDefaultSkills);
    IPvpClass GetGameClass();
    
    void AddSkill(ISkill skill, int level); 
    void RemoveSkill(ISkill getSkill);
    
    Collection<Entry<ISkill, Integer>> GetSkills();
    Collection<Entry<ISkill, Integer>> GetDefaultSkills();
    ISkill GetSkillByType(SkillType skillType);
    void ResetSkills();
	void ClearSkills();
	void ClearDefaultSkills();
	
	int GetSkillLevel(ISkill skill);
        
    PlayerInventory GetInventory();
    void OpenInventory(Inventory skillsCategory);
    void CloseInventory();
    void UpdateInventory();

    void ClearDefaults();
    
    void AddDefaultArmor(ItemStack[] armorContents);
    void PutDefaultItem(ItemStack deliverable, int slot);
    HashMap<Integer, ItemStack> GetDefaultItems();
    
    ItemStack[] GetDefaultArmor();

    void SetDefaultHead(ItemStack armor);
    void SetDefaultChest(ItemStack armor);
    void SetDefaultLegs(ItemStack armor);
    void SetDefaultFeet(ItemStack armor);

    void ResetToDefaults(boolean equipItems, boolean equipDefaultArmor);
}
