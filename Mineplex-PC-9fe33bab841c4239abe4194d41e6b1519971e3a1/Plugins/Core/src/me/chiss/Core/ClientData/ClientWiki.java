package me.chiss.Core.ClientData;

import nautilus.minecraft.core.webserver.token.Account.ClientWikiToken;
import me.chiss.Core.Wiki.WikiArticle;
import mineplex.core.account.CoreClient;
import mineplex.core.server.IRepository;

public class ClientWiki extends ClientDataBase<ClientWikiToken>
{
	private int _articlesActive = 0;
	private int _articlesDeprecated = 0;
	private int _articlesDenied = 0;
	
	private WikiArticle _last = null;
	private long _lastTime = 0;
	
	private String _lastBlock;
	
	public ClientWiki(CoreClient client) 
	{
		super(client, "Wiki", null);
	}
	
    public ClientWiki(CoreClient client, IRepository repository, ClientWikiToken token)
    {
        super(client, "Wiki", repository, token);
    }   
	
	@Override
	public void Load() 
	{
		
	}

    @Override 
    public void LoadToken(ClientWikiToken token)
    {
        if (token == null)
            return;
        
        _articlesActive = token.ArticlesActive;
        _articlesDeprecated = token.ArticlesDeprecated;
        _articlesDenied = token.ArticlesDenied;
    }

	public WikiArticle GetLastArticle() 
	{
		return _last;
	}

	public void SetLastArticle(WikiArticle last) 
	{
		_last = last;
	}

	public long GetLastTime() 
	{
		return _lastTime;
	}

	public void SetLastTime(long lastTime) 
	{
		_lastTime = lastTime;
	}

	public String GetLastBlock() 
	{
		return _lastBlock;
	}

	public void SetLastBlock(String lastBlock) 
	{
		_lastBlock = lastBlock;
	}

	public int GetActive() 
	{
		return _articlesActive;
	}
	
	public int GetDeprecated() 
	{
		return _articlesDeprecated;
	}
	
	public int GetDenied() 
	{
		return _articlesDenied;
	}

	public void RegisterArticle(WikiArticle article) 
	{
		if (article.GetRevision() > 0)
		{
			if (article.IsActive())
			    _articlesActive++;
			else
			    _articlesDeprecated++;
		}
		else
		    _articlesDenied++;	
	}
}
