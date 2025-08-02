package me.chiss.Core.Shop.salespackage;

import java.util.List;

import me.chiss.Core.ClientData.IClientClass;
import mineplex.core.account.CoreClient;
import net.minecraft.server.v1_6_R2.IInventory;

public interface ISalesPackage
{
	String GetName();
    int GetCreditCost();
    int GetPointCost();
    boolean CanFitIn(CoreClient player);
    List<Integer> AddToCategory(IInventory inventory, int slot);
    void DeliverTo(IClientClass player);
    void PurchaseBy(CoreClient player);
    int ReturnFrom(CoreClient player);
    void DeliverTo(IClientClass player, int slot);
    int GetSalesPackageId();
	boolean IsFree();
}
