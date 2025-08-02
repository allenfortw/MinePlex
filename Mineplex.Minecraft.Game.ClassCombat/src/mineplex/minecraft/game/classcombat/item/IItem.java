package mineplex.minecraft.game.classcombat.item;

import org.bukkit.Material;

public interface IItem
{
    Material GetType();
    int GetAmount();
    int GetGemCost();
    boolean IsFree();
    int GetSalesPackageId();
    String GetName();
	String[] GetDesc();
}
