package mineplex.core.donation;

import java.util.ArrayList;
import java.util.List;

import mineplex.core.common.CurrencyType;
import mineplex.core.donation.repository.token.DonorToken;

public class Donor
{
    private int _gems;
    private boolean _donated;
    private List<Integer> _salesPackagesOwned;
    private List<String> _unknownSalesPackagesOwned;
    
    private boolean _update = true;
	
	public Donor(DonorToken token)
	{
	    _gems = token.Gems;
	    _donated = token.Donated;
	    
	    _salesPackagesOwned = token.SalesPackages;
	    _unknownSalesPackagesOwned = token.UnknownSalesPackages;

	    if (_salesPackagesOwned == null)
	    {
	    	_salesPackagesOwned = new ArrayList<Integer>();
	    }
	    
	    if (_unknownSalesPackagesOwned == null)
	    {
	    	_unknownSalesPackagesOwned = new ArrayList<String>();
	    }
	}
	
    public int GetGems()
    {
        return _gems;
    }
    
    public List<Integer> GetSalesPackagesOwned()
    {
        return _salesPackagesOwned;
    }

    public boolean Owns(Integer salesPackageId)
    {
        return salesPackageId == -1 || _salesPackagesOwned.contains(salesPackageId);
    }

	public void AddSalesPackagesOwned(int salesPackageId) 
	{
		_salesPackagesOwned.add(salesPackageId);
	}

	public boolean HasDonated() 
	{
		return _donated;
	}
    
    public void DeductCost(int cost, CurrencyType currencyType)
    {
    	switch (currencyType)
    	{
			case Gems:
				_gems -= cost;
				_update = true;
				break;
			default:
				break;
    	}
    }
	
	public int GetBalance(CurrencyType currencyType)
	{
    	switch (currencyType)
    	{
			case Gems:
				return _gems;
			case Tokens:
				return 0;
			default:
				return 0;
    	}
	}

	public void AddGems(int gems)
	{
		_gems += gems;
	}
	
	public boolean OwnsUnknownPackage(String packageName)
	{
		return _unknownSalesPackagesOwned.contains(packageName);
	}
	
	public boolean Updated()
	{
		return _update;
	}

	public void AddUnknownSalesPackagesOwned(String packageName)
	{
		_unknownSalesPackagesOwned.add(packageName);
	}
}
