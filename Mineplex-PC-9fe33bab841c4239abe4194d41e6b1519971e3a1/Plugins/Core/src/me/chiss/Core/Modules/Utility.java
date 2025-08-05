package me.chiss.Core.Modules;

import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import me.chiss.Core.Utility.*;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilGear;

public class Utility extends AModule
{
	public Utility(JavaPlugin plugin) 
	{
		super("Utility", plugin);
	}
	
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{
		
	}

	@Override
	public void commands() 
	{
	
	}

	@Override
	public void command(org.bukkit.entity.Player caller, String cmd, String[] args) 
	{
	
	}

	private UtilAccount _utilAccount;
	private UtilEvent _utilEvent;
	private UtilGear _utilGear;
	private UtilInput _utilInput;
	private UtilItem _utilItem;
	
	public UtilAccount Acc() 
	{
		if (_utilAccount == null)
			_utilAccount = new UtilAccount(this);
		
		return _utilAccount;
	}
	
	public UtilEvent Event() 
	{
		if (_utilEvent == null)
			_utilEvent = new UtilEvent(this);
		
		return _utilEvent;
	}
	
	public UtilGear Gear() 
	{
		if (_utilGear == null)
			_utilGear = new UtilGear(this);
		
		return _utilGear;
	}
	
	public UtilInput Input() 
	{
		if (_utilInput == null)
			_utilInput = new UtilInput(this);
		
		return _utilInput;
	}
	
	public UtilItem Items() 
	{
		if (_utilItem == null)
			_utilItem = new UtilItem(this);
		
		return _utilItem;
	}
}
