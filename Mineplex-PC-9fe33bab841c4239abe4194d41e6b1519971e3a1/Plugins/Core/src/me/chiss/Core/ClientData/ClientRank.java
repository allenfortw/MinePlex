package me.chiss.Core.ClientData;

import mineplex.core.server.IRepository;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;

public class ClientRank extends ClientDataBase<String>
{	
	private Rank _rank;
	
	public ClientRank(CoreClient client, IRepository repository) 
	{
		super(client, "Rank", repository);
	}
	
    public ClientRank(CoreClient client, IRepository repository, String token) 
    {
        super(client, "Rank", repository, token);
    }
	
	@Override
	public void Load() 
	{
	    _rank = Rank.ALL;
	}

    @Override 
    public void LoadToken(String token)
    {
        _rank = Rank.valueOf(token);
    }	

	public Rank GetRank()
	{
		return _rank;
	}
	
	public boolean Has(Rank rank, boolean inform) 
	{
		if (Client.GetPlayer() != null)
			if (Client.GetPlayer().isOp())
				return true;
		
		if (_rank.compareTo(rank) <= 0)
		{
			return true;
		}
		
		if (inform)
		{
			UtilPlayer.message(Client.GetPlayer(), C.mHead + "Permissions > " + 
					C.mBody + "This requires Permission Rank [" + 
					C.mHead + rank +
					C.mBody + "].");
			System.out.println("Rank: " + _rank + " compareTo:" + _rank.compareTo(rank));
		}
		
		return false;
	}
}
