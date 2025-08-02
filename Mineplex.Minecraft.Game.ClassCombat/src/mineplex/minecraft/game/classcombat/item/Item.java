package mineplex.minecraft.game.classcombat.item;

import mineplex.core.common.util.UtilGear;
import mineplex.minecraft.game.classcombat.item.repository.ItemToken;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Item implements IItem, Listener
{
	protected ItemFactory Factory;

	private int _salesPackageId;
	private Material _type;
	private String _name;
	private String[] _desc;
	private int _amount;
	private boolean _free;
	private int _gemCost;
	private boolean _canDamage;

	public Item(ItemFactory factory, int salesPackageId, String name, String[] desc, Material type, int amount, boolean canDamage, int gemCost)
	{
		Factory = factory;
		_salesPackageId = salesPackageId;
		_name = name;
		_desc = desc;
		_type = type;
		_amount = amount;
		_gemCost = gemCost;
		_canDamage = canDamage;
	}

	@Override
	public Material GetType()
	{
		return _type;
	}

	@Override
	public int GetAmount()
	{
		return _amount;
	}

	@Override
	public int GetGemCost()
	{
		return _gemCost;
	}

	@Override
	public int GetSalesPackageId()
	{
		return _salesPackageId;
	}

	@Override
	public String GetName()
	{
		return _name;
	}
	
	@EventHandler
	public void Damage(CustomDamageEvent event)
	{
		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;
		
		if (!UtilGear.isMat(damager.getItemInHand(), GetType()))
			return;
		
		if (!_canDamage)
			event.SetCancelled("Item Damage Cancel");
	}
	
	@Override
	public boolean IsFree()
	{
		return _free;
	}

	public void Update(ItemToken itemToken) 
	{
		_salesPackageId = itemToken.SalesPackage.GameSalesPackageId;
		_gemCost = itemToken.SalesPackage.Gems;
		_free = itemToken.SalesPackage.Free;
	}

	@Override
	public String[] GetDesc() 
	{
		return _desc;
	}
}
