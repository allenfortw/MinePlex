package me.chiss.Core.ClientData;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.chiss.Core.Class.IPvpClass;
import me.chiss.Core.Skill.ISkill;
import me.chiss.Core.Skill.ISkill.SkillType;
import mineplex.core.account.CoreClient;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.core.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.core.classcombat.Class.repository.token.CustomBuildToken;

public class ClientClass extends ClientDataBase<Object> implements IClientClass
{
	//Class and Skills
	private IPvpClass _gameClass;
	private HashMap<SkillType, Entry<ISkill, Integer>> _skillMap;

	//Temp
	private IPvpClass _lastClass;
	private ItemStack[] _lastArmor;
	private HashMap<Integer, ItemStack> _lastItems;
	private HashMap<SkillType, Entry<ISkill, Integer>> _lastSkillMap;

	public ClientClass(CoreClient client) 
	{
		super(client, "Class", null);

		_lastItems = new HashMap<Integer, ItemStack>();
		_lastArmor = new ItemStack[4];		
		_lastSkillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();
	}

	@Override
	public void Load() 
	{

	}

	@Override
	protected void LoadToken(Object token)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String GetName() 
	{
		return Client.GetPlayerName();
	}

	@Override
	public void ResetToDefaults(boolean equipItems, boolean equipDefaultArmor)
	{
		if (equipDefaultArmor)
		{
			if (_lastArmor[3] != null)
				GetInventory().setHelmet(_lastArmor[3].clone());
	
			if (_lastArmor[2] != null)
				GetInventory().setChestplate(_lastArmor[2].clone());
	
			if (_lastArmor[1] != null)
				GetInventory().setLeggings(_lastArmor[1].clone());
	
			if (_lastArmor[0] != null)
				GetInventory().setBoots(_lastArmor[0].clone());
		}
		
		if (equipItems)
		{
			for (Entry<Integer, ItemStack> defaultItem : _lastItems.entrySet())
			{
				GetInventory().setItem(defaultItem.getKey(), (defaultItem.getValue() == null ? new ItemStack(Material.AIR) : defaultItem.getValue().clone()));
			}
		}

		SetGameClass(_lastClass, false);

		NewSkills();
		for (Entry<SkillType, Entry<ISkill, Integer>> entry : _lastSkillMap.entrySet())
			AddSkill(entry.getValue().getKey(), entry.getValue().getValue());  
	}

	@Override
	public void SetGameClass(IPvpClass gameClass) 
	{
		SetGameClass(gameClass, true);
	}

	@Override
	public void SetGameClass(IPvpClass gameClass, boolean addDefaultSkills) 
	{
		ClearSkills();

		_gameClass = gameClass;

		if (_gameClass == null)
			return;

		//Load Saved
		if (Client.Manager.Classes().GetRestore().IsActive())
		{
			Collection<Entry<ISkill, Integer>> skills = Client.Manager.Classes().GetRestore().GetBuild(Client.GetPlayerName(), gameClass);

			if (skills != null)
			{
				for (Entry<ISkill, Integer> skill : skills)
					AddSkill(skill.getKey(), skill.getValue());
				
				//Inform
				UtilPlayer.message(Client.GetPlayer(), F.main("Class", "Armor Class: " +  F.oo(_gameClass.GetName(), true)));
				return;
			}
		}

		_lastClass = gameClass;

		for (ISkill cur : gameClass.GetDefaultSkills().keySet())
		{
			if (addDefaultSkills || cur.GetSkillType() == SkillType.Class)
				AddSkill(cur, gameClass.GetDefaultSkills().get(cur));
		}

		//Inform
		UtilPlayer.message(Client.GetPlayer(), F.main("Class", "Armor Class: " +  F.oo(_gameClass.GetName(), true)));
	}

	@Override
	public IPvpClass GetGameClass() 
	{
		return _gameClass;
	}

	@Override
	public void ClearDefaults()
	{
		_lastItems.clear();
		_lastArmor = new ItemStack[4];
		_lastSkillMap.clear();
	}

	public String GetGameClassTag()
	{
		IPvpClass gameClass = GetGameClass();
		if (gameClass == null)	return "";
		return gameClass.GetName().charAt(0)+".";
	}

	public void NewSkills()
	{
		ClearSkills();
		_skillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();
	}

	public void ClearSkills()
	{
		if (_skillMap != null)
			for (Entry<ISkill, Integer> entry : _skillMap.values())
				entry.getKey().RemoveUser(Client.GetPlayer());

		_skillMap = null;
	}
	
