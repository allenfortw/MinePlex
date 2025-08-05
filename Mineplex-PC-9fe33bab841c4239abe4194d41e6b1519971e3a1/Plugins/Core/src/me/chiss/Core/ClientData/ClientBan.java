package me.chiss.Core.ClientData;

import mineplex.core.account.CoreClient;
import mineplex.core.server.IRepository;
import mineplex.minecraft.punish.Tokens.PunishToken;

public class ClientBan extends ClientDataBase<PunishToken>
{
	private PunishToken _activeBan;
	
	public ClientBan(CoreClient client, IRepository repository) 
	{
		super(client, "Ban", repository);
		//_bans = new LinkedList<ClientBanEntry>();
	}
	
    public ClientBan(CoreClient client, IRepository repository, PunishToken banToken) 
    {
        super(client, "Ban", repository, banToken);
        //_bans = new LinkedList<ClientBanEntry>();
    }
	
	@Override
	public void Load() 
	{

	}
	
	@Override
	public void LoadToken(PunishToken banToken)
	{
		_activeBan = banToken;
	}
	
	public boolean IsBanned()
	{ 
		return _activeBan != null;
	}
	
	public ClientBanEntry GetBan()
	{
		/*
		for (ClientBanEntry cur : _bans)
			if (cur.Active())
				return cur;
		
		*/
		return null;
	}

	public String Reason()  
	{
		String reason = "Unknown";
		
		if (_activeBan != null)
		{
			//reason = C.consoleBody + _activeBan.Reason + C.consoleFill + " - " + C.consoleBody + "Remaining : " + C.cGreen + UtilTime.convertString((long)(_activeBan.Hours * 3600000), 1, TimeUnit.FIT) + ".";
		}
		
		return "Banned > " + reason;
	}
}
