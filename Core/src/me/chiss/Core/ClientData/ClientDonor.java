package me.chiss.Core.ClientData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nautilus.minecraft.core.webserver.token.Account.DonorToken;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.chiss.Core.Class.IPvpClass;
import mineplex.core.CurrencyType;
import mineplex.core.Rank;
import mineplex.core.server.IRepository;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.minecraft.account.CoreClient;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.SlotToken;
import me.chiss.Core.Pet.repository.token.PetToken;
import me.chiss.Core.Skill.ISkill;
import me.chiss.Core.Skill.ISkill.SkillType;

public class ClientDonor
{
	private DonationRepository
    private int _blueGems;
    private int _greenGems;
    private boolean _donated;
    private List<Integer> _salesPackagesOwned;
    
	public ClientDonor(CoreClient client, IRepository repository)
	{
		super(client, "Donor", repository);
	}
	
    public ClientDonor(CoreClient client, IRepository repository, DonorToken token)
    {
        super(client, "Donor", repository, token);
    }	
	
	@Override
	public void Load() 
	{

	}
	
	@Override
	public void LoadToken(DonorToken token)
	{
		int creditChange = token.BlueGems - _blueGems;
		int pointChange = token.GreenGems - _greenGems;
	    _blueGems = token.BlueGems;
	    _greenGems = token.GreenGems;
	    _donated = token.Donated;
	    
	    _salesPackagesOwned = token.SalesPackages;
	    

	    
	    _customBuilds = new NautHashMap<IPvpClass, HashMap<Integer, CustomBuildToken>>();
	    _activeCustomBuilds = new NautHashMap<IPvpClass, CustomBuildToken>();
	    
	    if (Client.Manager().Classes() != null && Client.Manager.Skills() != null)
	    {
		    for (IPvpClass pvpClass : Client.Manager().Classes().GetAllClasses())
		    {
		    	_customBuilds.put(pvpClass, new HashMap<Integer, CustomBuildToken>());
		    }
		    
		    for (CustomBuildToken buildToken : token.CustomBuilds)
		    {
		    	IPvpClass pvpClass = Client.Manager().Classes().GetClass(buildToken.PvpClassId);
		    	
		    	int skillTokenCount = buildToken.SkillTokensBalance;
	
		    	Entry<ISkill, Integer> swordSkill = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.SwordSkillId);
		    	Entry<ISkill, Integer> axeSkill = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.AxeSkillId);
		    	Entry<ISkill, Integer> bowSkill = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.BowSkillId);
		    	Entry<ISkill, Integer> classPassiveASkill = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.ClassPassiveASkillId);
		    	Entry<ISkill, Integer> classPassiveBSkill = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.ClassPassiveBSkillId);
		    	Entry<ISkill, Integer> globalPassiveA = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.GlobalPassiveASkillId);
		    	Entry<ISkill, Integer> globalPassiveB = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.GlobalPassiveBSkillId);
		    	Entry<ISkill, Integer> globalPassiveC = Client.Manager.Skills().GetSkillBySalesPackageId(buildToken.GlobalPassiveCSkillId);
		    	
		    	if (buildToken.SwordSkillId != -1 && (swordSkill == null || swordSkill.getKey().GetSkillType() != SkillType.Sword 
		    			|| (!swordSkill.getKey().IsFree(swordSkill.getValue()) && !Owns(buildToken.SwordSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.AxeSkillId != -1 && (axeSkill == null || axeSkill.getKey().GetSkillType() != SkillType.Axe 
		    			|| (!axeSkill.getKey().IsFree(axeSkill.getValue()) && !Owns(buildToken.AxeSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.BowSkillId != -1 && (bowSkill == null || bowSkill.getKey().GetSkillType() != SkillType.Bow 
		    			|| (!bowSkill.getKey().IsFree(bowSkill.getValue()) && !Owns(buildToken.BowSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.ClassPassiveASkillId != -1 && (classPassiveASkill == null || classPassiveASkill.getKey().GetSkillType() != SkillType.PassiveA 
		    			|| (!classPassiveASkill.getKey().IsFree(classPassiveASkill.getValue()) && !Owns(buildToken.ClassPassiveASkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.ClassPassiveBSkillId != -1 && (classPassiveBSkill == null || classPassiveBSkill.getKey().GetSkillType() != SkillType.PassiveB 
		    			|| (!classPassiveBSkill.getKey().IsFree(classPassiveBSkill.getValue()) && !Owns(buildToken.ClassPassiveBSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.GlobalPassiveASkillId != -1 && (globalPassiveA == null || globalPassiveA.getKey().GetSkillType() != SkillType.PassiveC 
		    			|| (!globalPassiveA.getKey().IsFree(globalPassiveA.getValue()) && !Owns(buildToken.GlobalPassiveASkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.GlobalPassiveBSkillId != -1 && (globalPassiveB == null || globalPassiveB.getKey().GetSkillType() != SkillType.PassiveD 
		    			|| (!globalPassiveB.getKey().IsFree(globalPassiveB.getValue()) && !Owns(buildToken.GlobalPassiveBSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	if (buildToken.GlobalPassiveCSkillId != -1 && (globalPassiveC == null || globalPassiveC.getKey().GetSkillType() != SkillType.PassiveE 
		    			|| (!globalPassiveC.getKey().IsFree(globalPassiveC.getValue()) && !Owns(buildToken.GlobalPassiveCSkillId) && !Client.Rank().Has(Rank.DIAMOND, false))))
		    		continue;
		    	
				if (buildToken.SwordSkillId != -1)
					skillTokenCount += swordSkill.getKey().GetTokenCost() * swordSkill.getValue();
				
				if (buildToken.AxeSkillId != -1)
					skillTokenCount += axeSkill.getKey().GetTokenCost() * axeSkill.getValue();
				
				if (buildToken.BowSkillId != -1)
					skillTokenCount += bowSkill.getKey().GetTokenCost() * bowSkill.getValue();
				
				if (buildToken.ClassPassiveASkillId != -1)
					skillTokenCount += classPassiveASkill.getKey().GetTokenCost() * classPassiveASkill.getValue();
				
				if (buildToken.ClassPassiveBSkillId != -1)
					skillTokenCount += classPassiveBSkill.getKey().GetTokenCost() * classPassiveBSkill.getValue();
				
				if (buildToken.GlobalPassiveASkillId != -1)
					skillTokenCount += globalPassiveA.getKey().GetTokenCost() * globalPassiveA.getValue();
				
				if (buildToken.GlobalPassiveBSkillId != -1)
					skillTokenCount += globalPassiveB.getKey().GetTokenCost() * globalPassiveB.getValue();
				
				if (buildToken.GlobalPassiveCSkillId != -1)
					skillTokenCount += globalPassiveC.getKey().GetTokenCost() * globalPassiveC.getValue();
		    	
				if (skillTokenCount <= 120)
				{
					_customBuilds.get(pvpClass).put(buildToken.CustomBuildNumber, buildToken);
					
					if (buildToken.Active)
					{
						_activeCustomBuilds.put(pvpClass, buildToken);
					}
				}
		    }
	    }
	    
	    if (Client.GetPlayer() != null)
	    {
		    if (creditChange > 0)
		    {
		    	Client.GetPlayer().sendMessage(C.cGray + "You've received " + C.cAqua + pointChange + C.cGray + " credits.  Thanks for donating!");
		    }
		    else if (creditChange < 0)
		    {
		    	Client.GetPlayer().sendMessage(C.cGray + "Your credit balance is now : " + C.cAqua + pointChange + C.cGray + ".");
		    }
		    
		    if (pointChange > 0)
		    {
		    	Client.GetPlayer().sendMessage(C.cGray + "You've received " + C.cGreen + pointChange + C.cGray + " points.  Thanks for playing!");
		    }
		    else if (pointChange < 0)
		    {
		    	Client.GetPlayer().sendMessage(C.cGray + "Your point balance is now : " + C.cGreen + pointChange + C.cGray + ".");
		    }
	    }
	}

	public int GetBlueGems()
	{
	    return _blueGems;
	}
	
    public int GetGreenGems()
    {
        return _greenGems;
    }
    
    public void AddPoints(int points)
    {
        _greenGems += points;
    }
    
    public List<Integer> GetSalesPackagesOwned()
    {
        return _salesPackagesOwned;
    }
    
    public HashMap<Integer, CustomBuildToken> GetCustomBuilds(IPvpClass pvpClass)
    {
    	return _customBuilds.get(pvpClass);
    }

    public void PurchaseItem(int cost)
    {
        _itemTokens -= cost;
    }
    
    public void PurchaseSkill(int cost)
    {
        _skillTokens -= cost;
    }
    
    public void ReturnSkill(int cost)
    {
        _skillTokens += cost;
    }
    
    public void ReturnItem(int cost)
    {
        _itemTokens += cost;
    }

    public void SetCredits(int credits)
    {
        _blueGems = credits;
    }
    
    public void DeductCost(int cost, CurrencyType currencyType, boolean premium)
    {
    	switch (currencyType)
    	{
			case Coins:
				Client.Game().SetEconomyBalance(Client.Game().GetEconomyBalance() - cost);
				break;
			case Gems:
				if (premium)
					_blueGems -= cost;
				else
					_greenGems -= cost;
				break;
			default:
				break;
    	}
    }

    public boolean Owns(Integer salesPackageId)
    {
        return salesPackageId == -1 || _salesPackagesOwned.contains(salesPackageId);
    }

    public void SetTokens(int skillTokens, int itemTokens)
    {
        _skillTokens = skillTokens;
        _itemTokens = itemTokens;
    }
    
    public void SetDefaultTokens(int skillTokens, int itemTokens)
    {
        _defaultSkillTokens = skillTokens;
        _defaultItemTokens = itemTokens;
    }
    
    public int GetSkillTokens()
    {
        return _skillTokens;
    }
    
    public int GetItemTokens()
    {
        return _itemTokens;
    }

	public void ResetItemTokens() 
	{
		_itemTokens = _defaultItemTokens;
	}
	
	public void ResetSkillTokens()
	{
		_skillTokens = _defaultSkillTokens;
	}

	public void AddSalesPackagesOwned(int salesPackageId) 
	{
		_salesPackagesOwned.add(salesPackageId);
	}

	public void SetItemTokens(int itemTokens) 
	{
		_itemTokens = itemTokens;
	}
	
	public void SetSkillTokens(int skillTokens) 
	{
		_skillTokens = skillTokens;
	}

	public void SetPoints(int points) 
	{
		_greenGems = points;
	}

	public boolean HasDonated() 
	{
		return _donated;
	}

	public void SaveActiveCustomBuild(boolean saveItems) 
	{
		if (Client == null || Client.Class().GetGameClass() == null)
			return;
		
		_savingCustomBuild.PvpClassId = Client.Class().GetGameClass().GetSalesPackageId();
		_savingCustomBuild.PlayerName = Client.GetPlayerName();
		_savingCustomBuild.SkillTokensBalance = _skillTokens;
		_savingCustomBuild.ItemTokensBalance = _itemTokens;
		
		ISkill swordSkill = Client.Class().GetSkillByType(SkillType.Sword);
		
		if (swordSkill != null)
			_savingCustomBuild.SwordSkillId = swordSkill.GetSalesPackageId(Client.Class().GetSkillLevel(swordSkill));
		else
			_savingCustomBuild.SwordSkillId = -1;
		
		ISkill axeSkill = Client.Class().GetSkillByType(SkillType.Axe);
		
		if (axeSkill != null)
			_savingCustomBuild.AxeSkillId = axeSkill.GetSalesPackageId(Client.Class().GetSkillLevel(axeSkill));
		else
			_savingCustomBuild.AxeSkillId = -1;
		
		ISkill bowSkill = Client.Class().GetSkillByType(SkillType.Bow);
		
		if (bowSkill != null)
			_savingCustomBuild.BowSkillId = bowSkill.GetSalesPackageId(Client.Class().GetSkillLevel(bowSkill));
		else
			_savingCustomBuild.BowSkillId = -1;
		
		ISkill passiveASkill = Client.Class().GetSkillByType(SkillType.PassiveA);
		
		if (passiveASkill != null)
			_savingCustomBuild.ClassPassiveASkillId = passiveASkill.GetSalesPackageId(Client.Class().GetSkillLevel(passiveASkill));
		else
			_savingCustomBuild.ClassPassiveASkillId = -1;
		
		ISkill passiveBSkill = Client.Class().GetSkillByType(SkillType.PassiveB);
		
		if (passiveBSkill != null)
			_savingCustomBuild.ClassPassiveBSkillId = passiveBSkill.GetSalesPackageId(Client.Class().GetSkillLevel(passiveBSkill));
		else
			_savingCustomBuild.ClassPassiveBSkillId = -1;
		
		ISkill passiveCSkill = Client.Class().GetSkillByType(SkillType.PassiveC);
		
		if (passiveCSkill != null)
			_savingCustomBuild.GlobalPassiveASkillId = passiveCSkill.GetSalesPackageId(Client.Class().GetSkillLevel(passiveCSkill));
		else
			_savingCustomBuild.GlobalPassiveASkillId = -1;
		
		ISkill passiveDSkill = Client.Class().GetSkillByType(SkillType.PassiveD);
		
		if (passiveDSkill != null)
			_savingCustomBuild.GlobalPassiveBSkillId = passiveDSkill.GetSalesPackageId(Client.Class().GetSkillLevel(passiveDSkill));
		else
			_savingCustomBuild.GlobalPassiveBSkillId = -1;
		
		ISkill passiveESkill = Client.Class().GetSkillByType(SkillType.PassiveE);
		
		if (passiveESkill != null)
			_savingCustomBuild.GlobalPassiveCSkillId = passiveESkill.GetSalesPackageId(Client.Class().GetSkillLevel(passiveESkill));
		else
			_savingCustomBuild.GlobalPassiveCSkillId = -1;
		
		_savingCustomBuild.Slots = new ArrayList<SlotToken>(9);
		
		if (saveItems)
		{
			for (int i=0; i < 9; i++)
			{
				SlotToken slotToken = new SlotToken();
				ItemStack itemStack = Client.Class().GetInventory().getItem(i);
				slotToken.Material = itemStack == null ? org.bukkit.Material.AIR.toString() : itemStack.getType().toString();
				slotToken.Amount = itemStack == null ? 1 : itemStack.getAmount();
				
				_savingCustomBuild.Slots.add(slotToken);
			}
		}
		
		Repository.SaveCustomBuild(_savingCustomBuild);
		_savingCustomBuild = null;
	}

	public void SetSavingCustomBuild(int i, IPvpClass pvpClass, CustomBuildToken customBuild)
	{
		_savingCustomBuild = customBuild;
		_savingCustomBuild.CustomBuildNumber = i;
		_customBuilds.get(pvpClass).put(i, _savingCustomBuild);
	}
	
	public void SetActiveCustomBuild(int i, IPvpClass pvpClass, CustomBuildToken customBuild)
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

	public NautHashMap<EntityType, String> GetPets()
	{
		return _pets;
	}

	public Integer GetPetNameTagCount()
	{
		return _petNameTagCount;
	}

	public void SetPetNameTagCount(int count)
	{
		_petNameTagCount = count;
	}

	public int GetBalance(CurrencyType currencyType, boolean premium)
	{
    	switch (currencyType)
    	{
			case Coins:
				return Client.Game().GetEconomyBalance();
			case Gems:
				return premium ? _blueGems : (_greenGems + _blueGems);
			case Tokens:
				return 0;
			default:
				return 0;
    	}
	}
}