	public void AddSkill(String skillName, int level) 
	{
		ISkill skill = Client.Manager.Skills().GetSkill(skillName);
		if (skill == null)
			return;
		
		AddSkill(skill, level);
	}

	@Override
	public void AddSkill(ISkill skill, int level) 
	{
		if (level <= 0)
			return;

		if (_skillMap == null)
			_skillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();

		if (_skillMap.get(skill.GetSkillType()) != null)
			_skillMap.get(skill.GetSkillType()).getKey().RemoveUser(Client.GetPlayer());

		_skillMap.put(skill.GetSkillType(), new AbstractMap.SimpleEntry<ISkill, Integer>(skill, level));
		_lastSkillMap.put(skill.GetSkillType(), new AbstractMap.SimpleEntry<ISkill, Integer>(skill, level));

		skill.AddUser(Client.GetPlayer(), level);
		
		//Save
		if (Client.Manager.Classes().GetRestore().IsActive())		
			Client.Manager.Classes().GetRestore().SaveBuild(Client.GetPlayerName(), _gameClass, GetSkills());
	}

	@Override
	public void RemoveSkill(ISkill skill) 
	{
		if (skill == null)
			return;
		
		if (_skillMap == null)
			return;

		_skillMap.remove(skill.GetSkillType());
		_lastSkillMap.remove(skill.GetSkillType());

		skill.RemoveUser(Client.GetPlayer());
	}

	@Override
	public Collection<Entry<ISkill, Integer>> GetSkills() 
	{
		if (_skillMap == null)
			_skillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();

		return _skillMap.values();
	}

	@Override
	public Collection<Entry<ISkill, Integer>> GetDefaultSkills() 
	{
		return _lastSkillMap.values();
	}

	@Override
	public void ResetSkills()
	{
		for (Entry<ISkill, Integer> entry : GetSkills())
		{
			entry.getKey().Reset(Client.GetPlayer());
		}
	}

	public void ClearDefaultSkills()
	{
		_lastSkillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();
	}

	public boolean IsGameClass(ClassType type) 
	{
		if (GetGameClass() == null)
			return false;

		return GetGameClass().GetType() == type;
	}

	public ISkill GetSkillByType(SkillType skillType)
	{
		if (_skillMap == null)
			_skillMap = new HashMap<SkillType, Entry<ISkill, Integer>>();

		if (_skillMap.containsKey(skillType))
			return _skillMap.get(skillType).getKey();

		return null;
	}

	public int GetSkillLevel(ISkill skill)
	{
		//No Class
		if (_skillMap == null)
			return 0;

		if (_skillMap.containsKey(skill.GetSkillType()) && _skillMap.get(skill.GetSkillType()).getKey() == skill)
			return _skillMap.get(skill.GetSkillType()).getValue();

		return 0;
	}

	public void SetSkillLevel(ISkill skill, int level)
	{
		_skillMap.put(skill.GetSkillType(), new AbstractMap.SimpleEntry<ISkill, Integer>(skill, level));
	}

	@Override
	public PlayerInventory GetInventory() 
	{
		if (Client.GetPlayer() == null)
			return null;

		return Client.GetPlayer().getInventory();
	}

	@Override
	public void OpenInventory(Inventory skillsCategory) 
	{
		if (Client.GetPlayer() == null)
			return;

		Client.GetPlayer().openInventory(skillsCategory);
	}

