package me.chiss.Core.ClientData;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.server.IRepository;

public abstract class ClientDataBase<TokenType> implements IClientDataLoad
{
	protected CoreClient Client;
	protected String DataName;
	protected IRepository Repository;
	
	public ClientDataBase(CoreClient client, String dataName, IRepository repository)
	{
        Client = client;
        DataName = dataName;
        Repository = repository;
        
	    Load();
	}
	
    public ClientDataBase(CoreClient client, String dataName, IRepository repository, TokenType token)
    {
        Client = client;
        DataName = dataName;
        Repository = repository;
        
        LoadToken(token);
    }
	
	public String GetDataName()
	{
		return DataName;
	}
	
	protected abstract void LoadToken(TokenType token);
	
	public CoreClientManager Manager()
	{
		return Client.Manager();
	}
}
