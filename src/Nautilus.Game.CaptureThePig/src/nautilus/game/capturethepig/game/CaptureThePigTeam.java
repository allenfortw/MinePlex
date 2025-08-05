package nautilus.game.capturethepig.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;

import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.game.Team;

public class CaptureThePigTeam extends Team<ICaptureThePigPlayer, ICaptureThePigTeam> implements ICaptureThePigTeam
{
	private Region _pigPen;
	private Pig _pig;
	
	private boolean _hasPig = false;
	
	public CaptureThePigTeam(TeamType teamType)
	{
		super(teamType);
	}
	   
	@Override
	public void AddPlayer(ICaptureThePigPlayer player)
	{
		player.SetTeam(this);
		Players.add(player);
	}

	@Override
	public Region GetPigPen()
	{
		return _pigPen;
	}

	@Override
	public void SetPigPen(Region pigPen) 
	{
		_pigPen = pigPen;
	}

	@Override
	public void CapturePig(Entity pig) 
	{
		Vector mid = _pigPen.GetMidPoint();
		pig.teleport(new Location(pig.getWorld(), mid.getBlockX() + .5, _pigPen.GetMinimumPoint().getBlockY(), mid.getBlockZ() + .5));
		_pig.teleport(new Location(pig.getWorld(), mid.getBlockX() + .5, _pigPen.GetMinimumPoint().getBlockY(), mid.getBlockZ() + .5));
	}

	@Override
	public boolean HasPig() 
	{
		return _hasPig;
	}
	
	@Override
	public void ReturnPig()
	{
		_hasPig = true;
		Vector mid = _pigPen.GetMidPoint();
		_pig.teleport(new Location(_pig.getWorld(), mid.getBlockX() + .5, _pigPen.GetMinimumPoint().getBlockY(), mid.getBlockZ() + .5));
	}

	@Override
	public Entity RemovePig()
	{
		_hasPig = false;
		return _pig;
	}

	@Override
	public void SpawnPig(Pig pig)
	{
		_hasPig = true;
		_pig = pig;
		Vector mid = _pigPen.GetMidPoint();
		_pig.teleport(new Location(pig.getWorld(), mid.getBlockX() + .5, _pigPen.GetMinimumPoint().getBlockY(), mid.getBlockZ() + .5));
		_pig.setCustomName(Enum.valueOf(ChatColor.class, GetTeamType().toString().toUpperCase()) + GetTeamType().toString());
		_pig.setCustomNameVisible(true);
	}
	
	@Override
	public Pig GetPig()
	{
		return _pig;
	}
}
