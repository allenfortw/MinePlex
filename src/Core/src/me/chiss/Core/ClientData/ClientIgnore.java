package me.chiss.Core.ClientData;

import java.util.HashSet;
import java.util.Set;

import nautilus.minecraft.core.webserver.token.Account.ClientIgnoreToken;

import mineplex.core.account.CoreClient;

public class ClientIgnore extends ClientDataBase<ClientIgnoreToken>
{
	private HashSet<String> _ignore = new HashSet<String>();

	public ClientIgnore(CoreClient client) 
	{
		super(client, "Ignore", null);
	}

	@Override
	public void Load() 
	{

	}
	
    @Override 
    public void LoadToken(ClientIgnoreToken token)
    {
    	if (token != null)
        {
    		for (String cur : token.Ignored)
    			_ignore.add(cur);
        }
    }

	public boolean ToggleIgnore(String name)
	{
		if (_ignore.contains(name))
		{
			//Memory
			_ignore.remove(name);

			Save();

			return false;
		}

		//Memory
		_ignore.add(name);

		Save();

		return true;
	}
	
	public void Save()
	{
		//XXX
	}

	public boolean IsIgnored(String name)
	{
		return _ignore.contains(name);
	}

	public Set<String> GetIgnored() 
	{
		return _ignore;
	}
}
