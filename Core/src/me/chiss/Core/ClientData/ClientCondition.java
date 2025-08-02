package me.chiss.Core.ClientData;

import java.util.HashMap;


import org.bukkit.potion.PotionEffectType;

import mineplex.core.account.CoreClient;
import mineplex.core.server.IRepository;

public class ClientCondition extends ClientDataBase<String>
{
	private HashMap<PotionEffectType, ClientConditionEntry> _conditions;
	
	public ClientCondition(CoreClient client, IRepository repository) 
	{
		super(client, "Condition", repository);
	}
	
    public ClientCondition(CoreClient client, IRepository repository, String token) 
    {
        super(client, "Condition", repository, token);
    }
	
	@Override
	public void Load() 
	{
	    
	}

    @Override 
    public void LoadToken(String token)
    {
        
    }	

	public HashMap<PotionEffectType, ClientConditionEntry> get_conditions() {
		return _conditions;
	}

	public void set_conditions(HashMap<PotionEffectType, ClientConditionEntry> _conditions) {
		this._conditions = _conditions;
	}
}
