package me.chiss.Core.ClientData;

import mineplex.core.common.Rank;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;

public class ClientBanEntry 
{
	public ClientBan _ban;
	public ClientBanEntry(ClientBan ban, String admin, Rank rank, String reason, 
			long banTime, long banDuration, boolean alias, String ip, String mac)
	{
		_ban = ban;
		
		_admin = admin;
		_rank = rank;
		_reason = reason;
		_banTime = banTime;
		_banDuration = banDuration;
		_aliasBan = alias;
		_ip = ip;
		_mac = mac;
	}
	
	private boolean _unbanned = false;
	
	private String _admin = "";
	private Rank _rank = Rank.ALL;
	private String _reason = "";

	private long _banTime = 0;
	private long _banDuration = 0;
	
	private boolean _aliasBan = false;
	private String _ip = "";
	private String _mac = "";
	
	public boolean Active()
	{
		if (_unbanned)
			return false;
		
		if (_banDuration == 0)
			return true;
		
		return UtilTime.elapsed(_banTime, _banDuration);
	}
	
	public boolean Alias()
	{
		return _aliasBan;
	}
	
	public long Remaining() 
	{
		return _banDuration - (System.currentTimeMillis() - _banTime);
	}

	public String GetAdmin() {
		return _admin;
	}

	public String GetReason() {
		return _reason;
	}

	public long GetBanTime() {
		return _banTime;
	}

	public long GetBanDuration() {
		return _banDuration;
	}

	public boolean IsAliasBan() {
		return _aliasBan;
	}

	public String GetIP() {
		return _ip;
	}

	public String GetMac() {
		return _mac;
	}

	public Rank GetRank() {
		return _rank;
	}

	public boolean GetUnbanned() {
		return _unbanned;
	}

	public void SetUnbanned(boolean _unbanned) {
		this._unbanned = _unbanned;
	}

	public String RemainingString() 
	{
		return UtilTime.convertString(Remaining(), 1, TimeUnit.FIT) + " Remaining";
	}
}
