package mineplex.minecraft.game.core.condition;

import mineplex.core.itemstack.ItemStackFactory;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ConditionIndicator implements Listener
{	
	private Entity _indicator;
	private Condition _condition;

	public ConditionIndicator(Condition condition)
	{
		SetCondition(condition);
	}

	public Condition GetCondition()
	{
		return _condition;
	}

	public Entity GetIndicator()
	{
		if (!IsVisible())
			return null;

		if (_indicator == null)
			_indicator = _condition.GetEnt().getWorld().dropItem(_condition.GetEnt().getEyeLocation(), 
					ItemStackFactory.Instance.CreateStack(_condition.GetIndicatorMaterial(), _condition.GetIndicatorData()));

		return _indicator;
	}

	public void SetCondition(Condition newCon) 
	{
		_condition = newCon;
		
		if (_indicator != null)
			if (_indicator instanceof Item)
				((Item)_indicator).getItemStack().setType(newCon.GetIndicatorMaterial());

		newCon.Apply();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Pickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_indicator != null)
			if (_indicator.equals(event.getItem()))
				event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_indicator != null)
			if (_indicator.equals(event.getItem()))
				event.setCancelled(true);
	}
	
	public boolean IsVisible()
	{
		return GetCondition().IsVisible();
	}
}
