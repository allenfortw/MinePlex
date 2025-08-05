package me.chiss.Core.Shop.salespackage;

import java.util.List;

import me.chiss.Core.ClientData.IClientClass;
import mineplex.core.account.CoreClient;
import mineplex.minecraft.shop.item.ISalesPackage;
import net.minecraft.server.v1_6_R2.IInventory;

public class DonationPackage implements ISalesPackage
{
	private ISalesPackage _salesPackage;
	
	public DonationPackage(ISalesPackage salesPackage)
	{
		_salesPackage = salesPackage;
	}
	
    @Override
    public String GetName()
    {
    	return _salesPackage.GetName();
    }
	
    public int GetCreditCost()
    {
        return _salesPackage.GetCreditCost();
    }
    
    public int GetPointCost()
    {
    	return _salesPackage.GetPointCost();
    }
    
    public int GetTokenCost()
    {
    	return _salesPackage.GetTokenCost();
    }
    
    public int GetEconomyCost()
    {
    	return _salesPackage.GetEconomyCost();
    }
	
	@Override
	public boolean IsFree()
	{
		return _salesPackage.IsFree();
	}

	@Override
	public boolean CanFitIn(CoreClient player) 
	{
		return true;
	}

	@Override
	public List<Integer> AddToCategory(IInventory inventory, int slot) 
	{
		return _salesPackage.AddToCategory(inventory, slot);
	}

	@Override
	public void DeliverTo(IClientClass player) 
	{

	}

	@Override
	public void PurchaseBy(CoreClient player) 
	{
		player.Donor().AddSalesPackagesOwned(_salesPackage.GetSalesPackageId());
	}

	@Override
	public int ReturnFrom(CoreClient player) 
	{
		return 0;
	}

	@Override
	public void DeliverTo(IClientClass player, int slot) 
	{
		_salesPackage.DeliverTo(player, slot);
	}

	@Override
	public int GetSalesPackageId()
	{
		return _salesPackage.GetSalesPackageId();
	}

	public ISalesPackage GetWrappedPackage() 
	{
		return _salesPackage;
	}
}