	@Override
	public void CloseInventory() 
	{
		if (Client.GetPlayer() == null)
			return;

		Client.GetPlayer().closeInventory();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void UpdateInventory() 
	{
		if (Client.GetPlayer() == null)
			return;

		Client.GetPlayer().updateInventory();
	}

	public void ListSkills(Player caller) 
	{
		UtilPlayer.message(caller, F.main("Skill", "Listing Class Skills;"));

		for (SkillType type : SkillType.values())
			if (caller.isOp() || type != SkillType.Class)
				if (_skillMap.containsKey(type))
					UtilPlayer.message(caller, F.desc(type.toString(), _skillMap.get(type).getKey().GetName() + " " + F.elem(_skillMap.get(type).getValue() + "/" + _skillMap.get(type).getKey().GetMaxLevel())));
	}

	@Override
	public void AddDefaultArmor(ItemStack[] armorContents)
	{
		_lastArmor = armorContents;
	}

	@Override
	public void PutDefaultItem(ItemStack deliverable, int slot)
	{
		_lastItems.put(slot, deliverable);
	}

	@Override
	public ItemStack[] GetDefaultArmor()
	{
		if (_lastArmor[0] == null || _lastArmor[1] == null || _lastArmor[2] == null || _lastArmor[3] == null)
			return new ItemStack[4];

		ItemStack[] armorReturn = new ItemStack[4];

		armorReturn[3] = _lastArmor[3].clone();
		armorReturn[2] = _lastArmor[2].clone();
		armorReturn[1] = _lastArmor[1].clone();
		armorReturn[0] = _lastArmor[0].clone();

		return armorReturn;
	}

	@Override
	public HashMap<Integer, ItemStack> GetDefaultItems()
	{
		return _lastItems;
	}

	@Override
	public void SetDefaultHead(ItemStack armor)
	{
		_lastArmor[3] = armor;
	}

	@Override
	public void SetDefaultChest(ItemStack armor)
	{
		_lastArmor[2] = armor;
	}

	@Override
	public void SetDefaultLegs(ItemStack armor)
	{
		_lastArmor[1] = armor;
	}

	@Override
	public void SetDefaultFeet(ItemStack armor)
	{
		_lastArmor[0] = armor;
	}

	public void EquipCustomBuild(CustomBuildToken customBuild) 
	{
		EquipCustomBuild(customBuild, true, true);
	}

	public void EquipCustomBuild(CustomBuildToken customBuild, boolean equipItems, boolean equipDefaultArmor) 
	{
		_lastClass = Client.Manager.Classes().GetClass(customBuild.PvpClassId);

		if (_lastClass == null)
			return;

		_lastSkillMap.remove(SkillType.Class);

		if (equipDefaultArmor)
		{
			SetDefaultHead(ItemStackFactory.Instance.CreateStack(_lastClass.GetHead()));
			SetDefaultChest(ItemStackFactory.Instance.CreateStack(_lastClass.GetChestplate()));
			SetDefaultLegs(ItemStackFactory.Instance.CreateStack(_lastClass.GetLeggings()));
			SetDefaultFeet(ItemStackFactory.Instance.CreateStack(_lastClass.GetBoots()));
		}

		if (equipItems)
		{
			for (int i=0; i < 9; i++)
			{
				if (customBuild.Slots == null || customBuild.Slots.size() <= i || customBuild.Slots.get(i).Material == "AIR" || customBuild.Slots.get(i).Material == "NULL")
					_lastItems.put(i, null);
				else
				{
					/*
					if (ItemFactory.Instance.GetItem(customBuild.Slots.get(i).Material) != null)
						_lastItems.put(i, ItemFactory.Instance.GetItem(customBuild.Slots.get(i).Material).);
					*/
					_lastItems.put(i, ItemStackFactory.Instance.CreateStack(Material.getMaterial(customBuild.Slots.get(i).Material), customBuild.Slots.get(i).Amount));
				}
			}
		}

		if (customBuild.SwordSkillId != -1)
			_lastSkillMap.put(SkillType.Sword, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.SwordSkillId));
		else
			_lastSkillMap.remove(SkillType.Sword);

		if (customBuild.AxeSkillId != -1)
			_lastSkillMap.put(SkillType.Axe, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.AxeSkillId));
		else
			_lastSkillMap.remove(SkillType.Axe);

		if (customBuild.BowSkillId != -1)
			_lastSkillMap.put(SkillType.Bow, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.BowSkillId));
		else
			_lastSkillMap.remove(SkillType.Bow);

		if (customBuild.ClassPassiveASkillId != -1)
			_lastSkillMap.put(SkillType.PassiveA, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.ClassPassiveASkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveA);

		if (customBuild.ClassPassiveBSkillId != -1)
			_lastSkillMap.put(SkillType.PassiveB, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.ClassPassiveBSkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveB);

		if (customBuild.GlobalPassiveASkillId != -1)
			_lastSkillMap.put(SkillType.PassiveC, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.GlobalPassiveASkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveC);

		if (customBuild.GlobalPassiveBSkillId != -1)
			_lastSkillMap.put(SkillType.PassiveD, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.GlobalPassiveBSkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveD);

		if (customBuild.GlobalPassiveCSkillId != -1)
			_lastSkillMap.put(SkillType.PassiveE, Client.Manager.Skills().GetSkillBySalesPackageId(customBuild.GlobalPassiveCSkillId));
		else
			_lastSkillMap.remove(SkillType.PassiveE);

		ResetToDefaults(equipItems, equipDefaultArmor);

		Client.Donor().SetTokens(customBuild.SkillTokensBalance, customBuild.ItemTokensBalance);

		ListSkills(Client.GetPlayer());
		Client.GetPlayer().getWorld().playSound(Client.GetPlayer().getLocation(), Sound.LEVEL_UP, 1f, 1f);

		Client.GetPlayer().sendMessage(F.main("Class", "You equipped " + F.skill(customBuild.Name) + "."));
	}
}
