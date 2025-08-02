package me.chiss.Core.PvpShop;

import mineplex.minecraft.game.core.classcombat.item.repository.ItemToken;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.C;
import mineplex.core.itemstack.ItemStackFactory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopItem implements IShopItem, Listener
{
	protected PvpShopFactory Factory;

	private int _salesPackageId = 0;
	private Material _type;
	private byte _data;
	private String _name;
	private String _deliveryName;
	private String[] _desc;
	private int _amount;
	private boolean _free;
	private int _tokenCost;
	private int _creditCost;
	private int _pointCost;
	private int _economyCost;
	private int _slot;
	private boolean _canDamage;
	private float _returnPercent = 0.25f;
	
	public ShopItem(PvpShopFactory factory, String name, String deliveryName, String[] desc, 
			Material type, byte data, int amount, int economyCost, float returnPercent, int slot)
	{
		Factory = factory;
		
		if (name != null)			_name = name;
		else 						_name = ItemStackFactory.Instance.GetName(type, data, false);
		
		if (deliveryName != null)	_deliveryName = name;
		else						_deliveryName = ItemStackFactory.Instance.GetName(type, data, true);
		
		_returnPercent = returnPercent;
		
		if (desc != null)			_desc = desc;
		else						_desc = new String[]{"Left-Click: " + C.cWhite + "Purchase", "Right-Click: " + C.cWhite + "Sell All at " + (int)(_returnPercent * 100) + "%"};
		
		_type = type;
		_data = data;
		_amount = amount;
		_slot = slot;
		_economyCost = economyCost;
		
		_free = true;
	}

	@Override
	public Material GetType()
	{
		return _type;
	}
	
	@Override
	public byte GetData()
	{
		return _data;
	}

	@Override
	public int GetAmount()
	{
		return _amount;
	}

	@Override
	public int GetTokenCost()
	{
		return _tokenCost;
	}

	@Override
	public int GetCreditCost()
	{
		return _creditCost;
	}
	
	@Override
	public int GetPointCost()
	{
		return _pointCost;
	}
	
	@Override
	public int GetEconomyCost()
	{
		return _economyCost;
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
	
	@Override
	public String GetDeliveryName()
	{
		return _deliveryName;
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
		_creditCost = itemToken.SalesPackage.BlueGems;
		_pointCost = itemToken.SalesPackage.GreenGems;
		_free = itemToken.SalesPackage.Free;
	}

	@Override
	public String[] GetDesc() 
	{
		return _desc;
	}
	
	@Override
	public int GetSlot()
	{
		return _slot;
	}
	
	public float GetReturnPercent()
	{
		return _returnPercent;
	}
}
