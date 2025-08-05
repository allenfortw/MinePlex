package mineplex.minecraft.game.classcombat.Class;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.repository.ClassRepository;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClientClass
{
  private ClassManager _classFactory;
  private SkillFactory _skillFactory;
  private CoreClient _client;
  private Donor _donor;
  private IPvpClass _gameClass;
  private NautHashMap<ISkill.SkillType, ISkill> _skillMap = new NautHashMap();
  
  private IPvpClass _lastClass;
  private NautHashMap<Integer, ItemStack> _lastItems = new NautHashMap();
  private ItemStack[] _lastArmor = new ItemStack[4];
  private NautHashMap<ISkill.SkillType, ISkill> _lastSkillMap = new NautHashMap();
  
  private NautHashMap<IPvpClass, HashMap<Integer, CustomBuildToken>> _customBuilds;
  
  private NautHashMap<IPvpClass, CustomBuildToken> _activeCustomBuilds;
  private CustomBuildToken _savingCustomBuild;
  
  public ClientClass(ClassManager classFactory, SkillFactory skillFactory, CoreClient client, Donor donor, ClientClassToken token)
  {
    this._classFactory = classFactory;
    this._skillFactory = skillFactory;
    this._client = client;
    this._donor = donor;
    
    Load(token);
  }
  
  public void Load(ClientClassToken token)
  {
    this._customBuilds = new NautHashMap();
    this._activeCustomBuilds = new NautHashMap();
    
    for (IPvpClass pvpClass : this._classFactory.GetAllClasses())
    {
      this._customBuilds.put(pvpClass, new HashMap());
    }
    
    if (token == null) {
      return;
    }
    for (CustomBuildToken buildToken : token.CustomBuilds)
    {
      IPvpClass pvpClass = this._classFactory.GetClass(buildToken.PvpClassId);
      
      ISkill swordSkill = this._skillFactory.GetSkillBySalesPackageId(buildToken.SwordSkillId);
      ISkill axeSkill = this._skillFactory.GetSkillBySalesPackageId(buildToken.AxeSkillId);
      ISkill bowSkill = this._skillFactory.GetSkillBySalesPackageId(buildToken.BowSkillId);
      ISkill classPassiveASkill = this._skillFactory.GetSkillBySalesPackageId(buildToken.ClassPassiveASkillId);
      ISkill classPassiveBSkill = this._skillFactory.GetSkillBySalesPackageId(buildToken.ClassPassiveBSkillId);
      ISkill globalPassive = this._skillFactory.GetSkillBySalesPackageId(buildToken.GlobalPassiveSkillId);
      
      if (ValidSkill(buildToken.SwordSkillId, swordSkill, ISkill.SkillType.Sword))
      {

        if (ValidSkill(buildToken.AxeSkillId, axeSkill, ISkill.SkillType.Axe))
        {

          if (ValidSkill(buildToken.BowSkillId, bowSkill, ISkill.SkillType.Bow))
          {

            if (ValidSkill(buildToken.ClassPassiveASkillId, classPassiveASkill, ISkill.SkillType.PassiveA))
            {

              if (ValidSkill(buildToken.ClassPassiveBSkillId, classPassiveBSkill, ISkill.SkillType.PassiveB))
              {

                if (ValidSkill(buildToken.GlobalPassiveSkillId, globalPassive, ISkill.SkillType.GlobalPassive))
                {

                  ((HashMap)this._customBuilds.get(pvpClass)).put(buildToken.CustomBuildNumber, buildToken); } } } } }
      }
    }
  }
  
  public NautHashMap<Integer, ItemStack> GetDefaultItems() {
    return this._lastItems;
  }
  
  public void SetDefaultHead(ItemStack armor)
  {
    this._lastArmor[3] = armor;
  }
  
  public void SetDefaultChest(ItemStack armor)
  {
    this._lastArmor[2] = armor;
  }
  
  public void SetDefaultLegs(ItemStack armor)
  {
    this._lastArmor[1] = armor;
  }
  
  public void SetDefaultFeet(ItemStack armor)
  {
    this._lastArmor[0] = armor;
  }
  
  public void SaveActiveCustomBuild()
  {
    if (GetGameClass() == null) {
      return;
    }
    this._savingCustomBuild.PvpClassId = GetGameClass().GetSalesPackageId();
    this._savingCustomBuild.PlayerName = this._client.GetPlayerName();
    
    ISkill swordSkill = GetSkillByType(ISkill.SkillType.Sword);
    
    if (swordSkill != null) {
      this._savingCustomBuild.SwordSkillId = swordSkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.SwordSkillId = -1;
    }
    ISkill axeSkill = GetSkillByType(ISkill.SkillType.Axe);
    
    if (axeSkill != null) {
      this._savingCustomBuild.AxeSkillId = axeSkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.AxeSkillId = -1;
    }
    ISkill bowSkill = GetSkillByType(ISkill.SkillType.Bow);
    
    if (bowSkill != null) {
      this._savingCustomBuild.BowSkillId = bowSkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.BowSkillId = -1;
    }
    ISkill passiveASkill = GetSkillByType(ISkill.SkillType.PassiveA);
    
    if (passiveASkill != null) {
      this._savingCustomBuild.ClassPassiveASkillId = passiveASkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.ClassPassiveASkillId = -1;
    }
    
    ISkill passiveBSkill = GetSkillByType(ISkill.SkillType.PassiveB);
    
    if (passiveBSkill != null) {
      this._savingCustomBuild.ClassPassiveBSkillId = passiveBSkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.ClassPassiveBSkillId = -1;
    }
    ISkill globalPassiveSkill = GetSkillByType(ISkill.SkillType.GlobalPassive);
    
    if (globalPassiveSkill != null) {
      this._savingCustomBuild.GlobalPassiveSkillId = globalPassiveSkill.GetSalesPackageId().intValue();
    } else {
      this._savingCustomBuild.GlobalPassiveSkillId = -1;
    }
    this._savingCustomBuild.Slots = new ArrayList(9);
    
    this._classFactory.GetRepository().SaveCustomBuild(this._savingCustomBuild);
    this._savingCustomBuild = null;
  }
  
  public void SetSavingCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
  {
    this._savingCustomBuild = customBuild;
    ((HashMap)this._customBuilds.get(pvpClass)).put(this._savingCustomBuild.CustomBuildNumber, this._savingCustomBuild);
  }
  
  public void SetActiveCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
  {
    customBuild.Active = true;
    this._activeCustomBuilds.put(pvpClass, customBuild);
  }
  
  public CustomBuildToken GetActiveCustomBuild(IPvpClass pvpClass)
  {
    return (CustomBuildToken)this._activeCustomBuilds.get(pvpClass);
  }
  
  public CustomBuildToken GetSavingCustomBuild()
  {
    return this._savingCustomBuild;
  }
  
  public boolean IsSavingCustomBuild()
  {
    return this._savingCustomBuild != null;
  }
  
  public HashMap<Integer, CustomBuildToken> GetCustomBuilds(IPvpClass pvpClass)
  {
    return (HashMap)this._customBuilds.get(pvpClass);
  }
  
  public void EquipCustomBuild(CustomBuildToken customBuild)
  {
    EquipCustomBuild(customBuild, true);
  }
  
  public void EquipCustomBuild(CustomBuildToken customBuild, boolean notify)
  {
    this._lastClass = this._classFactory.GetClass(customBuild.PvpClassId);
    
    if (this._lastClass == null) {
      return;
    }
    this._lastSkillMap.remove(ISkill.SkillType.Class);
    
    SetDefaultHead(ItemStackFactory.Instance.CreateStack(this._lastClass.GetHead()));
    SetDefaultChest(ItemStackFactory.Instance.CreateStack(this._lastClass.GetChestplate()));
    SetDefaultLegs(ItemStackFactory.Instance.CreateStack(this._lastClass.GetLeggings()));
    SetDefaultFeet(ItemStackFactory.Instance.CreateStack(this._lastClass.GetBoots()));
    
    if (customBuild.SwordSkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.Sword, this._skillFactory.GetSkillBySalesPackageId(customBuild.SwordSkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Sword);
    }
    if (customBuild.AxeSkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.Axe, this._skillFactory.GetSkillBySalesPackageId(customBuild.AxeSkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Axe);
    }
    if (customBuild.BowSkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.Bow, this._skillFactory.GetSkillBySalesPackageId(customBuild.BowSkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Bow);
    }
    if (customBuild.ClassPassiveASkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.PassiveA, this._skillFactory.GetSkillBySalesPackageId(customBuild.ClassPassiveASkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.PassiveA);
    }
    if (customBuild.ClassPassiveBSkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.PassiveB, this._skillFactory.GetSkillBySalesPackageId(customBuild.ClassPassiveBSkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.PassiveB);
    }
    if (customBuild.GlobalPassiveSkillId != -1) {
      this._lastSkillMap.put(ISkill.SkillType.GlobalPassive, this._skillFactory.GetSkillBySalesPackageId(customBuild.GlobalPassiveSkillId));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.GlobalPassive);
    }
    ResetToDefaults(true, false);
    
    if (notify)
    {
      ListSkills(this._client.GetPlayer());
      this._client.GetPlayer().getWorld().playSound(this._client.GetPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      
      this._client.GetPlayer().sendMessage(F.main("Class", "You equipped " + F.skill(customBuild.Name) + "."));
    }
  }
  
  public void ListSkills(Player caller)
  {
    UtilPlayer.message(caller, F.main("Skill", "Listing Class Skills:"));
    
    for (ISkill.SkillType type : ISkill.SkillType.values()) {
      if (((caller.isOp()) || (type != ISkill.SkillType.Class)) && 
        (this._skillMap.containsKey(type)))
        UtilPlayer.message(caller, F.desc(type.toString(), ((ISkill)this._skillMap.get(type)).GetName()));
    }
  }
  
  public void ResetSkills(Player player) {
    for (ISkill skill : GetSkills())
    {
      skill.Reset(player);
    }
  }
  
  public void ResetToDefaults(boolean equipItems, boolean equipDefaultArmor)
  {
    if (this._lastClass == null)
    {
      this._lastClass = this._classFactory.GetClass("Knight");
      
      this._lastArmor[3] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetHead());
      this._lastArmor[2] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetChestplate());
      this._lastArmor[1] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetLeggings());
      this._lastArmor[0] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetBoots());
      
      for (ISkill skill : this._lastClass.GetDefaultSkills().keySet())
      {
        if (skill.GetSkillType() != ISkill.SkillType.Class) {
          this._lastSkillMap.put(skill.GetSkillType(), skill);
        }
      }
    }
    SetGameClass(this._lastClass);
    
    if (equipDefaultArmor)
    {
      if (this._lastArmor[3] != null) {
        this._client.GetPlayer().getInventory().setHelmet(this._lastArmor[3].clone());
      }
      if (this._lastArmor[2] != null) {
        this._client.GetPlayer().getInventory().setChestplate(this._lastArmor[2].clone());
      }
      if (this._lastArmor[1] != null) {
        this._client.GetPlayer().getInventory().setLeggings(this._lastArmor[1].clone());
      }
      if (this._lastArmor[0] != null) {
        this._client.GetPlayer().getInventory().setBoots(this._lastArmor[0].clone());
      }
    }
    if (equipItems)
    {
      PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD), Integer.valueOf(0));
      PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.IRON_AXE), Integer.valueOf(1));
      
      for (int i = 2; i < 9; i++)
      {
        PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.MUSHROOM_SOUP), Integer.valueOf(i));
      }
      
      if ((this._gameClass.GetType() == IPvpClass.ClassType.Assassin) || (this._gameClass.GetType() == IPvpClass.ClassType.Ranger))
      {
        PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.BOW), Integer.valueOf(2));
        PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.ARROW, this._gameClass.GetType() == IPvpClass.ClassType.Assassin ? 16 : 32), Integer.valueOf(3));


      }
      else if (this._gameClass.GetType() != IPvpClass.ClassType.Mage)
      {
        PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.POTION), Integer.valueOf(8));
      }
      else
      {
        PutDefaultItem(ItemStackFactory.Instance.CreateStack(Material.WEB, 2), Integer.valueOf(8));
      }
      

      this._client.GetPlayer().getInventory().clear();
      
      for (Map.Entry<Integer, ItemStack> defaultItem : GetDefaultItems().entrySet())
      {
        this._client.GetPlayer().getInventory().setItem(((Integer)defaultItem.getKey()).intValue(), (ItemStack)defaultItem.getValue());
      }
    }
    
    ClearSkills();
    
    for (ISkill cur : this._gameClass.GetDefaultSkills().keySet())
    {
      if (cur.GetSkillType() == ISkill.SkillType.Class) {
        AddSkill(cur);
      }
    }
    for (ISkill skill : this._lastSkillMap.values()) {
      AddSkill(skill);
    }
  }
  
  public void ClearSkills() {
    if (this._skillMap != null)
    {
      for (ISkill skill : this._skillMap.values())
      {
        if (skill == null)
        {
          System.out.println("Skill is null in ClientClass.ClearSkills()");
        }
        else if (this._client == null)
        {
          System.out.println("Client is null in ClientClass.ClearSkills()");
        }
        else
        {
          skill.RemoveUser(this._client.GetPlayer());
        }
      }
    }
    
    this._skillMap.clear();
  }
  
  public void ClearDefaultSkills()
  {
    this._lastSkillMap = new NautHashMap();
  }
  
  public void SetGameClass(IPvpClass gameClass)
  {
    ClearSkills();
    
    this._gameClass = gameClass;
    
    if (this._gameClass == null) {
      return;
    }
    ISkill skill;
    if (this._classFactory.GetRestore().IsActive())
    {
      Collection<ISkill> skills = this._classFactory.GetRestore().GetBuild(this._client.GetPlayerName(), gameClass);
      
      if (skills != null)
      {
        for (Iterator localIterator = skills.iterator(); localIterator.hasNext();) { skill = (ISkill)localIterator.next();
          AddSkill(skill);
        }
        
        UtilPlayer.message(this._client.GetPlayer(), F.main("Class", "Armor Class: " + F.oo(this._gameClass.GetName(), true)));
        return;
      }
    }
    
    for (ISkill cur : gameClass.GetDefaultSkills().keySet())
    {
      if (cur.GetSkillType() == ISkill.SkillType.Class) {
        AddSkill(cur);
      }
    }
    
    UtilPlayer.message(this._client.GetPlayer(), F.main("Class", "Armor Class: " + F.oo(this._gameClass.GetName(), true)));
  }
  

  public IPvpClass GetGameClass()
  {
    return this._gameClass;
  }
  
  public boolean IsGameClass(IPvpClass.ClassType type)
  {
    if (GetGameClass() == null) {
      return false;
    }
    return GetGameClass().GetType() == type;
  }
  
  public Collection<ISkill> GetSkills()
  {
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    return this._skillMap.values();
  }
  
  public Collection<ISkill> GetDefaultSkills()
  {
    return this._lastSkillMap.values();
  }
  
  public ISkill GetSkillByType(ISkill.SkillType skillType)
  {
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    if (this._skillMap.containsKey(skillType)) {
      return (ISkill)this._skillMap.get(skillType);
    }
    return null;
  }
  
  public void AddSkill(ISkill skill)
  {
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    if (this._skillMap.get(skill.GetSkillType()) != null) {
      ((ISkill)this._skillMap.get(skill.GetSkillType())).RemoveUser(this._client.GetPlayer());
    }
    this._skillMap.put(skill.GetSkillType(), skill);
    this._lastSkillMap.put(skill.GetSkillType(), skill);
    
    skill.AddUser(this._client.GetPlayer());
    

    if (this._classFactory.GetRestore().IsActive()) {
      this._classFactory.GetRestore().SaveBuild(this._client.GetPlayerName(), this._gameClass, GetSkills());
    }
  }
  
  public void RemoveSkill(ISkill skill) {
    if (skill == null) {
      return;
    }
    if (this._skillMap == null) {
      return;
    }
    this._skillMap.remove(skill.GetSkillType());
    
    skill.RemoveUser(this._client.GetPlayer());
  }
  
  public ItemStack[] GetDefaultArmor()
  {
    return this._lastArmor;
  }
  
  public void PutDefaultItem(ItemStack value, Integer key)
  {
    this._lastItems.put(key, value);
  }
  
  public void ClearDefaults()
  {
    this._lastItems.clear();
    this._lastArmor = new ItemStack[4];
    this._lastSkillMap.clear();
  }
  
  private boolean ValidSkill(int skillId, ISkill skill, ISkill.SkillType expectedType)
  {
    if ((skillId != -1) && ((skill == null) || (skill.GetSkillType() != expectedType) || ((!skill.IsFree()) && (!this._donor.Owns(Integer.valueOf(skillId))) && (!this._client.GetRank().Has(Rank.ULTRA)) && (!this._donor.OwnsUnknownPackage("Competitive ULTRA"))))) {
      return false;
    }
    return true;
  }
}
