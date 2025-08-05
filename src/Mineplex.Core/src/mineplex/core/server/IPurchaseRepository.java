package mineplex.core.server;

import mineplex.core.common.util.Callback;

public interface IPurchaseRepository
{
	void PurchaseSalesPackage(Callback<String> callback, String name, boolean usingCredits, int salesPackageId);
}
