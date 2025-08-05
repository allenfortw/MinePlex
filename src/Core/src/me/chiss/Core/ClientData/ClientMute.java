package me.chiss.Core.ClientData;


import org.bukkit.entity.Player;

import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.minecraft.punish.Tokens.PunishToken;

public class ClientMute extends ClientDataBase<PunishToken>
{
	private String _admin = "";
	private String _reason = "";
	private Rank _rank = Rank.ALL;

	private long _muteTime = 0;
	private long _muteDuration = -1;
	
	public ClientMute(CoreClient client) 
	{
		super(client, "Mute", null);
	}
	
	@Override
	public void Load() 
	{
		
	}
	
    @Override 
    public void LoadToken(PunishToken token)
    {
		SetAdmin(token.Admin);
		//SetMuteDuration((long)(3600000 * token.Hours));
		SetMuteTime(System.currentTimeMillis());
		SetReason(token.Reason);
    }

	public void DisplayData(Player player) 
	{
		if (IsMuted())
		{
			player.sendMessage(F.value("Muted", "True", true));

			player.sendMessage(F.value(1, "Date", UtilTime.when(_muteTime)));
			player.sendMessage(F.value(1, "Duration", UtilTime.when(_muteDuration)));
			if (_muteTime != 0)
				player.sendMessage(F.value(1, "Remaining", UtilTime.convertString(GetRemaining(), 1, TimeUnit.FIT)));
			player.sendMessage(F.value(1, "Admin", _admin));
			player.sendMessage(F.value(1, "Reason", _reason));	
		}
		else
		{
			player.sendMessage(F.value("Muted", "False", false));		
		}
	}
	
	public boolean IsMuted()
	{
		if (_muteDuration == -1)
			return false;
		
		if (_muteDuration == 0)
			return true;
		
		return !UtilTime.elapsed(_muteTime, _muteDuration);
	}
	
	public long GetRemaining() 
	{
		return _muteDuration - (System.currentTimeMillis() - _muteTime);
	}

	public String GetAdmin() {
		return _admin;
	}

	public void SetAdmin(String admin) {
		this._admin = admin;
	}

	public String GetReason() {
		return _reason;
	}

	public void SetReason(String reason) {
		this._reason = reason;
	}

	public long GetMuteTime() {
		return _muteTime;
	}

	public void SetMuteTime(long muteTime) {
		_muteTime = muteTime;
	}

	public long GetMuteDuration() {
		return _muteDuration;
	}

	public void SetMuteDuration(long muteDuration) {
		_muteDuration = muteDuration;
	}

	public Rank GetRank() {
		return _rank;
	}

	public void SetRank(Rank rank) {
		_rank = rank;
	}
}
