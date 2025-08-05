package me.chiss.Core.Scheduler;

import java.util.Calendar;
import java.util.HashMap;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.TimeSpan;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler extends MiniPlugin
{
	public static Scheduler Instance;

	private HashMap<IScheduleListener, Long> _listenerMap;
	private long _startOfDay = 0;
	
	public static void Initialize(JavaPlugin plugin)
	{
		if (Instance == null)
			Instance = new Scheduler(plugin);
	}
	
	private Scheduler(JavaPlugin plugin)
	{
		super("Scheduler", plugin);
		
		_listenerMap = new HashMap<IScheduleListener, Long>();
		
		Calendar calender = Calendar.getInstance();
		   
		_startOfDay = System.currentTimeMillis() - calender.get(Calendar.HOUR_OF_DAY) * TimeSpan.HOUR - calender.get(Calendar.MINUTE) * TimeSpan.MINUTE - calender.get(Calendar.SECOND) * TimeSpan.SECOND - calender.get(Calendar.MILLISECOND);
	}

	@Override
	public void AddCommands() 
	{
		AddCommand(new ForceDailyCommand(this));
	}

	@EventHandler
	public void UpdateDaily(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		for (IScheduleListener listener : _listenerMap.keySet())
		{
			if (_listenerMap.get(listener) < TimeSpan.DAY && _listenerMap.get(listener) + _startOfDay <= System.currentTimeMillis())
			{
				listener.AppointmentFire();
				_listenerMap.put(listener, _listenerMap.get(listener) + TimeSpan.DAY);
			}
		}
		
		if (System.currentTimeMillis() - _startOfDay >= TimeSpan.DAY)
			ResetStartOfDay();
	}

	public void ScheduleDailyRecurring(IScheduleListener listener, long offsetFromDay)
	{
		long time = (TimeSpan.DAY + offsetFromDay) % TimeSpan.DAY;
		
		_listenerMap.put(listener, time);
		
		if (_listenerMap.get(listener) + _startOfDay <= System.currentTimeMillis())
			_listenerMap.put(listener, _listenerMap.get(listener) + TimeSpan.DAY);
	}
	
	public void ResetStartOfDay()
	{
		for (IScheduleListener listener : _listenerMap.keySet())
		{
			if (_listenerMap.get(listener) >= TimeSpan.DAY)
			{
				_listenerMap.put(listener, _listenerMap.get(listener) - TimeSpan.DAY);
			}
		}
		
		_startOfDay = System.currentTimeMillis();
	}

	public long GetTimeTilNextAppt(IScheduleListener listener)
	{
		return _listenerMap.get(listener) + _startOfDay - System.currentTimeMillis();
	}
}
