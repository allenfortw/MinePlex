package me.chiss.Core.Shop.page;

import me.chiss.Core.Shop.salespackage.ISalesPackage;
import mineplex.core.account.CoreClient;

public interface IShopPage extends IPage<IShopPage>
{
	void AddItem(ISalesPackage iSalesPackage, int slot);
    ISalesPackage GetItem(int itemSlot);
    
    void Reset(CoreClient player);    
    
    void PlayerWants(CoreClient donor, int slot);
    void PlayerReturning(CoreClient player, int slot);
    
	void ChangeCurrency(CoreClient player);
	void UpdateBalance(CoreClient player);

	void UpdateSlot(CoreClient player, int slot);
	void ResetVisuals();
	void PrepSlotsForPlayer(CoreClient player);
}
