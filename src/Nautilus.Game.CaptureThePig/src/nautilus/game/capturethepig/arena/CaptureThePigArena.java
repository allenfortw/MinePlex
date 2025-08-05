package nautilus.game.capturethepig.arena;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import nautilus.game.core.arena.Region;
import nautilus.game.core.arena.TeamArena;

public class CaptureThePigArena extends TeamArena implements ICaptureThePigArena 
{
	private Region _redPigPen;
	private Region _bluePigPen;
	
	private Location _pigSpawnPoint;
	
	public CaptureThePigArena(String name) 
	{
		super(name);
	}

	@Override
	public Region GetRedPigPen() 
	{
		return _redPigPen;
	}

	@Override
	public Region GetBluePigPen() 
	{
		return _bluePigPen;
	}

	@Override
	public Location GetPigSpawnPoint() 
	{
		return _pigSpawnPoint;
	}

	@Override
	public void SetRedPigPen(Region pen) 
	{
		Regions.add(pen);
		_redPigPen = pen;
	}

	@Override
	public void SetBluePigPen(Region pen) 
	{
		Regions.add(pen);
		_bluePigPen = pen;
	}

	@Override
	public void SetPigSpawnPoint(Vector location) 
	{
		_pigSpawnPoint = new Location(World, location.getX(), location.getY(), location.getZ());
	}
}
