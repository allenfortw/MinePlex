package me.chiss.Core.ClientData;

import org.bukkit.Location;

import mineplex.core.account.CoreClient;

public class ClientPlayer extends ClientDataBase<Object>
{
	private long _lastAction = 0;
	private long _lastGrounded = 0;
	private long _lastMovement = 0;
	private long _lastDamagee = 0;
	private long _lastDamager = 0;
	private long _lastEnergy = 0;
	
	private Location _lastLocation = null;
	
	public ClientPlayer(CoreClient client) 
	{
		super(client, "Player", null);
	}
	
	@Override
	public void Load() 
	{
		
	}
	
	@Override
	protected void LoadToken(Object token) 
	{
		
	}

	public long GetLastAction() {
		return _lastAction;
	}

	public void SetLastAction(long _lastAction) {
		this._lastAction = _lastAction;
	}

	public long GetLastGrounded() {
		return _lastGrounded;
	}

	public void SetLastGrounded(long _lastGrounded) {
		this._lastGrounded = _lastGrounded;
	}

	public long GetLastMovement() {
		return _lastMovement;
	}

	public void SetLastMovement(long _lastMovement) {
		this._lastMovement = _lastMovement;
	}

	public Location GetLastLocation() {
		return _lastLocation;
	}

	public void SetLastLocation(Location _lastLocation) {
		this._lastLocation = _lastLocation;
	}

	public long GetLastDamagee()
	{
		return _lastDamagee;
	}

	public void SetLastDamagee(long _lastDamaged) {
		this._lastDamagee = _lastDamaged;
	}
	
	public long GetLastDamager()
	{
		return _lastDamager;
	}

	public void SetLastDamager(long _lastDamaged) {
		this._lastDamager = _lastDamaged;
	}
	
	public long GetLastCombat()
	{
		if (_lastDamager > _lastDamagee)
			return _lastDamager;
		return _lastDamagee;
	}

	public void SetLastEnergy(long time) 
	{
		_lastEnergy = time;
	}
	
	public long GetLastEnergy()
	{
		return _lastEnergy;
	}
}
