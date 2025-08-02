package me.chiss.Core.Modules;

import org.bukkit.Location;

public class QuitDataLog 
{
	//Log Timer
	private int _logTime = 0;
	private long _logLast = 0;
	private Location _logLoc = null;
	
	public QuitDataLog(int time, Location loc)
	{
		_logTime = time;
		_logLast = System.currentTimeMillis();
		_logLoc = loc;
	}
	
	public int GetLogTime() {
		return _logTime;
	}

	public void SetLogTime(int _logTime) {
		this._logTime = _logTime;
	}

	public long GetLogLast() {
		return _logLast;
	}

	public void SetLogLast(long _logLast) {
		this._logLast = _logLast;
	}

	public Location GetLogLoc() {
		return _logLoc;
	}

	public void SetLogLoc(Location _logLoc) {
		this._logLoc = _logLoc;
	}
}
