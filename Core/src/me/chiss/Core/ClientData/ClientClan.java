package me.chiss.Core.ClientData;

import java.util.HashMap;

import nautilus.minecraft.core.webserver.token.Account.ClientClanToken;

import me.chiss.Core.Clans.ClansUtility.ClanRelation;
import mineplex.core.account.CoreClient;
import mineplex.core.server.IRepository;

public class ClientClan extends ClientDataBase<ClientClanToken>
{
	//Data
	private String _clanName = "";
	private String _inviter = "";
	private long _delay = 0;
	
	//Temp - Territory
	private boolean _safe = false;
	private boolean _mapOn = false;
	private String _territory = "";
	private String _owner = "";
	private boolean _clanChat = false;
	private boolean _allyChat = false;
	
	//Colors
	private HashMap<String, ClanRelation> _relations = new HashMap<String, ClanRelation>();
	
	
	//Admin
	private String mimic = "";
	private boolean autoClaim = false;
	
	public ClientClan(CoreClient client, IRepository repository) 
	{
		super(client, "Clan", repository);
	}

	@Override
	public void Load() 
	{
		//Load Clients
	}
	
    @Override 
    public void LoadToken(ClientClanToken token)
    {
    	if (token != null)
        {
    		_clanName = token.Name;
        }
    }
	
	public void Update(String clanName, String inviter, long delay) 
	{
		_clanName = clanName;
		_inviter = inviter;
		_delay = delay;
	}
	
	public boolean CanJoin()
	{
		if (System.currentTimeMillis() > _delay) 
			return true;
		
		return false;
	}
	
	public boolean InClan()
	{
		return (!_clanName.equals(""));
	}
	
	public void Reset()
	{
		_clanName = "";
		_inviter = "";
	}

	public String GetClanName() {
		return _clanName;
	}

	public void SetClan(String _clanName) {
		this._clanName = _clanName;
	}

	public String GetInviter() {
		return _inviter;
	}

	public void SetInviter(String _inviter) {
		this._inviter = _inviter;
	}

	public long GetDelay() {
		return _delay;
	}

	public void SetDelay(long _delay) {
		this._delay = _delay;
	}

	public boolean IsSafe() {
		return _safe;
	}

	public void SetSafe(boolean _safe) {
		this._safe = _safe;
	}

	public boolean IsMapOn() {
		return _mapOn;
	}

	public void SetMapOn(boolean _mapOn) {
		this._mapOn = _mapOn;
	}

	public String GetTerritory() {
		return _territory;
	}

	public void SetTerritory(String _territory) {
		this._territory = _territory;
	}

	public String GetOwner() {
		return _owner;
	}

	public void SetOwner(String _owner) {
		this._owner = _owner;
	}

	public boolean IsClanChat() {
		return _clanChat;
	}

	public void SetClanChat(boolean enabled) 
	{
		this._clanChat = enabled;
		
		if (enabled)
			_allyChat = false;
	}
	
	public boolean IsAllyChat() {
		return _allyChat;
	}

	public void SetAllyChat(boolean enabled) 
	{
		this._allyChat = enabled;
		
		if (enabled)
			_clanChat = false;
	}

	public String GetMimic() {
		return mimic;
	}

	public void SetMimic(String mimic) {
		this.mimic = mimic;
	}
	
	public boolean IsAutoClaim()
	{
		return autoClaim;
	}
	
	public void SetAutoClaim(boolean claim) {
		this.autoClaim = claim;
	}
	
	public void SetRelationship(String player, ClanRelation relation)
	{
		_relations.put(player, relation);
	}
	
	public ClanRelation GetRelation(String player)
	{
		return _relations.get(player);
	}
}
