package mineplex.core.donation.repository;

import mineplex.core.common.util.Callback;
import mineplex.core.donation.repository.token.PlayerUpdateToken;
import mineplex.core.donation.repository.token.PurchaseToken;
import mineplex.core.donation.repository.token.UnknownPurchaseToken;
import mineplex.core.server.remotecall.AsyncJsonWebCall;
import mineplex.core.server.util.TransactionResponse;

public class DonationRepository
{
	private String _webAddress;
	
	public DonationRepository(String webAddress)
	{
		_webAddress = webAddress;
	}
	
	public void PurchaseKnownSalesPackage(Callback<TransactionResponse> callback, String name, int salesPackageId) 
	{
		PurchaseToken token = new PurchaseToken();
		token.AccountName = name;
		token.UsingCredits = false;
		token.SalesPackageId = salesPackageId;

		new AsyncJsonWebCall(_webAddress + "PlayerAccount/PurchaseKnownSalesPackage").Execute(TransactionResponse.class, callback, token);
	}
	
	public void PurchaseUnknownSalesPackage(Callback<TransactionResponse> callback, String name, String packageName, int gemCost) 
	{
		UnknownPurchaseToken token = new UnknownPurchaseToken();
		token.AccountName = name;
		token.SalesPackageName = packageName;
		token.Cost = gemCost;
		token.Premium = false;

		new AsyncJsonWebCall(_webAddress + "PlayerAccount/PurchaseUnknownSalesPackage").Execute(TransactionResponse.class, callback, token);
	}
	
	public void PlayerUpdate(Callback<PlayerUpdateToken> callback, String name, int greenGems)
	{
		PlayerUpdateToken token = new PlayerUpdateToken();
		token.Name = name;
		token.Gems = greenGems;
		new AsyncJsonWebCall(_webAddress + "PlayerAccount/PlayerUpdate").Execute(PlayerUpdateToken.class, callback, token);
	}
}