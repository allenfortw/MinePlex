package me.chiss.Core.Modules;

import org.bukkit.entity.Player;

public class QuitDataQuit
{
	//Quit
	private boolean _offline = false;
	private long _quitTime = 0;
	private int _quitCount = 0;
	
	private Player _player = null;
	
	public QuitDataQuit(Player player)
	{
		SetOffline(true);
		SetPlayer(player);
		_quitTime = System.currentTimeMillis();
	}
	
	public long GetQuitTime() {
		return _quitTime;
	}

	public void SetQuitTime(long _quitTime) {
		this._quitTime = _quitTime;
	}

	public boolean IsOffline() {
		return _offline;
	}

	public boolean SetOffline(boolean offline) 
	{
		this._offline = offline;
		
		_quitTime = System.currentTimeMillis();
		
		if (offline)
		{	
			_quitCount++;
			
			if (_quitCount >= 3)
				return true;
		}
		
		return false;
	}

	public int GetCount() 
	{
		return _quitCount;
	}

	public Player GetPlayer() {
		return _player;
	}

	public void SetPlayer(Player _quitRef) {
		this._player = _quitRef;
	}
}
