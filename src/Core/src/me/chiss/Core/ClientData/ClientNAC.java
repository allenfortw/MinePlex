package me.chiss.Core.ClientData;

import mineplex.core.account.CoreClient;

public class ClientNAC extends ClientDataBase<Object>
{
	//XXX - Temp
	private long _requireTime = 86400000;
	
	//Loaded at Plugin Load
	private boolean _required = false;
	private String _date = "";
	private String _admin = "";
	private String _reason = "";

	//Loaded on Client Connect
	private boolean _using = false;

	private long _queryTime = 0;

	private String _token = "";

	private String _pack = "";
	private String _packHash = "";

	public ClientNAC(CoreClient client) 
	{
		super(client, "CAH", null);
	}

	@Override
	public void Load() 
	{
		//Load Requirements
	}
	
	@Override 
	public void LoadToken(Object token)
	{
	    
	}

	public void SetRequired(boolean required) 
	{
		_required = required;
	}
	
	public boolean GetRequired()
	{
		return _required;
	}
	
	public long GetRequireTime()
	{
		return _requireTime;
	}

	public boolean IsRequired()
	{
		if (_required)
			return true;

		if (Client.Acc().GetPlayTime() > _requireTime)
			return true;

		return false;
	}
	
	public void SetUsing(boolean using)
	{
		_using = using;
	}

	public boolean IsUsing()
	{
		return _using;
	}

	public void Reset()
	{
		_using = false;

		_queryTime = 0;

		_token = "";

		_pack = "";
		_packHash = "";
	}

	public void SetQueryTime(long time) 
	{
		_queryTime = time;
	}
	
	public long GetQueryTime() 
	{
		return _queryTime;
	}
	
	public String GetPack() 
	{
		return _pack;
	}

	public void SetPack(String pack) 
	{
		_pack = pack;
	}

	public String GetPackHash() 
	{
		return _packHash;
	}

	public void SetPackHash(String packHash) 
	{
		_packHash = packHash;
	}
	
	public boolean HasToken()
	{
		return _token.length() > 0;
	}

	public String GetToken() 
	{
		return _token;
	}

	public void SetToken(String token) 
	{
		_token = token;
	}

	public void SetDate(String date) 
	{
		_date = date;
	}
	
	public String GetDate()
	{
		return _date;
	}

	public void SetAdmin(String admin) 
	{
		_admin = admin;
	}
	
	public String GetAdmin()
	{
		return _admin;
	}

	public void SetReason(String reason) 
	{
		_reason = reason;
	}
	
	public String GetReason()
	{
		return _reason;
	}
	
	public void Save()
	{
		if (!_required)
			return;

		/*
		String statement = "REPLACE INTO client_cah (client, date, admin, reason, systime) VALUES (" + 
				Client.Manager().SQL().f(Client.GetPlayerName()) + ", " + 
				Client.Manager().SQL().f(_date) + ", " + 
				Client.Manager().SQL().f(_admin) + ", " + 
				Client.Manager().SQL().f(_reason) + ", " + 
				System.currentTimeMillis() + ")";

		//Insert
		Client.Manager().SQL().doStatement(statement, DB.REMOTE, true);*/
	}
}
