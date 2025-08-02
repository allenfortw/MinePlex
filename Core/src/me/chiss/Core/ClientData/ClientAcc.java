package me.chiss.Core.ClientData;

import java.util.HashSet;

import nautilus.minecraft.core.webserver.token.Account.AccountToken;

import mineplex.core.account.CoreClient;
import mineplex.core.server.IRepository;

public class ClientAcc extends ClientDataBase<AccountToken>
{
	private long _playTime = 0;

	private long _loginLast = 0;
	private int _loginCount = 0;

	private String _ip = "Unknown";
	private String _mac = "Unknown";

	private HashSet<String> _listIP = new HashSet<String>();
	private HashSet<String> _listMAC = new HashSet<String>();

	private HashSet<String> _aliasIP = new HashSet<String>();
	private HashSet<String> _aliasMAC = new HashSet<String>();

	public ClientAcc(CoreClient client, IRepository repository) 
	{
		super(client, "Account", repository);
	}
	
    public ClientAcc(CoreClient client, IRepository repository, AccountToken token) 
    {
        super(client, "Account", repository, token);
    }

	@Override
	public void Load() 
	{

	}

	@Override 
	public void LoadToken(AccountToken token)
	{
	    _playTime = token.TotalPlayingTime;
	    _loginLast = token.LastLogin;
	    _loginCount = token.LoginCount;
	    
	    _listIP = new HashSet<String>();
	    _listMAC = new HashSet<String>();

	    _aliasIP = new HashSet<String>();
	    _aliasMAC = new HashSet<String>();
	    
	    _listIP.addAll(token.IpAddresses);
	    _listMAC.addAll(token.MacAddresses);
	    _aliasIP.addAll(token.IpAliases);
	    _aliasMAC.addAll(token.MacAliases);
	}
	
	public void SetIP(String ip)
	{
		_ip = ip;
	}
	
	public String GetIP()
	{
		return _ip;
	}
	
	public void SetMac(String mac)
	{
		_mac = mac;
	}
	
	public String GetMac()
	{
		return _mac;
	}

	public long GetPlayTime() 
	{
		return _playTime;
	}
	
	public long GetLoginLast()
	{
		if (_loginLast == 0)
			_loginLast = System.currentTimeMillis();
		
		return _loginLast;
	}
	
	public void SetLoginLast(long loginLast)
	{
		_loginLast = loginLast;
	}
	
	public int GetLoginCount()
	{
		return _loginCount;
	}

	public HashSet<String> GetListIP()
	{
		return _listIP;
	}
	
	public HashSet<String> GetListMAC()
	{
		return _listMAC;
	}
	
	public HashSet<String> GetAliasIP()
	{
		return _aliasIP;
	}
	
	public HashSet<String> GetAliasMAC()
	{
		return _aliasMAC;
	}
}
