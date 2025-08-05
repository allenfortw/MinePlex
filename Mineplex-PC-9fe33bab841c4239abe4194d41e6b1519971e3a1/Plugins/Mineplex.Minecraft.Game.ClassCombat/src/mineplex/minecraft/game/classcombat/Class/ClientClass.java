package mineplex.minecraft.game.classcombat.Class;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.SlotToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class ClientClass
{
	private ClassManager _classFactory;
	private SkillFactory _skillFactory;
	private CoreClient _client;
	private Donor _donor;
	
	private IPvpClass _gameClass;
	private NautHashMap<SkillType, ISkill> _skillMap = new NautHashMap<SkillType, ISkill>();
	
	private IPvpClass _lastClass;
	private NautHashMap<Integer, ItemStack> _lastItems = new NautHashMap<Integer, ItemStack>();
	private ItemStack[] _lastArmor = new ItemStack[4];
	private NautHashMap<SkillType, ISkill> _lastSkillMap = new NautHashMap<SkillType, ISkill>();
	
    private NautHashMap<IPvpClass, HashMap<Integer, CustomBuildToken>> _customBuilds;
    private NautHashMap<IPvpClass, CustomBuildToken> _activeCustomBuilds;
    
    private CustomBuildToken _savingCustomBuild;
    
    public ClientClass(ClassManager classFactory, SkillFactory skillFactory, CoreClient client, Donor donor, ClientClassToken token)
    {
    	_classFactory = classFactory;
    	_skillFactory = skillFactory;
    	_client = client;
    	_donor = donor;
    	
    	Load(token);
    }
    
    public void Load(ClientClassToken token)
    {
        _customBuilds = new NautHashMap<IPvpClass, HashMap<Integer, CustomBuildToken>>();
        _activeCustomBuilds = new NautHashMap<IPvpClass, CustomBuildToken>();
        
	    for (IPvpClass pvpClass : _classFactory.GetAllClasses())
	    {
	    	_customBuilds.put(pvpClass, new HashMap<Integer, CustomBuildToken>());
	    }
	    
	    for (CustomBuildToken buildToken : token.CustomBuilds)
	    {
	    	IPvpClass pvpClass = _classFactory.GetClass(buildToken.PvpClassId);
	    	
	    	ISkill swordSkill = _skillFactory.GetSkillBySalesPackageId(buildToken.SwordSkillId);
	    	ISkill axeSkill = _skillFactory.GetSkillBySalesPackageId(buildToken.AxeSkillId);
	    	ISkill bowSkill = _skillFactory.GetSkillBySalesPackageId(buildToken.BowSkillId);
	    	ISkill classPassiveASkill = _skillFactory.GetSkillBySalesPackageId(buildToken.ClassPassiveASkillId);
	    	ISkill classPassiveBSkill = _skillFactory.GetSkillBySalesPackageId(buildToken.ClassPassiveBSkillId);
	    	ISkill globalPassive = _skillFactory.GetSkillBySalesPackageId(buildToken.GlobalPassiveSkillId);
	    	
	    	if (!ValidSkill(buildToken.SwordSkillId, swordSkill, SkillType.Sword))
	    		continue;
	    	
	    	if (!ValidSkill(buildToken.AxeSkillId, axeSkill, SkillType.Axe))
	    		continue;
	    	
	    	if (!ValidSkill(buildToken.BowSkillId, bowSkill, SkillType.Bow))
	    		continue;
	    	
	    	if (!ValidSkill(buildToken.ClassPassiveASkillId, classPassiveASkill, SkillType.PassiveA))
	    		continue;
	    	
	    	if (!ValidSkill(buildToken.ClassPassiveBSkillId, classPassiveBSkill, SkillType.PassiveB)) 
	    		continue;
	    	
	    	if (!ValidSkill(buildToken.GlobalPassiveSkillId, globalPassive, SkillType.GlobalPassive))
	    		continue;
	    	
	    	_customBuilds.get(pvpClass).put(buildToken.CustomBuildNumber, buildToken);
	    }
    }
    
	public NautHashMap<Integer, ItemStack> GetDefaultItems()
	{
		return _lastItems;
	}

	public void SetDefaultHead(ItemStack armor)
	{
		_lastArmor[3] = armor;
	}

	public void SetDefaultChest(ItemStack armor)
	{
		_lastArmor[2] = armor;
	}

	public void SetDefaultLegs(ItemStack armor)
	{
		_lastArmor[1] = armor;
	}

	public void SetDefaultFeet(ItemStack armor)
	{
		_lastArmor[0] = armor;
	}
    
	public void SaveActiveCustomBuild() 
	{
		if (GetGameClass() == null)
			return;
		
		_savingCustomBuild.PvpClassId = GetGameClass().GetSalesPackageId();
		_savingCustomBuild.PlayerName = _client.GetPlayerName();
		
		ISkill swordSkill = GetSkillByType(SkillType.Sword);
		
		if (swordSkill != null)
			_savingCustomBuild.SwordSkillId = swordSkill.GetSalesPackageId();
		else
			_savingCustomBuild.SwordSkillId = -1;
		
		ISkill axeSkill = GetSkillByType(SkillType.Axe);
		
		if (axeSkill != null)
			_savingCustomBuild.AxeSkillId = axeSkill.GetSalesPackageId();
		else
			_savingCustomBuild.AxeSkillId = -1;
		
		ISkill bowSkill = GetSkillByType(SkillType.Bow);
		
		if (bowSkill != null)
			_savingCustomBuild.BowSkillId = bowSkill.GetSalesPackageId();
		else
			_savingCustomBuild.BowSkillId = -1;
		
		ISkill passiveASkill = GetSkillByType(SkillType.PassiveA);
		
		if (passiveASkill != null)
			_savingCustomBuild.ClassPassiveASkillId = passiveASkill.GetSalesPackageId();
		else
			_savingCustomBuild.ClassPassiveASkillId = -1;
		
		
		ISkill passiveBSkill = GetSkillByType(SkillType.PassiveB);
		
		if (passiveBSkill != null)
			_savingCustomBuild.ClassPassiveBSkillId = passiveBSkill.GetSalesPackageId();
		else
			_savingCustomBuild.ClassPassiveBSkillId = -1;
		
		ISkill globalPassiveSkill = GetSkillByType(SkillType.GlobalPassive);
		
		if (globalPassiveSkill != null)
			_savingCustomBuild.GlobalPassiveSkillId = globalPassiveSkill.GetSalesPackageId();
		else
			_savingCustomBuild.GlobalPassiveSkillId = -1;
		
		_savingCustomBuild.Slots = new ArrayList<SlotToken>(9);	
		
		_classFactory.GetRepository().SaveCustomBuild(_savingCustomBuild);
		_savingCustomBuild = null;
	}

	public void SetSavingCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
	{
		_savingCustomBuild = customBuild;
		_customBuilds.get(pvpClass).put(_savingCustomBuild.CustomBuildNumber, _savingCustomBuild);
	}
	
	public void SetActiveCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
	{
		customBuild.Active = true;
		_activeCustomBuilds.put(pvpClass, customBuild);
	}
	
	public CustomBuildToken GetActiveCustomBuild(IPvpClass pvpClass)
	{
		return _activeCustomBuilds.get(pvpClass);
	}
	
	public CustomBuildToken GetSavingCustomBuild() 
	{
		return _savingCustomBuild;
	}

	public boolean IsSavingCustomBuild() 
	{
		return _savingCustomBuild != null;
	}
	
    public HashMap<Integer, CustomBuildToken> GetCustomBuilds(IPvpClass pvpClass)
    {
    	return _customBuilds.get(pvpClass);
    }
    
	public void EquipCustomBuild(CustomBuildToken customBuild) 
	{
		EquipCustomBuild(customBuild, true);
	}
    
	public void EquipCustomBuild(CustomBuildToken customBuild, boolean notify) 
	{
		_lastClass = _classFactory.GetClass(customBuild.PvpClassId);

		if (_lastClass == null)
			return;

		_lastSkillMap.remove(SkillType.Class);

		SetDefaultHead(ItemStackFactory.Instance.CreateStack(_lastClass.GetHead()));
		SetDefaultChest(ItemStackFactory.Instance.CreateStack(_lastClass.GetChestplate()));
		SetDefaultLegs(ItemStackFactory.Instance.CreateStack(_lastClass.GetLeggings()));
		SetDefaultFeet(ItemStackFactory.Instance.CreateStack(_lastClass.GetBoots()));
		
		if (customBuild.SwordSkillId != -1)
			_lastSkillMap.put(SkillType.Sword, _skillFactory.GetSkillBySalesPackageId(customBuild.SwordSkillId));
		else
			_lastSkillMap.remove(SkillType.Sword);

		if (customBuild.AxeSkillId != -1)
			_lastSkillMap.put(SkillType.Axe, _skillFactory.GetSkillBySalesPackageId(customBuild.AxeSkillId));
		else
			_lastSkillMap.remove(SkillType.Axe);

		if (customBuild.BowSkillId != -1)
			_lastSkillMap.put(SkillType.Bow, _skillFactory.GetSkillBySalesPackageId(customBuild.BowSkillId));
		else
			_lastSkillMap.remove(SkillType.Bow);

		if (customBuild.ClassPassiveASkillId != -1)
			_lastSkillMap.put(SkillType.PassiveA, _skillFactory.GetSkillBySalesPackageId(customBuild.ClassPassiveASkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveA);

		if (customBuild.ClassPassiveBSkillId != -1)
			_lastSkillMap.put(SkillType.PassiveB, _skillFactory.GetSkillBySalesPackageId(customBuild.ClassPassiveBSkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveB);

		if (customBuild.GlobalPassiveSkillId != -1)
			_lastSkillMap.put(SkillType.GlobalPassive, _skillFactory.GetSkillBySalesPackageId(customBuild.GlobalPassiveSkillId));
		else
			_lastSkillMap.remove(SkillType.GlobalPassive);

		ResetToDefaults(true, false);

		if (notify)
		{
			ListSkills(_client.GetPlayer());
			_client.GetPlayer().getWorld().playSound(_client.GetPlayer().getLocation(), Sound.LEVEL_UP, 1f, 1f);

			_client.GetPlayer().sendMessage(F.main("Class", "You equipped " + F.skill(customBuild.Name) + "."));
		}
	}

	public void ListSkills(Player caller) 
	{
		UtilPlayer.message(caller, F.main("Skill", "Listing Class Skills:"));

		for (SkillType type : SkillType.values())
			if (caller.isOp() || type != SkillType.Class)
				if (_skillMap.containsKey(type))
					UtilPlayer.message(caller, F.desc(type.toString(), _skillMap.get(type).GetName()));
	}
	
	public void ResetSkills(Player player)
	{
		for (ISkill skill : GetSkills())
		{
			skill.Reset(player);
		}
	}
	
	public void ResetToDefaults(boolean equipItems, boolean equipDefaultArmor)
	{
		if (_lastClass == null)
		{
			_lastClass = _classFactory.GetClass("Knight");
			
			_lastArmor[3] = ItemStackFactory.Instance.CreateStack(_lastClass.GetHead());
			_lastArmor[2] = ItemStackFactory.Instance.CreateStack(_lastClass.GetChestplate());
			_lastArmor[1] = ItemStackFactory.Instance.CreateStack(_lastClass.GetLeggings());
			_lastArmor[0] = ItemStackFactory.Instance.CreateStack(_lastClass.GetBoots());
			
			for (ISkill skill : _lastClass.GetDefaultSkills().keySet())
			{
				if (skill.GetSkillType() != SkillType.Class)
					_lastSkillMap.put(skill.GetSkillType(), skill);
			}
		}
		
		SetGameClass(_lastClass);
		
		if (equipDefaultArmor)
		{
			if (_lastArmor[3] != null)
				_client.GetPlayer().getInventory().setHelmet(_lastArmor[3].clone());
	
			if (_lastArmor[2] != null)
				_client.GetPlayer().getInventory().setChestplate(_lastArmor[2].clone());
	
			if (_lastArmor[1] != null)
				_client.GetPlayer().getInventory().setLeggings(_lastArmor[1].clone());
	
			if (_lastArmor[0] != null)
				_client.GetPlayer().getInventory().setBoots(_lastArmor[0].clone());
		}
		
		if (equipItems)
		{
			PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD), 0);
			PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE), 1);

			for (int i = 2; i < 9; i++)
			{
				PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP), i);
			}
			
			if (_gameClass.GetType() == ClassType.Assassin || _gameClass.GetType() == ClassType.Ranger)
			{
				PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.BOW), 2);
				PutDefaultItem( ItemStackFactory.Instance.CreateStack(Material.ARROW, _gameClass.GetType() == ClassType.Assassin ? 16 : 32), 3);
			}
			else
			{
				if (_gameClass.GetType() != ClassType.Mage)
				{
					PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.POTION), 8);
				}
				else
				{
					PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.WEB, 2), 8);
				}
			}
			
			_client.GetPlayer().getInventory().clear();
			
			for (Entry<Integer, ItemStack> defaultItem : GetDefaultItems().entrySet())
			{
				_client.GetPlayer().getInventory().setItem(defaultItem.getKey(), defaultItem.getValue());
			}
		}

		ClearSkills();
		
		for (ISkill cur : _gameClass.GetDefaultSkills().keySet())
		{
			if (cur.GetSkillType() == SkillType.Class)
				AddSkill(cur);
		}
		
		for (ISkill skill : _lastSkillMap.values())
			AddSkill(skill); 
	}
    
	public void ClearSkills()
	{
		if (_skillMap != null)
			for (ISkill skill : _skillMap.values())
				skill.RemoveUser(_client.GetPlayer());

		_skillMap.clear();
	}
	
	public void ClearDefaultSkills()
	{
		_lastSkillMap = new NautHashMap<SkillType, ISkill>();
	}
    
	public void SetGameClass(IPvpClass gameClass) 
	{
		ClearSkills();

		_gameClass = gameClass;

		if (_gameClass == null)
			return;

		//Load Saved
		if (_classFactory.GetRestore().IsActive())
		{
			Collection<ISkill> skills = _classFactory.GetRestore().GetBuild(_client.GetPlayerName(), gameClass);

			if (skills != null)
			{
				for (ISkill skill : skills)
					AddSkill(skill);
				
				//Inform
				UtilPlayer.message(_client.GetPlayer(), F.main("Class", "Armor Class: " +  F.oo(_gameClass.GetName(), true)));
				return;
			}
		}

		for (ISkill cur : gameClass.GetDefaultSkills().keySet())
		{
			if (cur.GetSkillType() == SkillType.Class)
				AddSkill(cur);
		}

		//Inform
		UtilPlayer.message(_client.GetPlayer(), F.main("Class", "Armor Class: " +  F.oo(_gameClass.GetName(), true)));

	}
    
	public IPvpClass GetGameClass() 
	{
		return _gameClass;
	}
    
	public boolean IsGameClass(ClassType type) 
	{
		if (GetGameClass() == null)
			return false;

		return GetGameClass().GetType() == type;
	}
	
	public Collection<ISkill> GetSkills() 
	{
		if (_skillMap == null)
			_skillMap = new NautHashMap<SkillType, ISkill>();

		return _skillMap.values();
	}
	
	public Collection<ISkill> GetDefaultSkills() 
	{
		return _lastSkillMap.values();
	}

	public ISkill GetSkillByType(SkillType skillType)
	{
		if (_skillMap == null)
			_skillMap = new NautHashMap<SkillType, ISkill>();

		if (_skillMap.containsKey(skillType))
			return _skillMap.get(skillType);

		return null;
	}
	
	public void AddSkill(ISkill skill) 
	{
		if (_skillMap == null)
			_skillMap = new NautHashMap<SkillType, ISkill>();

		if (_skillMap.get(skill.GetSkillType()) != null)
			_skillMap.get(skill.GetSkillType()).RemoveUser(_client.GetPlayer());

		_skillMap.put(skill.GetSkillType(), skill);
		_lastSkillMap.put(skill.GetSkillType(), skill);
		
		skill.AddUser(_client.GetPlayer());
		
		//Save
		if (_classFactory.GetRestore().IsActive())		
			_classFactory.GetRestore().SaveBuild(_client.GetPlayerName(), _gameClass, GetSkills());
	}

	public void RemoveSkill(ISkill skill) 
	{
		if (skill == null)
			return;
		
		if (_skillMap == null)
			return;

		_skillMap.remove(skill.GetSkillType());

		skill.RemoveUser(_client.GetPlayer());
	}

	public ItemStack[] GetDefaultArmor()
	{
		return _lastArmor;
	}

	public void PutDefaultItem(ItemStack value, Integer key)
	{
		_lastItems.put(key, value);
	}

	public void ClearDefaults()
	{
		_lastItems.clear();
		_lastArmor = new ItemStack[4];
		_lastSkillMap.clear();
	}
	
	private boolean ValidSkill(int skillId, ISkill skill, SkillType expectedType)
	{
    	if (skillId != -1 && (skill == null || skill.GetSkillType() != expectedType || (!skill.IsFree() && !_donor.Owns(skillId) && !_client.GetRank().Has(Rank.ULTRA) && !_donor.OwnsUnknownPackage("Competitive ULTRA"))))
    		return false;
    	
    	return true;
	}
}
