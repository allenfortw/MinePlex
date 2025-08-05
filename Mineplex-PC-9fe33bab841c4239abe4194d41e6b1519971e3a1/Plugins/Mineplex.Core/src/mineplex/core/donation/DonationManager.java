package mineplex.core.donation;

import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.repository.DonationRepository;
import mineplex.core.donation.repository.token.DonorTokenWrapper;
import mineplex.core.donation.repository.token.PlayerUpdateToken;
import mineplex.core.server.util.TransactionResponse;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DonationManager implements Listener
{
	private JavaPlugin _plugin;
	private DonationRepository _repository;
	
	private NautHashMap<String, Donor> _donors;
	
	private Object _donorLock = new Object();
	
	public DonationManager(JavaPlugin plugin, String webAddress)
	{
		_plugin = plugin;
		_repository = new DonationRepository(webAddress);
		
		_donors = new NautHashMap<String, Donor>();
		
		_plugin.getServer().getPluginManager().registerEvents(this, _plugin);
	}
	
	@EventHandler
	public void OnClientWebResponse(ClientWebResponseEvent event)
	{
		DonorTokenWrapper token = new Gson().fromJson(event.GetResponse(), DonorTokenWrapper.class);
		LoadDonor(token);
	}
	
	@EventHandler
	public void UnloadDonor(ClientUnloadEvent event)
	{
		synchronized (_donorLock)
		{
			_donors.remove(event.GetName());
		}
	}

	private void LoadDonor(DonorTokenWrapper token)
	{
		synchronized (_donorLock)
		{
			_donors.put(token.Name, new Donor(token.DonorToken));
		}
	}

	public Donor Get(String name)
	{
		synchronized (_donorLock)
		{
			return _donors.get(name);
		}
	}
	
	public void PurchaseUnknownSalesPackage(final Callback<TransactionResponse> callback, final String name, final String packageName, final int gemCost, boolean oneTimePurchase) 
	{
		Donor donor = Get(name);
		
		if (donor != null)
		{
			if (oneTimePurchase && donor.OwnsUnknownPackage(packageName))
			{
				if (callback != null)
					callback.run(TransactionResponse.AlreadyOwns);
				
				return;
			}
		}
		
		_repository.PurchaseUnknownSalesPackage(new Callback<TransactionResponse>()
		{
			public void run(TransactionResponse response)
			{
				if (response == TransactionResponse.Success)
				{
					Donor donor = Get(name);
					
					if (donor != null)
					{
						donor.AddUnknownSalesPackagesOwned(packageName);
						donor.DeductCost(gemCost, CurrencyType.Gems);
					}
				}
				
				if (callback != null)
					callback.run(response);
			}
		}, name, packageName, gemCost);
	}
	
	public void PurchaseKnownSalesPackage(final Callback<TransactionResponse> callback, final String name, final int salesPackageId) 
	{
		_repository.PurchaseKnownSalesPackage(new Callback<TransactionResponse>()
		{
			public void run(TransactionResponse response)
			{
				if (response == TransactionResponse.Success)
				{
					Donor donor = Get(name);				

					if (donor != null)
					{
						donor.AddSalesPackagesOwned(salesPackageId);
					}
				}
				
				if (callback != null)
					callback.run(response);
			}
		}, name, salesPackageId);
	}

	public void RewardGems(final String name, final int greenGems)
	{
		_repository.PlayerUpdate(new Callback<PlayerUpdateToken>()
		{
			public void run(PlayerUpdateToken token)
			{
				Donor donor = Get(name);				

				if (donor != null)
				{
					donor.AddGems(greenGems);
				}
			}
		}, name, greenGems);
	}
}
